package yu.likelion14th.allligo_was.fastapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
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

    @Value("${app.backend.base-url:http://localhost:8080}")
    private String baseUrl;

    // 매분 0초에 실행
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void executeTwoTrackScheduler() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        
        // 기존 로직: 1시간 전 생성 요청 (운영용)
        // LocalDateTime oneHourLater = now.plusHours(1);
        
        // 테스트 로직: 5분 전 생성 요청 (테스트용, 테스트 완료 후 위 주석 해제 및 본 줄 삭제)
        LocalDateTime oneHourLater = now.plusMinutes(5);

        log.info("Two-Track Scheduler Running... now: {}, oneHourLater: {}", now, oneHourLater);

        // Track A (T - 1시간): 영상 생성 요청
        List<PromotionExecution> pendingExecutions = executionRepository.findAllByStatusAndExecutedAtBetween(
                "PENDING", oneHourLater, oneHourLater.plusSeconds(59));

        for (PromotionExecution execution : pendingExecutions) {
            log.info("Track A: Requesting content generation for execution ID: {}", execution.getExecutionId());
            
            // 상태를 PROCESSING으로 변경 후 저장
            execution.setStatus("PROCESSING");
            executionRepository.save(execution);

            Promotion promotion = execution.getPromotion();
            PromotionSchedule schedule = execution.getPromotionSchedule();

            // TODO: Promotion과 PromotionTag에서 실제 분위기태그, 해시태그 추출 (현재는 임시값 또는 기본값 처리)
            FastapiGenerateReqDto reqDto = new FastapiGenerateReqDto();

            // 기본 매핑
            reqDto.setMoodTag("밝은, 쾌활한");
            reqDto.setHashTag("#마케팅 #이벤트");
            reqDto.setPrompt(promotion != null ? promotion.getPrompt() : "");
            
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

                // 3. mode Null 방어 및 기본값 매핑
                String dbMode = promotion.getMode();
                if (dbMode == null || dbMode.isBlank()) {
                    reqDto.setMode("TRANSFORM");
                } else {
                    reqDto.setMode(dbMode.toUpperCase().trim());
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

            try {
                FastapiContentResponseDto response = fastapiClientService.generateContent(reqDto);
                if (response != null && response.getTaskId() != null) {
                    execution.setTaskId(response.getTaskId());
                    // status is likely "PROCESSING" from response, or we keep "PENDING"
                    if (response.getStatus() != null) {
                        execution.setStatus(response.getStatus());
                    }
                    executionRepository.save(execution);
                    log.info("Track A generation requested successfully. Task ID: {}", response.getTaskId());
                }
            } catch (Exception e) {
                log.error("Track A generation request failed for execution ID: {}", execution.getExecutionId(), e);
                execution.setStatus("FAILED");
                execution.setErrorMessage(e.getMessage());
                executionRepository.save(execution);
            }
        }

        // Track B (T - 0시간): 유튜브 업로드 요청
        List<PromotionExecution> uploadExecutions = executionRepository.findAllByExecutedAtBetween(
                now, now.plusSeconds(59));

        for (PromotionExecution execution : uploadExecutions) {
            log.info("Track B: Requesting youtube upload for execution ID: {}", execution.getExecutionId());
            Promotion promotion = execution.getPromotion();
            PromotionSchedule schedule = execution.getPromotionSchedule();

            // Execution에 연결된 Content 조회
            Content content = contentRepository
                    .findByPromotionExecution(execution).orElse(null);

            if (content == null || content.getLocalVideoPath() == null) {
                log.warn("Track B: No Content or LocalVideoPath found for execution ID: {}", execution.getExecutionId());
                continue;
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

            try {
                FastapiUploadResponseDto response = fastapiClientService.uploadToYoutube(uploadReq);
                if (response != null && "SUCCESS".equalsIgnoreCase(response.getStatus())) {
                    content.setUploadVideoUrl(response.getYoutubeUrl());
                    content.setUploadedAt(LocalDateTime.now());
                    content.setStatus("PUBLISHED");
                    contentRepository.save(content);
                    log.info("Track B: Upload success. YouTube URL saved: {}", response.getYoutubeUrl());
                }
            } catch (Exception e) {
                log.error("Track B upload request failed for execution ID: {}", execution.getExecutionId(), e);
            }
        }
    }
}
