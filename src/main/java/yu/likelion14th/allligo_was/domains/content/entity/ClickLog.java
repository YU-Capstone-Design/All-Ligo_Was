package yu.likelion14th.allligo_was.domains.content.entity;

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
public class ClickLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clickId;

    @Column(name="clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @Column(name="ip_address")
    private String ipAddress;

    @Column(name="click_source", nullable = false)
    private String clickSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="content_id", nullable = false)
    private Content content;

}
