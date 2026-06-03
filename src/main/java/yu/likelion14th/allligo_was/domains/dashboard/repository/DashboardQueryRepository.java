package yu.likelion14th.allligo_was.domains.dashboard.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardQueryRepository {

    private final EntityManager em;

    public Long countTotalClicks(Long userId, LocalDateTime start, LocalDateTime end) {
        Object result = em.createNativeQuery("""
                SELECT COUNT(*)
                FROM click_log cl
                JOIN content c ON cl.content_id = c.content_id
                JOIN promotion_execution pe ON c.execution_id = pe.execution_id
                JOIN promotion p ON pe.promotion_id = p.promotion_id
                WHERE p.user_id = :userId
                  AND cl.clicked_at >= :start
                  AND cl.clicked_at < :end
                """)
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();

        return ((Number) result).longValue();
    }

    public List<Object[]> findHourlyClicks(Long userId, LocalDateTime start, LocalDateTime end) {
        return em.createNativeQuery("""
                SELECT HOUR(cl.clicked_at) AS hour, COUNT(*) AS click_count
                FROM click_log cl
                JOIN content c ON cl.content_id = c.content_id
                JOIN promotion_execution pe ON c.execution_id = pe.execution_id
                JOIN promotion p ON pe.promotion_id = p.promotion_id
                WHERE p.user_id = :userId
                  AND cl.clicked_at >= :start
                  AND cl.clicked_at < :end
                GROUP BY HOUR(cl.clicked_at)
                ORDER BY hour
                """)
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public List<Object[]> findDayOfWeekClicks(Long userId, LocalDateTime start, LocalDateTime end) {
        return em.createNativeQuery("""
                SELECT DAYOFWEEK(cl.clicked_at) AS day_num, COUNT(*) AS click_count
                FROM click_log cl
                JOIN content c ON cl.content_id = c.content_id
                JOIN promotion_execution pe ON c.execution_id = pe.execution_id
                JOIN promotion p ON pe.promotion_id = p.promotion_id
                WHERE p.user_id = :userId
                  AND cl.clicked_at >= :start
                  AND cl.clicked_at < :end
                GROUP BY DAYOFWEEK(cl.clicked_at)
                ORDER BY day_num
                """)
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public List<Object[]> findTagClicks(Long userId, LocalDateTime start, LocalDateTime end) {
        return em.createNativeQuery("""
                SELECT pt.tag_name, COUNT(tl.tag_log_id) AS click_count
                FROM tag_log tl
                JOIN promotion_tag pt ON tl.tag_id = pt.tag_id
                JOIN promotion p ON pt.promotion_id = p.promotion_id
                WHERE p.user_id = :userId
                  AND tl.clicked_at >= :start
                  AND tl.clicked_at < :end
                GROUP BY pt.tag_id, pt.tag_name
                ORDER BY click_count DESC
                LIMIT 3
                """)
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public List<Object[]> findContentTypeClicks(Long userId, LocalDateTime start, LocalDateTime end) {
        return em.createNativeQuery("""
                SELECT p.content_type, COUNT(cl.click_id) AS click_count
                FROM click_log cl
                JOIN content c ON cl.content_id = c.content_id
                JOIN promotion_execution pe ON c.execution_id = pe.execution_id
                JOIN promotion p ON pe.promotion_id = p.promotion_id
                WHERE p.user_id = :userId
                  AND cl.clicked_at >= :start
                  AND cl.clicked_at < :end
                GROUP BY p.content_type
                ORDER BY click_count DESC
                """)
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}