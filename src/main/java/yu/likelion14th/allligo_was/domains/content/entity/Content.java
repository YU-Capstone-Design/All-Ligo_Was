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
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    // 공통 필드

    @Column(name="status")
    private String status;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="expires_at")
    private LocalDateTime expiresAt;

    @Column(name="uploaded_at")
    private LocalDateTime uploadedAt;

    // 포스트용 필드


    @Column(name="poster_url")
    private String posterUrl;

    @Column(name="body_text")
    private String bodyText;


    // 영상용 필드

    @Column(name="caption")
    private String caption;

    @Column(name="s3_video_url")
    private String s3VideoUrl;

    @Column(name="local_video_path")
    private String localVideoPath;

    @Column(name="upload_video_url")
    private String uploadVideoUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id", nullable = false)
    private PromotionExecution promotionExecution;
}
