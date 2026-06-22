-- 2026-06-22
-- 홍보 삭제 시 로그/알림 FK 정책 정리
--
-- 정책:
-- 1. click_log는 대시보드 통계 로그이므로 content가 삭제되어도 로그를 보존한다.
--    따라서 content_id는 NULL 허용 + ON DELETE SET NULL.
-- 2. tag_log는 태그 클릭 통계 로그이므로 promotion_tag가 삭제되어도 로그를 보존한다.
--    따라서 tag_id는 NULL 허용 + ON DELETE SET NULL.
-- 3. notification은 content에 종속된 알림이므로 content가 삭제되면 함께 삭제한다.
--    따라서 ON DELETE CASCADE.

-- =========================================================
-- click_log
-- =========================================================

-- 기존 click_log 백업
CREATE TABLE IF NOT EXISTS click_log_backup_20260622 AS
SELECT * FROM click_log;

-- user_id 컬럼 추가
-- 이미 존재하는 환경에서는 중복 실행 시 오류가 날 수 있음.
ALTER TABLE click_log
ADD COLUMN user_id BIGINT NULL;

-- 기존 FK 제거
-- 기존 FK 이름이 다른 경우 SHOW CREATE TABLE click_log로 확인 후 수정 필요.
ALTER TABLE click_log
DROP FOREIGN KEY FK8dubev7hcvdlf8mb5qdf4t1is;

-- 기존 데이터 user_id 백필
UPDATE click_log cl
JOIN content c ON cl.content_id = c.content_id
JOIN promotion_execution pe ON c.execution_id = pe.execution_id
JOIN promotion p ON pe.promotion_id = p.promotion_id
SET cl.user_id = p.user_id
WHERE cl.user_id IS NULL;

-- content_id NULL 허용
ALTER TABLE click_log
MODIFY content_id BIGINT NULL;

-- content 삭제 시 click_log는 보존하고 content_id만 NULL 처리
ALTER TABLE click_log
ADD CONSTRAINT FK_click_log_content
FOREIGN KEY (content_id)
REFERENCES content(content_id)
ON DELETE SET NULL;


-- =========================================================
-- tag_log
-- =========================================================

-- 기존 tag_log 백업
CREATE TABLE IF NOT EXISTS tag_log_backup_20260622 AS
SELECT * FROM tag_log;

-- tag_id NULL 허용
ALTER TABLE tag_log
MODIFY tag_id BIGINT NULL;

-- 기존 FK 제거
-- 기존 FK 이름이 다른 경우 SHOW CREATE TABLE tag_log로 확인 후 수정 필요.
ALTER TABLE tag_log
DROP FOREIGN KEY FK_tag_log_promotion_tag;

-- promotion_tag 삭제 시 tag_log는 보존하고 tag_id만 NULL 처리
ALTER TABLE tag_log
ADD CONSTRAINT FK_tag_log_promotion_tag
FOREIGN KEY (tag_id)
REFERENCES promotion_tag(tag_id)
ON DELETE SET NULL;


-- =========================================================
-- notification
-- =========================================================

-- 기존 notification 백업
CREATE TABLE IF NOT EXISTS notification_backup_20260622 AS
SELECT * FROM notification;

-- 기존 FK 제거
-- 기존 FK 이름이 다른 경우 SHOW CREATE TABLE notification으로 확인 후 수정 필요.
ALTER TABLE notification
DROP FOREIGN KEY FKkjn0x35geqqrxasp8kbeo99e0;

-- content 삭제 시 notification도 함께 삭제
ALTER TABLE notification
ADD CONSTRAINT FK_notification_content
FOREIGN KEY (content_id)
REFERENCES content(content_id)
ON DELETE CASCADE;