package yu.likelion14th.allligo_was.domains.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yu.likelion14th.allligo_was.domains.content.entity.ClickLog;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.content.entity.TagLog;
import yu.likelion14th.allligo_was.domains.content.repository.ClickLogRepository;
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.domains.content.repository.TagLogRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionTag;
import yu.likelion14th.allligo_was.domains.store.entity.Store;
import yu.likelion14th.allligo_was.domains.store.repository.StoreRepository;
import yu.likelion14th.allligo_was.domains.user.entity.User;
import yu.likelion14th.allligo_was.domains.user.repository.UserRepository;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final TagLogRepository tagLogRepository;
    private final ClickLogRepository clickLogRepository;

    /**
     * 조회수 증가 및
     * @param contentId 컨텐츠 ID
     * @return URL문자열 (소상공인 URL)
     */
    public String trackAndRedirect(Long contentId) {
        // 1. 콘텐츠 조회
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));


        // 2. 소상공인 및 가게 조회
        User owner = content.getPromotionExecution().getPromotion().getUser();

        if(owner == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Store store = storeRepository.findByUser(owner)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));


        // 3. 클릭 로그 조회 생성
        clickLogRepository.save(ClickLog.builder()
                .content(content)
                .clickSource(content.getContentType())
                .clickedAt(LocalDateTime.now())
                .build());


        // 4. 태그 로그 조회 생성
        List<PromotionTag> tags = content.getPromotionExecution().getPromotion().getTags();

        // 클릭된 태그들 로그로 저장
        for(PromotionTag tag : tags){
            tagLogRepository.save(TagLog.builder()
                    .clickedAt(LocalDateTime.now())
                    .promotionTag(tag)
                    .tagName(tag.getTagName().trim())
                    .userId(owner.getUserId())
                    .build());
        }

        // 5. 소상공인 가게URL 반환
        return store.getMapUrl();
    }
}
