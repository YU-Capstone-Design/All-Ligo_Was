package yu.likelion14th.allligo_was.domains.promotion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionExecution {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    @Column(name="executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name="status", nullable = false)
    private String status;

    @Column(name="weather_info")
    private String weatherInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="promotion_id", nullable = false)
    private Promotion promotion;

}
