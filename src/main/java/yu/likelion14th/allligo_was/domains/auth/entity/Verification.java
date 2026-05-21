package yu.likelion14th.allligo_was.domains.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long verificationId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public void updateVerification(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.isVerified = false;
        this.expiresAt = expiresAt;
        this.verifiedAt = null;
    }

    public void completeVerification() {
        this.isVerified = true;
        this.verifiedAt = LocalDateTime.now();
    }
}