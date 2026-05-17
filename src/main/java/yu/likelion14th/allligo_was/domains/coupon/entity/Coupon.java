package yu.likelion14th.allligo_was.domains.coupon.entity;

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
import yu.likelion14th.allligo_was.domains.store.entity.Store;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(name="image_url", nullable = false)
    private String imageUrl;

    @Column(name="menu_name", nullable = false)
    private String menuName;

    @Column(name="discount_num", nullable = false)
    private Integer discountNum;

    // RATE, AMOUNT만 오도록 유지
    @Column(name="discount_type", nullable = false)
    private String discountType;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
