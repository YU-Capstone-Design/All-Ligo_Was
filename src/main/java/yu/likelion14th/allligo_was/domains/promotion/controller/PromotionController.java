package yu.likelion14th.allligo_was.domains.promotion.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.domains.promotion.api.PromotionAPI;
import yu.likelion14th.allligo_was.domains.promotion.dto.request.PromotionCreateReqDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.request.PromotionUpdateReqDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionDetailResDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionListResDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionScheduleQueueResDto;
import yu.likelion14th.allligo_was.domains.promotion.service.PromotionQueueService;
import yu.likelion14th.allligo_was.domains.promotion.service.PromotionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/promotions")
public class PromotionController implements PromotionAPI {

    private final PromotionService promotionService;
    private final PromotionQueueService promotionQueueService;

    @Override
    @PostMapping
    public ResponseEntity<PromotionDetailResDto> createPromotion(
            @Valid @RequestBody PromotionCreateReqDto request
    ) {
        Long userId = getCurrentUserId();

        PromotionDetailResDto response =
                promotionService.createPromotion(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<List<PromotionListResDto>> getMyPromotions() {
        Long userId = getCurrentUserId();

        List<PromotionListResDto> response =
                promotionService.getMyPromotions(userId);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{promotionId}")
    public ResponseEntity<PromotionDetailResDto> getPromotionDetail(
            @PathVariable Long promotionId
    ) {
        Long userId = getCurrentUserId();

        PromotionDetailResDto response =
                promotionService.getPromotionDetail(userId, promotionId);

        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/{promotionId}")
    public ResponseEntity<PromotionDetailResDto> updatePromotion(
            @PathVariable Long promotionId,
            @Valid @RequestBody PromotionUpdateReqDto request
    ) {
        Long userId = getCurrentUserId();

        PromotionDetailResDto response =
                promotionService.updatePromotion(userId, promotionId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{promotionId}")
    public ResponseEntity<Void> deletePromotion(
            @PathVariable Long promotionId
    ) {
        Long userId = getCurrentUserId();

        promotionService.deletePromotion(userId, promotionId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/schedules/queue")
    public ResponseEntity<List<PromotionScheduleQueueResDto>> getScheduleQueue() {
        Long userId = getCurrentUserId();

        List<PromotionScheduleQueueResDto> response =
                promotionQueueService.getScheduleQueue(userId);

        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}