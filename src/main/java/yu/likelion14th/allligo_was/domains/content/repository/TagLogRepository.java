package yu.likelion14th.allligo_was.domains.content.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yu.likelion14th.allligo_was.domains.content.entity.TagLog;

import java.util.List;

public interface TagLogRepository extends JpaRepository<TagLog, Long> {
    @Modifying
    @Query("""
        update TagLog tl
        set tl.promotionTag = null
        where tl.promotionTag.tagId in :tagIds
    """)
    void nullifyPromotionTagByTagIds(@Param("tagIds") List<Long> tagIds);
}
