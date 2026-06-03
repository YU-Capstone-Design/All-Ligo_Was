
INSERT INTO user (user_id, created_at, email, email_verified, password, updated_at)
VALUES (1, '2026-06-01 00:21:01', 'testuser@example.com', true, 'dummy_password_hash', '2026-06-01 00:21:01');

INSERT INTO store (store_id, created_at, latitude, longitude, map_url, profile_image_url, store_name, updated_at, user_id)
VALUES (1, '2026-06-01 00:21:06', 35.83, 128.75, 'http://map.example.com', 'http://image.example.com', '마법의 디저트 카페', '2026-06-01 00:21:06', 1);

-- 날짜도 테스트에 맞게 잘 조정 바랍니다.
INSERT INTO promotion (promotion_id, content_type, created_at, deadline, is_weather_enabled, prompt, updated_at, user_id, mode, promotion_title)
VALUES (1, 'VIDEO', '2026-06-01 00:21:15', '2026-06-30 23:59:59', false, '우리 가게 시그니처 초코 케이크입니다. 동화 속 마법의 숲에서 나올 것 같은 신비롭고 예쁜 포스터로 새로 그려서 홍보하고 싶어요.', '2026-06-01 00:21:15', 1, 'TRANSFORM', '고디바 초코케이크');

INSERT INTO promotion (promotion_id, content_type, created_at, deadline, is_weather_enabled, prompt, updated_at, user_id, mode, promotion_title)
VALUES (2, 'VIDEO', '2026-06-01 02:06:52', '2026-06-30 23:59:59', true, '불판 위에서 지글지글 굽히는 삼겹살입니다. 오늘 퇴근하고 당장 고기 한 점 하러 오시라고 아주 짧고 강렬하게 유혹해주세요.', '2026-06-01 02:08:26', 1, 'ORIGINAL', '황제무한화로구이');

INSERT INTO promotion (promotion_id, content_type, created_at, deadline, is_weather_enabled, prompt, updated_at, user_id, mode, promotion_title)
VALUES (3, 'POST', '2026-06-01 02:08:50', '2026-06-30 23:59:59', true, '이번 여름을 맞이하여 아포가토를 출시하였습니다. 에티오피아 고급 원두를 쓴다는 점을 강조해주세요.', '2026-06-01 02:09:33', 1, 'TRANSFORM', '룰리커피 경산점');

-- 주소는 테스트 시 S3에 업로드해놓고 경로 설정 바랍니다.
INSERT INTO promotion_image (image_id, image_url, promotion_id) VALUES
                                                                    (1, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/cake.jpg', 1),
                                                                    (2, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/meat1.jpg', 2),
                                                                    (3, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/meat2.jpg', 2),
                                                                    (4, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/meat3.jpg', 2),
                                                                    (5, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/cafe1.jpg', 3);

INSERT INTO promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES
                                                                         (1, '동화같은', 'MOOD', 1), (2, '달콤한', 'MOOD', 1), (3, '몽환적인', 'MOOD', 1), (4, '#디저트카페', 'HASH', 1), (5, '#수제케이크', 'HASH', 1), (6, '#마법의맛', 'HASH', 1),
                                                                         (7, '군침도는', 'MOOD', 2), (8, '활기찬', 'MOOD', 2), (9, '저녁회식', 'MOOD', 2), (10, '#삼겹살맛집', 'HASH', 2), (11, '#회식장소추천', 'HASH', 2), (12, '#대구맛집', 'HASH', 2), (13, '#고기스타', 'HASH', 2),
                                                                         (14, '세련된', 'MOOD', 3), (15, '여름', 'MOOD', 3), (16, '시원한', 'MOOD', 3), (17, '#룰리커피', 'HASH', 3), (18, '#대구의블루보틀', 'HASH', 3), (19, '#분위기세련된카페', 'HASH', 3);

-- 5. 스케줄 규칙 세팅 (에러 방지를 위해 영어 요일로 변경)
INSERT INTO promotion_schedule (schedule_id, day_of_week, publish_time, promotion_id)
VALUES (1, 'WEDNESDAY', '2026-06-03 11:50:00', 1);

INSERT INTO promotion_schedule (schedule_id, day_of_week, publish_time, promotion_id)
VALUES (2, 'FRIDAY', '2026-06-05 18:00:00', 2);

INSERT INTO promotion_schedule (schedule_id, day_of_week, publish_time, promotion_id)
VALUES (3, 'WEDNESDAY', '2026-06-03 18:50:00', 3);