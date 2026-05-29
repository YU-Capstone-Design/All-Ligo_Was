package yu.likelion14th.allligo_was.domains.coupon.entity;

import jakarta.persistence.*;
import lombok.*;
import yu.likelion14th.allligo_was.domains.store.entity.Store;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;

    @Column(name = "discount_num", nullable = false)
    private Integer discountNum;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}