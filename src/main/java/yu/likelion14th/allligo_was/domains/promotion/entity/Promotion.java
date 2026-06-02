package yu.likelion14th.allligo_was.domains.promotion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yu.likelion14th.allligo_was.domains.content.entity.TagLog;
import yu.likelion14th.allligo_was.domains.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;

    // BLOG, VIDEO로 유지
    @Column(name= "content_type", nullable = false)
    private String contentType;

    @Column(name = "promotion_title", nullable = false)
    private String promotionTitle;

    @Column(name="prompt", nullable = false)
    private String prompt;

    @Column(name="is_weather_enabled", nullable = false)
    private boolean isWeatherEnabled;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="deadline")
    private LocalDateTime deadline;

    @Column(name="mode", nullable = true)
    private String mode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 태그를 불러오기 위한 양방향 관계
    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
    List<PromotionTag> tags;

    public void updatePromotionInfo(
            String promotionTitle,
            String contentType,
            String prompt,
            boolean isWeatherEnabled,
            String mode,
            LocalDateTime deadline
    ) {
        this.promotionTitle = promotionTitle;
        this.contentType = contentType;
        this.prompt = prompt;
        this.isWeatherEnabled = isWeatherEnabled;
        this.mode = mode;
        this.deadline = deadline;
        this.updatedAt = LocalDateTime.now();
    }




}
