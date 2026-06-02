package yu.likelion14th.allligo_was.domains.promotion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yu.likelion14th.allligo_was.domains.content.entity.Content;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionExecution {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    @Column(name="executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name="task_id")
    private String taskId;

    // 상태값: PENDING (대기 중), PROCESSING (진행 중), SUCCESS (성공), FAILED (실패)
    @Column(name="status", nullable = false)
    private String status;

    @Column(name="error_message")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="schedule_id")
    private PromotionSchedule promotionSchedule;

    @OneToOne(mappedBy = "promotionExecution", fetch = FetchType.LAZY)
    private Content content;

}
