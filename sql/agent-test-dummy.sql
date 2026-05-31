INSERT INTO all_ligo.user (user_id, created_at, email, email_verified, password, updated_at) VALUES (1, '2026-06-01 00:21:01.000000', 'testuser@example.com', true, 'dummy_password_hash', '2026-06-01 00:21:01.000000');


INSERT INTO all_ligo.store (store_id, created_at, latitude, longitude, map_url, profile_image_url, store_name, updated_at, user_id) VALUES (1, '2026-06-01 00:21:06.000000', 35.83, 128.75, 'http://map.example.com', 'http://image.example.com', '마법의 디저트 카페', '2026-06-01 00:21:06.000000', 1);


INSERT INTO all_ligo.promotion (promotion_id, content_type, created_at, deadline, is_weather_enabled, prompt, updated_at, user_id, mode) VALUES (1, 'VIDEO', '2026-06-01 00:21:15.000000', null, false, '우리 가게 시그니처 초코 케이크입니다. 동화 속 마법의 숲에서 나올 것 같은 신비롭고 예쁜 포스터로 새로 그려서 홍보하고 싶어요.', '2026-06-01 00:21:15.000000', 1, 'TRANSFORM');
INSERT INTO all_ligo.promotion (promotion_id, content_type, created_at, deadline, is_weather_enabled, prompt, updated_at, user_id, mode) VALUES (2, 'VIDEO', '2026-06-01 02:06:52.000000', null, true, '불판 위에서 지글지글 굽히는 삼겹살입니다. 오늘 퇴근하고 당장 고기 한 점 하러 오시라고 아주 짧고 강렬하게 유혹해주세요.', '2026-06-01 02:08:26.000000', 1, 'ORIGINAL');
INSERT INTO all_ligo.promotion (promotion_id, content_type, created_at, deadline, is_weather_enabled, prompt, updated_at, user_id, mode) VALUES (3, 'POST', '2026-06-01 02:08:50.000000', null, true, '이번 여름을 맞이하여 아포가토를 출시하였습니다. 우리 매장은 블루보틀 급의 인테리어를 자랑하며 해당 제품은 커피 위에 아이스크림이 올라간 특별한 제품입니다. 에티오피아 고급 원두를 쓴다는 점을 강조해주세요.', '2026-06-01 02:09:33.000000', 1, '<null>');


INSERT INTO all_ligo.promotion_image (image_id, image_url, promotion_id) VALUES (1, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/cake.jpg', 1);
INSERT INTO all_ligo.promotion_image (image_id, image_url, promotion_id) VALUES (2, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/meat1.jpg', 2);
INSERT INTO all_ligo.promotion_image (image_id, image_url, promotion_id) VALUES (3, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/meat2.jpg', 2);
INSERT INTO all_ligo.promotion_image (image_id, image_url, promotion_id) VALUES (4, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/meat3.jpg', 2);
INSERT INTO all_ligo.promotion_image (image_id, image_url, promotion_id) VALUES (5, 'https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/content/cafe1.jpg', 3);


INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (1, '동화같은', 'MOOD', 1);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (2, '달콤한', 'MOOD', 1);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (3, '몽환적인', 'MOOD', 1);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (4, '#디저트카페', 'HASH', 1);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (5, '#수제케이크', 'HASH', 1);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (6, '#마법의맛', 'HASH', 1);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (7, '군침도는', 'MOOD', 2);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (8, '활기찬', 'MOOD', 2);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (9, '저녁회식', 'MOOD', 2);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (10, '#삼겹살맛집', 'HASH', 2);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (11, '#회식장소추천', 'HASH', 2);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (12, '#대구맛집', 'HASH', 2);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (13, '#고기스타', 'HASH', 2);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (14, '세련된', 'MOOD', 3);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (15, '여름', 'MOOD', 3);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (16, '시원한', 'MOOD', 3);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (17, '#룰리커피', 'HASH', 3);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (18, '#대구의블루보틀', 'HASH', 3);
INSERT INTO all_ligo.promotion_tag (tag_id, tag_name, tag_type, promotion_id) VALUES (19, '#분위기세련된카페', 'HASH', 3);


INSERT INTO all_ligo.promotion_schedule (schedule_id, day_of_week, publish_time, promotion_id) VALUES (1, '토요일', '2026-06-01 01:05:00.000000', 1);
INSERT INTO all_ligo.promotion_schedule (schedule_id, day_of_week, publish_time, promotion_id) VALUES (2, '일요일', '2026-06-01 02:21:00.000000', 2);
INSERT INTO all_ligo.promotion_schedule (schedule_id, day_of_week, publish_time, promotion_id) VALUES (3, '월요일', '2026-06-01 02:25:00.000000', 3);
