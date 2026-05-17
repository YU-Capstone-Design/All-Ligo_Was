package yu.likelion14th.allligo_was.domains.content.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Column(name="body_text")
    private String bodyText;

    @Column(name="caption")
    private String caption;

    @Column(name="file_url")
    private String fileUrl;

    @Column(name="status")
    private String status;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="expires_at")
    private LocalDateTime expiresAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id", nullable = false)
    private PromotionExecution promotionExecution;

}
