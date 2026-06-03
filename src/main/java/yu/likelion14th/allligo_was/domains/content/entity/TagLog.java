package yu.likelion14th.allligo_was.domains.content.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionTag;

import java.time.LocalDateTime;

@Entity
@Table(name = "tag_log")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_log_id")
    private Long tagLogId;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private PromotionTag promotionTag;
}