package yu.likelion14th.allligo_was.fastapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.content.entity.ContentStatus;
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionImage;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionImageRepository;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiGenerateReqDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadReqDto;

import java.time.LocalDateTime;
import java.util.List;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionTagRepository;
import yu.likelion14th.allligo_was.domains.store.repository.StoreRepository;
import yu.likelion14th.allligo_was.domains.store.entity.Store;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionTag;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadResponseDto;
import yu.likelion14th.allligo_was.fastapi.dto.TopPerformerDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastapiScheduler {

    private final FastapiClientService fastapiClientService;
    private final PromotionExecutionRepository executionRepository;
    private final ContentRepository contentRepository;
    private final PromotionTagRepository promotionTagRepository;
    private final StoreRepository storeRepository;
    private final PromotionImageRepository promotionImageRepository;
    private final TransactionTemplate transactionTemplate;

    // 예약 시각(executedAt)이 지난 뒤에도 생성 요청·유튜브 업로드를 이어서 시도하는 시간(분).
    // 업로드가 길어져 다음 분 회차를 건너뛰거나 Was 가 재시작돼도 이 안에서 다시 처리하고, 업로드 실패도 매분 재시도한다.
    // 넘기면 실행을 FAILED 로 정리한다(Agent 재시작으로 웹훅이 오지 않는 PROCESSING 포함).
    private static final long LATE_TOLERANCE_MINUTES = 10;

    @Value("${app.backend.base-url:http://localhost:8080}")
    private String baseUrl;

    // 매분 0초에 실행
    // 트랜잭션은 실행 1건 단위로 짧게 잡는다. Agent 호출·유튜브 업로드 중에 DB 트랜잭션(행 잠금)을 쥐고 있으면
    // 그 사이 도착한 웹훅 처리가 막히거나, 웹훅이 저장한 상태를 스케줄러 커밋이 덮어쓴다.
    @Scheduled(cron = "0 * * * * *")
    public void executeTwoTrackScheduler() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        
        // 기존 로직: 1시간 전 생성 요청 (운영용)
        // LocalDateTime oneHourLater = now.plusHours(1);
        
        // 테스트 로직: 5분 전 생성 요청 (테스트용, 테스트 완료 후 위 주석 해제 및 본 줄 삭제)
        LocalDateTime oneHourLater = now.plusMinutes(5);

        LocalDateTime lateLimit = now.minusMinutes(LATE_TOLERANCE_MINUTES);

        log.info("Two-Track Scheduler Running... now: {}, oneHourLater: {}", now, oneHourLater);

        // 지연 허용 시간을 넘긴 실행 정리
        expireOverdueExecutions(lateLimit);

        // Track A (T - 1시간): 영상 생성 요청. 놓친 회차도 지연 허용 시간 안이면 요청
        List<Long> pendingExecutionIds = executionRepository.findAllByStatusAndExecutedAtBetween(
                "PENDING", lateLimit, oneHourLater.plusSeconds(59))
                .stream().map(PromotionExecution::getExecutionId).toList();

        for (Long executionId : pendingExecutionIds) {
            try {
                requestGeneration(executionId);
            } catch (Exception e) {
                log.error("Track A failed for execution ID: {}", executionId, e);
            }
        }

        // Track B (T - 0시간): 유튜브 업로드 요청. 실패·지연분은 지연 허용 시간 안에서 매분 재시도
        // (Agent 가 같은 localVideoPath 재요청은 다시 올리지 않고 처음 URL 을 돌려주므로 중복 업로드 없음)
        List<Long> uploadExecutionIds = executionRepository.findUploadTargetIds(lateLimit, now.plusSeconds(59));

        for (Long executionId : uploadExecutionIds) {
            try {
                requestUpload(executionId);
            } catch (Exception e) {
                log.error("Track B failed for execution ID: {}", executionId, e);
            }
        }
    }

    private void expireOverdueExecutions(LocalDateTime lateLimit) {
        // 하루 넘은 기록은 건드리지 않는다 (예전 데이터 일괄 변경 방지)
        LocalDateTime from = lateLimit.minusDays(1);
        Integer pending = transactionTemplate.execute(status -> executionRepository.expireByStatus(
                "PENDING", from, lateLimit, "예약 시각 경과: 생성 요청을 보내지 못함"));
        Integer processing = transactionTemplate.execute(status -> executionRepository.expireByStatus(
                "PROCESSING", from, lateLimit, "생성 결과 미수신: Agent 응답 시간 초과"));
        Integer notUploaded = transactionTemplate.execute(status -> executionRepository.expireNotUploaded(
                from, lateLimit, "유튜브 업로드 실패: 지연 허용 시간 초과"));

        if (isPositive(pending) || isPositive(processing) || isPositive(notUploaded)) {
            log.warn("Expired overdue executions. pending: {}, processing: {}, notUploaded: {}", pending, processing, notUploaded);
        }
    }

    private boolean isPositive(Integer count) {
        return count != null && count > 0;
    }

    private void requestGeneration(Long executionId) {
        // 1. PROCESSING 저장과 요청 본문 조립을 먼저 커밋 (웹훅이 일찍 와도 커밋된 실행을 보도록)
        FastapiGenerateReqDto reqDto = transactionTemplate.execute(status -> {
            PromotionExecution execution = executionRepository.findById(executionId).orElse(null);
            if (execution == null || !"PENDING".equals(execution.getStatus())) {
                return null;
            }
            log.info("Track A: Requesting content generation for execution ID: {}", executionId);
            execution.setStatus("PROCESSING");
            return buildGenerateRequest(execution);
        });
        if (reqDto == null) {
            return;
        }

        // 2. Agent 호출은 트랜잭션 밖에서
        try {
            FastapiContentResponseDto response = fastapiClientService.generateContent(reqDto);
            if (response != null && response.getTaskId() != null) {
                // 3. taskId 만 저장. 그 사이 웹훅이 바꾼 상태(SUCCESS/FAILED)는 덮어쓰지 않음
                transactionTemplate.executeWithoutResult(status ->
                        executionRepository.updateTaskId(executionId, response.getTaskId()));
                log.info("Track A generation requested successfully. Task ID: {}", response.getTaskId());
            }
        } catch (Exception e) {
            log.error("Track A generation request failed for execution ID: {}", executionId, e);
            transactionTemplate.executeWithoutResult(status ->
                    executionRepository.markFailedIfProcessing(executionId, e.getMessage()));
        }
    }

    private FastapiGenerateReqDto buildGenerateRequest(PromotionExecution execution) {
        Promotion promotion = execution.getPromotion();
        PromotionSchedule schedule = execution.getPromotionSchedule();

        // TODO: Promotion과 PromotionTag에서 실제 분위기태그, 해시태그 추출 (현재는 임시값 또는 기본값 처리)
        FastapiGenerateReqDto reqDto = new FastapiGenerateReqDto();

        // 기본 매핑
        reqDto.setMoodTag("밝은, 쾌활한");
        reqDto.setHashTag("#마케팅 #이벤트");
        reqDto.setPrompt(promotion != null ? promotion.getPrompt() : "");
        // 항상 TRANSFORM으로 설정하여 업로드된 이미지를 LLaVA로 분석하고, 이를 바탕으로 SDXL로 AI 이미지를 새로 생성하게 합니다.
        reqDto.setMode("TRANSFORM");
        
        if (schedule != null) {
            reqDto.setUploadDay(schedule.getDayOfWeek());
            reqDto.setUploadTime(schedule.getPublishTime() != null ? schedule.getPublishTime().toLocalTime().toString() : "morning");
            reqDto.setScheduleId(String.valueOf(schedule.getScheduleId())); // String 변환
        }

        if (promotion != null) {
            // 1. S3 이미지 URL 설정 (null 방어)
            List<PromotionImage> promotionImages = promotionImageRepository.findAllByPromotion(promotion);
            List<String> urls = promotionImages != null ? promotionImages.stream().map(PromotionImage::getImageUrl).collect(Collectors.toList()) : new ArrayList<>();
            reqDto.setImageUrls(urls != null && !urls.isEmpty() ? urls : new ArrayList<>());

            // 2. contentType Null 방어 및 기본값 매핑
            String dbContentType = promotion.getContentType();
            if (dbContentType == null || dbContentType.isBlank()) {
                reqDto.setContentType("POST");
            } else {
                reqDto.setContentType(dbContentType.toUpperCase().trim());
            }

            // 3. 분위기 태그 매핑 (DB의 mode 컬럼 값을 moodTag에 매핑)
            String dbMode = promotion.getMode(); // DB의 mode 컬럼은 '밝음', '따뜻함', '차분함' 등의 분위기 정보를 담고 있습니다.
            if (dbMode != null && !dbMode.isBlank()) {
                reqDto.setMoodTag(dbMode);
            }

            // 4. 해시태그 추출 및 매핑
            List<PromotionTag> promotionTags = promotionTagRepository.findAllByPromotion(promotion);
            if (promotionTags != null && !promotionTags.isEmpty()) {
                String hashTagStr = promotionTags.stream()
                        .map(t -> t.getTagName().startsWith("#") ? t.getTagName() : "#" + t.getTagName())
                        .collect(Collectors.joining(" "));
                reqDto.setHashTag(hashTagStr);
            }

            // --- Top 3 과거 우수 성과 콘텐츠 조회 및 매핑 ---
            try {
                Long userId = promotion.getUser().getUserId();
                List<Object[]> topContentsRaw = contentRepository.findTopContentsWithClickCountByUserId(userId, PageRequest.of(0, 3));
                List<TopPerformerDto> topPerformers = new ArrayList<>();
                
                for (Object[] row : topContentsRaw) {
                    Content topContent = (Content) row[0];
                    Long countLong = (Long) row[1];
                    Integer clickCount = countLong != null ? countLong.intValue() : 0;
                    
                    String marketingText = "";
                    if (topContent.getCaption() != null && !topContent.getCaption().isBlank()) {
                        marketingText = topContent.getCaption();
                    } else if (topContent.getBodyText() != null && !topContent.getBodyText().isBlank()) {
                        marketingText = topContent.getBodyText();
                    }
                    
                    if (marketingText.length() > 500) {
                        marketingText = marketingText.substring(0, 500);
                    }
                    
                    List<String> tags = new ArrayList<>();
                    if (topContent.getPromotionExecution() != null && topContent.getPromotionExecution().getPromotion() != null) {
                        List<PromotionTag> pTags = promotionTagRepository.findAllByPromotion(topContent.getPromotionExecution().getPromotion());
                        if (pTags != null) {
                            tags = pTags.stream().map(PromotionTag::getTagName).collect(Collectors.toList());
                        }
                    }
                    
                    topPerformers.add(TopPerformerDto.builder()
                            .clickCount(clickCount)
                            .marketingText(marketingText)
                            .tags(tags)
                            .build());
                }
                
                if (!topPerformers.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String topPerformersJson = objectMapper.writeValueAsString(topPerformers);
                    reqDto.setTopPerformers(topPerformersJson);
                    log.info("Top Performers JSON payload for user {}: {}", userId, topPerformersJson);
                }
            } catch (Exception e) {
                log.error("Failed to fetch top performers for execution ID: {}", execution.getExecutionId(), e);
            }
            // --- 매핑 끝 ---

        } else {
            reqDto.setImageUrls(new ArrayList<>());
            reqDto.setContentType("IMAGE");
            reqDto.setMode("TRANSFORM");
        }

        return reqDto;
    }

    private void requestUpload(Long executionId) {
        // 업로드 요청 조립은 짧은 트랜잭션에서, 유튜브 업로드(수십 초)는 트랜잭션 밖에서
        UploadTarget target = transactionTemplate.execute(status -> buildUploadTarget(executionId));
        if (target == null) {
            return;
        }

        try {
            FastapiUploadResponseDto response = fastapiClientService.uploadToYoutube(target.request());
            if (response != null && "SUCCESS".equalsIgnoreCase(response.getStatus())) {
                transactionTemplate.executeWithoutResult(status ->
                        contentRepository.findById(target.contentId()).ifPresent(content -> {
                            content.setUploadVideoUrl(response.getYoutubeUrl());
                            content.setUploadedAt(LocalDateTime.now());
                            content.setStatus("PUBLISHED");
                            contentRepository.save(content);
                        }));
                log.info("Track B: Upload success. YouTube URL saved: {}", response.getYoutubeUrl());
            }
        } catch (Exception e) {
            // 원인 스택은 FastapiClientService 가 남긴다. 지연 허용 시간 안이면 다음 분에 다시 시도한다.
            String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            log.warn("Track B upload request failed for execution ID: {}. Retry next minute. cause: {}", executionId, cause);
        }
    }

    private UploadTarget buildUploadTarget(Long executionId) {
        PromotionExecution execution = executionRepository.findById(executionId).orElse(null);
        if (execution == null) {
            return null;
        }
        log.info("Track B: Requesting youtube upload for execution ID: {}", executionId);
        Promotion promotion = execution.getPromotion();
        PromotionSchedule schedule = execution.getPromotionSchedule();

        // Execution에 연결된 Content 조회
        Content content = contentRepository
                .findByPromotionExecution(execution).orElse(null);

        if (content == null || content.getLocalVideoPath() == null) {
            log.warn("Track B: No Content or LocalVideoPath found for execution ID: {}", execution.getExecutionId());
            return null;
        }

        // 생성 완료(GENERATED) 상태만 업로드. 배포 중단(CANCELLED)·이미 발행(PUBLISHED)된 콘텐츠는 건너뜀
        if (!ContentStatus.GENERATED.equals(content.getStatus())) {
            log.info("Track B: Skip upload. Content ID: {} status is {}", content.getContentId(), content.getStatus());
            return null;
        }

        // 1. 태그 추출
        List<String> tags = new ArrayList<>();
        if (promotion != null) {
            List<PromotionTag> promotionTags = promotionTagRepository.findAllByPromotion(promotion);
            if (promotionTags != null && !promotionTags.isEmpty()) {
                tags = promotionTags.stream()
                        .map(PromotionTag::getTagName)
                        .collect(Collectors.toList());
            }
        }

        // 2. 나레이션 문구(generatedText) 추출
        String generatedText = "";
        if (content.getCaption() != null && !content.getCaption().isBlank()) {
            generatedText = content.getCaption();
        } else if (content.getBodyText() != null && !content.getBodyText().isBlank()) {
            generatedText = content.getBodyText();
        }

        // 3. 유튜브 타이틀 조립 (나레이션 + 해시태그 문자열)
        String hashtagString = tags.stream()
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));
        
        String rawTitle = generatedText;
        if (!hashtagString.isBlank()) {
            rawTitle = rawTitle.isBlank() ? hashtagString : rawTitle + " " + hashtagString;
        }
        
        // 생성된 텍스트가 없거나 유효한 문장이 없을 경우 매장명 기반 고정 포맷 적용
        if (rawTitle.isBlank()) {
            String storeName = "매장";
            if (promotion != null && promotion.getUser() != null) {
                Store store = storeRepository.findByUser(promotion.getUser()).orElse(null);
                if (store != null) {
                    storeName = store.getStoreName();
                }
            }
            rawTitle = storeName + " 추천 쇼츠 영상";
        }
        
        // 유튜브 제목 최대 100자 제한 방어 코드
        String title = rawTitle.length() > 100 ? rawTitle.substring(0, 97) + "..." : rawTitle;

        // 4. 추적 링크 생성 및 설명란(Description) 조립
        String trackLink = baseUrl + "/api/v1/contents/track/" + content.getContentId();
        String description = generatedText + "\n\n"
                + "👇 이벤트 확인하기 👇\n"
                + trackLink;

        FastapiUploadReqDto uploadReq = FastapiUploadReqDto.builder()
                .scheduleId(schedule != null ? String.valueOf(schedule.getScheduleId()) : "")
                .localVideoPath(content.getLocalVideoPath())
                .title(title)
                .description(description)
                .tags(tags)
                .build();

        return new UploadTarget(content.getContentId(), uploadReq);
    }

    private record UploadTarget(Long contentId, FastapiUploadReqDto request) {
    }
}
