-- content category 테이블
INSERT INTO content_category(category, activated)
VALUES ('THEATER', TRUE),
       ('DANCE', TRUE),
       ('POP_DANCE', TRUE),
       ('CLASSIC', TRUE),
       ('GUKAK', TRUE),
       ('POP_MUSIC', TRUE),
       ('MIX', TRUE),
       ('MAGIC', TRUE),
       ('MUSICAL', TRUE),
       ('TOUR', TRUE),
       ('CULTURE', TRUE),
       ('SPORTS', TRUE);

-- content 테이블
INSERT INTO content (category_id, external_id, content_title, age, fee, start_date, end_date, run_time, time,
                     start_time, address, area, guname, longitude, latitude, description, poster,
                     event_type, activated, created_at, modified_at)
VALUES ((SELECT id FROM content_category WHERE category = 'GUKAK'), 'PF270201','ONE PACT (원팩트) HALL LIVE, ONE FACT: 합',
        '만 11세 이상', 'VIP석 100,000원, R석 85,000원', '2025-08-16', '2025-08-16', '1시간 40분', '토요일(19:00)', '19:00',
        '스카이아트홀 (스카이아트홀)', '서울특별시', '영등포구', NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF270201_250723_142758.jpg', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'CLASSIC'),
        'PF270200','이은준 플루트 독주회, Origin of French Flute School: Souvenir de France Ⅱ', '만 7세 이상', '전석 20,000원',
        '2025-08-26', '2025-08-26', '1시간 40분', '화요일(19:30)', '19:30', '예술의전당 [서울] (리사이틀홀)', '서울특별시', '서초구',
        NULL, NULL, NULL, 'http://www.kopis.or.kr/upload/pfmPoster/PF_PF270200_250723_141608.gif',
        'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'CLASSIC'), 'PF270200','차민정 바이올린 독주회', '만 7세 이상',
        '전석 20,000원', '2025-08-19', '2025-08-19', '1시간 30분', '화요일(19:30)', '19:30',
        '예술의전당 [서울] (리사이틀홀)', '서울특별시','서초구', NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269271_250711_141630.gif', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'THEATER'), 'PF270200','제28회 서울프린지페스티벌, 공과사',
        '만 14세 이상', '전석 10,000원', '2025-08-01', '2025-08-03', '1시간',
        '금요일(20:00), 토요일(15:00,19:00), 일요일(15:00)', '20:00, 15:00, 19:00', '플랫폼 달 (플랫폼 달 (2F))',
        '서울특별시',NULL, NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269269_250711_141409.jpg', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'CLASSIC'),
        'PF270200', '소프라노 한보경 독창회: Die Liebe ist...','만 7세 이상', '전석 30,000원', '2025-07-29', '2025-07-29',
        '1시간 30분', '화요일(19:00)', '19:00', '꿈의숲아트센터 (콘서트홀)', '서울특별시',NULL, NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269267_250711_141016.png', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'POP_MUSIC'), 'PF270200','홍경민 매달리스트 콘서트', '만 7세 이상',
        '전석 77,000원', '2025-08-02', '2025-08-02', '2시간', '토요일(18:00)', '18:00',
        '스케치홀 (구.소극장 선물 1관) (SA Hall (지하1층) )', '서울특별시', NULL, NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269265_250711_135746.gif', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'THEATER'), 'PF270200','브릴리언트: 찬란하게 빛나던 [대학로]',
        '만 11세 이상', '전석 40,000원', '2025-08-04', '2025-08-29', '1시간 30분',
        '월요일(15:00), 수요일(15:00), 금요일(15:00), HOL(19:30)', '15:00, 19:30', '대학로 무하아트센터 (대학로 무하아트센터)',
        '서울특별시','종로구', NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269261_250711_135052.gif', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'CLASSIC'), 'PF270200','안종도 피아노 리사이틀', '만 7세 이상',
        'R석 50,000원, S석 30,000원', '2025-09-12', '2025-09-12', '1시간 35분', '금요일(19:30)', '19:30',
        '예술의전당 [서울] (IBK챔버홀)', '서울특별시','서초구', NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269258_250711_134533.gif', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'POP_MUSIC'), 'PF270200','Flight: 원위 (ONEWE) 편 [서울]',
        '만 7세 이상', '전석 110,000원', '2025-08-30', '2025-08-31', '1시간 30분', '토요일(19:00), 일요일(17:00)',
        '19:00, 17:00', '신한카드 SOL페이 스퀘어(구.신한pLay 스퀘어) (라이브홀)', '서울특별시',NULL, NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269257_250711_134419.gif', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'POP_MUSIC'),
        'PF270200', 'Kep1er CONCERT TOUR, Into The Orbit: Kep1asia [서울]', '만 7세 이상',
        'VIP석 176,000원, R석 143,000원', '2025-09-20', '2025-09-21', '2시간', '토요일(18:00), 일요일(17:00)',
        '18:00, 17:00', '연세대학교 백주년기념관 (콘서트홀)', '서울특별시',NULL, NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269249_250711_132442.jpg', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'MUSICAL'),
        'PF270200','언더노트 Vol.4, 조하린 서현석의 뮤지컬 갈라 콘서트: 어쩌다 보니', '만 12세 이상', '전석 40,000원', '2025-08-02',
        '2025-08-02', '1시간', '토요일(19:00)', '19:00', '언더노트 (공연장)', '서울특별시',NULL, NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269246_250711_132145.gif', 'EVENT', true, NOW(), NOW()),
       ((SELECT id FROM content_category WHERE category = 'POP_MUSIC'),
        'PF270200','전자양, 소음의 왕 10주년 기념 단독공연: 우리가 이겼어!', '만 19세 이상', '스탠딩 55,000원', '2025-08-09', '2025-08-10',
        '1시간 40분', '토요일(18:00), 일요일(17:00)', '18:00, 17:00', '극락 (극락)', '서울특별시',NULL, NULL, NULL, NULL,
        'http://www.kopis.or.kr/upload/pfmPoster/PF_PF269236_250711_130344.jpg', 'EVENT', true, NOW(), NOW());

-- content image 테이블
INSERT INTO content_image (content_id, image_url)
VALUES (1, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270201_202507230227589743.jpg'),
       (1, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270201_202507230227589682.jpg'),
       (1, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270201_202507230227589501.jpg'),
       (1, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270201_202507230227589440.jpg'),
       (2, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270200_202507230216081870.jpg'),
       (3, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270197_202507230203188822.jpg'),
       (3, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270197_202507230203188641.jpg'),
       (3, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270197_202507230203188500.jpg'),
       (4, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270195_202507230201457610.jpg'),
       (5, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270193_202507230157502970.png'),
       (6, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270189_202507230152478522.jpg'),
       (6, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270189_202507230152478451.jpg'),
       (6, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270189_202507230152478370.jpg'),
       (7, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270188_202507230148590820.png'),
       (8, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270186_202507230142270630.jpg'),
       (9, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270183_202507230134237450.jpg'),
       (10, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270174_202507230117504660.jpg'),
       (11, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270171_202507230110062470.jpg'),
       (12, 'http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF270169_202507230103002580.jpg');

-- content url 테이블
INSERT INTO content_url (content_id, site_name, url)
VALUES (1, '네이버N예약', 'https://booking.naver.com/booking/12/bizes/607625'),
       (2, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009757'),
       (3, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009662'),
       (4, '프린지네트워크',
        'https://www.seoulfringefestival.net:5632/load.asp?subPage=270.view&search_idx=5707&search_day=&page=1'),
       (5, '세종문화회관',
        'https://www.sejongpac.or.kr/dfac/dfacPerformance/dfacPerformance/performTicket.do?performIdx=36386&menuNo=1200007'),
       (6, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009867'),
       (7, '네이버N예약', 'https://booking.naver.com/booking/12/bizes/1452799'),
       (7, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009910'),
       (8, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009241'),
       (9, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009929'),
       (10, 'NHN티켓링크', 'http://www.ticketlink.co.kr/product/57363'),
       (11, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009944'),
       (12, '멜론티켓', 'https://ticket.melon.com/performance/index.htm?prodId=211622');



-- UserInfo 테이블 데이터 삽입
INSERT INTO content_url (content_id, site_name, url)
VALUES
    (1, '예스24', 'http://ticket.yes24.com/Perf/54447'),
    (2, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25010408'),
    (3, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25010096'),
    (4, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25010427'),
    (5, '멜론티켓', 'https://ticket.melon.com/performance/index.htm?prodId=211655'),
    (6, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009911'),
    (7, '세종문화회관', 'https://www.sejongpac.or.kr/dfac/dfacPerformance/dfacPerformance/performTicket.do?performIdx=36422&menuNo=1200007'),
    (8, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25010194'),
    (9, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009961'),
    (10, '멜론티켓', 'https://ticket.melon.com/performance/index.htm?prodId=211664'),
    (11, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25010533'),
    (12, '인터파크', 'http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=25009488');
-- User 테이블 데이터 삽입
INSERT INTO "user" (email, password, nickname, birth_date, gender, address, latitude, longitude,
                    role, status, due_date, suspend_duration, due_reason, is_verified,
                    is_marketing_agreed, info_id, activated, created_at, modified_at)
VALUES ('t1@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '김철수',
        '19900315', 'MALE', '서울특별시 강남구 역삼동 테헤란로 123', 37.5009, 127.0360,
        'ROLE_USER', 'ACTIVE', NULL, NULL, NULL, true, true, 't1@aaa.aaa', true, NOW(), NOW()),

       ('t2@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '이영희',
        '19850722', 'FEMALE', '서울특별시 송파구 잠실동 올림픽로 456', 37.5133, 127.1028,
        'ROLE_USER', 'ACTIVE', NULL, NULL, NULL, true, false, 't2@aaa.aaa', true, NOW(), NOW()),

       ('t3@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '박민수',
        '19921108', 'MALE', '서울특별시 서초구 반포동 반포대로 789', 37.5047, 127.0089,
        'ROLE_USER', 'ACTIVE', NULL, NULL, NULL, true, true, 't3@aaa.aaa', true, NOW(), NOW()),

       ('t4@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '정수빈',
        '19880214', 'FEMALE', '서울특별시 마포구 상암동 월드컵북로 321', 37.5799, 126.8896,
        'ROLE_USER', 'ACTIVE', NULL, NULL, NULL, false, true, 't4@aaa.aaa', true, NOW(), NOW()),

       ('t5@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '최동욱',
        '19930930', 'MALE', '서울특별시 용산구 한남동 한남대로 654', 37.5347, 126.9990,
        'ROLE_USER', 'SUSPENDED', '2025-08-15', 30, '부적절한 게시물', true, false, 't5@aaa.aaa', true,
        NOW(), NOW()),

       ('t6@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '장미라',
        '19870503', 'FEMALE', '서울특별시 성동구 성수동 아차산로 147', 37.5447, 127.0557,
        'ROLE_USER', 'ACTIVE', NULL, NULL, NULL, true, true, 't6@aaa.aaa', true, NOW(), NOW()),

       ('t7@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '안준호',
        '19911225', 'MALE', '서울특별시 강북구 수유동 도봉로 258', 37.6369, 127.0253,
        'ROLE_USER', 'ACTIVE', NULL, NULL, NULL, true, true, 't7@aaa.aaa', true, NOW(), NOW()),

       ('t8@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '오세영',
        '19890817', 'FEMALE', '서울특별시 광진구 자양동 능동로 369', 37.5347, 127.0822,
        'ROLE_USER', 'ACTIVE', NULL, NULL, NULL, false, false, 't8@aaa.aaa', true, NOW(), NOW()),

       ('t9@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa', '유하늘',
        '19940412', 'MALE', '서울특별시 중구 을지로 을지로3가 852', 37.5661, 126.9917,
        'ROLE_ADMIN', 'ACTIVE', NULL, NULL, NULL, true, true, 't9@aaa.aaa', true, NOW(), NOW()),

       ('t10@aaa.aaa', '{bcrypt}$2a$10$ART8g5agLHIE7F4cZ.SEreokni2CuMBm6mgwg6xXhIk4eCD75P9Oa',
        '임소라', '19860128', 'FEMALE', '서울특별시 동작구 상도동 상도로 741', 37.5013, 126.9486,
        'ROLE_USER', 'ACTIVE', NULL, NULL, NULL, true, true, 't10@aaa.aaa', true, NOW(), NOW());


-- GroupPreference 테이블 데이터 삽입
INSERT INTO group_preference (category, user_id, activated, created_at, modified_at)
VALUES
-- t1
('FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),

-- t2
('ART', 't2@aaa.aaa', true, NOW(), NOW()),
('MOVIE', 't2@aaa.aaa', true, NOW(), NOW()),
('CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),

-- t3
('SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('STUDY', 't3@aaa.aaa', true, NOW(), NOW()),

-- t4
('GAME', 't4@aaa.aaa', true, NOW(), NOW()),
('FOOD', 't4@aaa.aaa', true, NOW(), NOW()),

-- t5
('ART', 't5@aaa.aaa', true, NOW(), NOW()),
('TRAVEL', 't5@aaa.aaa', true, NOW(), NOW()),
('SPORT', 't5@aaa.aaa', true, NOW(), NOW()),

-- t6
('STUDY', 't6@aaa.aaa', true, NOW(), NOW()),
('MOVIE', 't6@aaa.aaa', true, NOW(), NOW()),

-- t7
('CULTURE', 't7@aaa.aaa', true, NOW(), NOW()),
('FOOD', 't7@aaa.aaa', true, NOW(), NOW()),
('TRAVEL', 't7@aaa.aaa', true, NOW(), NOW()),

-- t8
('GAME', 't8@aaa.aaa', true, NOW(), NOW()),
('MOVIE', 't8@aaa.aaa', true, NOW(), NOW()),

-- t9
('SPORT', 't9@aaa.aaa', true, NOW(), NOW()),
('STUDY', 't9@aaa.aaa', true, NOW(), NOW()),
('ART', 't9@aaa.aaa', true, NOW(), NOW()),

-- t10
('FOOD', 't10@aaa.aaa', true, NOW(), NOW()),
('CULTURE', 't10@aaa.aaa', true, NOW(), NOW());

-- ContentPreference 테이블 데이터 삽입
INSERT INTO content_preference (category, user_id, activated, created_at, modified_at)
VALUES
-- t1
('THEATER', 't1@aaa.aaa', true, NOW(), NOW()),
('MUSICAL', 't1@aaa.aaa', true, NOW(), NOW()),

-- t2
('POP_DANCE', 't2@aaa.aaa', true, NOW(), NOW()),
('CLASSIC', 't2@aaa.aaa', true, NOW(), NOW()),
('TOUR', 't2@aaa.aaa', true, NOW(), NOW()),

-- t3
('MAGIC', 't3@aaa.aaa', true, NOW(), NOW()),
('GUKAK', 't3@aaa.aaa', true, NOW(), NOW()),

-- t4
('POP_MUSIC', 't4@aaa.aaa', true, NOW(), NOW()),
('CULTURE', 't4@aaa.aaa', true, NOW(), NOW()),

-- t5
('MIX', 't5@aaa.aaa', true, NOW(), NOW()),
('SPORTS', 't5@aaa.aaa', true, NOW(), NOW()),
('THEATER', 't5@aaa.aaa', true, NOW(), NOW()),

-- t6
('TOUR', 't6@aaa.aaa', true, NOW(), NOW()),
('CLASSIC', 't6@aaa.aaa', true, NOW(), NOW()),

-- t7
('DANCE', 't7@aaa.aaa', true, NOW(), NOW()),
('GUKAK', 't7@aaa.aaa', true, NOW(), NOW()),
('MAGIC', 't7@aaa.aaa', true, NOW(), NOW()),

-- t8
('MUSICAL', 't8@aaa.aaa', true, NOW(), NOW()),
('CULTURE', 't8@aaa.aaa', true, NOW(), NOW()),

-- t9
('POP_DANCE', 't9@aaa.aaa', true, NOW(), NOW()),
('POP_MUSIC', 't9@aaa.aaa', true, NOW(), NOW()),
('SPORTS', 't9@aaa.aaa', true, NOW(), NOW()),

-- t10
('THEATER', 't10@aaa.aaa', true, NOW(), NOW()),
('DANCE', 't10@aaa.aaa', true, NOW(), NOW());

-- Follow 테이블 데이터 삽입
INSERT INTO follow (follower_id, followee_id, activated, created_at, modified_at)
VALUES
-- t1 follows t2, t3
('t1@aaa.aaa', 't2@aaa.aaa', true, NOW(), NOW()),
('t1@aaa.aaa', 't3@aaa.aaa', true, NOW(), NOW()),

-- t2 follows t3, t4
('t2@aaa.aaa', 't3@aaa.aaa', true, NOW(), NOW()),
('t2@aaa.aaa', 't4@aaa.aaa', true, NOW(), NOW()),

-- t3 follows t1, t5, t6
('t3@aaa.aaa', 't1@aaa.aaa', true, NOW(), NOW()),
('t3@aaa.aaa', 't5@aaa.aaa', true, NOW(), NOW()),
('t3@aaa.aaa', 't6@aaa.aaa', true, NOW(), NOW()),

-- t4 follows t1
('t4@aaa.aaa', 't1@aaa.aaa', true, NOW(), NOW()),

-- t5 follows t1, t2
('t5@aaa.aaa', 't1@aaa.aaa', true, NOW(), NOW()),
('t5@aaa.aaa', 't2@aaa.aaa', true, NOW(), NOW()),

-- t6 follows t3
('t6@aaa.aaa', 't3@aaa.aaa', true, NOW(), NOW()),

-- t7 follows t6, t4
('t7@aaa.aaa', 't6@aaa.aaa', true, NOW(), NOW()),
('t7@aaa.aaa', 't4@aaa.aaa', true, NOW(), NOW()),

-- t8 follows t2, t3
('t8@aaa.aaa', 't2@aaa.aaa', true, NOW(), NOW()),
('t8@aaa.aaa', 't3@aaa.aaa', true, NOW(), NOW()),

-- t9 follows t5, t6, t8
('t9@aaa.aaa', 't5@aaa.aaa', true, NOW(), NOW()),
('t9@aaa.aaa', 't6@aaa.aaa', true, NOW(), NOW()),
('t9@aaa.aaa', 't8@aaa.aaa', true, NOW(), NOW()),

-- t10 follows t1, t2
('t10@aaa.aaa', 't1@aaa.aaa', true, NOW(), NOW()),
('t10@aaa.aaa', 't2@aaa.aaa', true, NOW(), NOW());

-- Group 테이블 데이터 삽입 (30개)
INSERT INTO "group" (title, explain, simple_explain, place_name, address, group_date,
                     max_people, now_people, image_url, status, latitude, longitude, view_count,
                     during, category, leader_id, activated, created_at, modified_at)
VALUES
-- t1@aaa.aaa 리더 모임들
('그림 그리기 모임', '함께 그림을 그리며 예술적 감성을 나누는 모임입니다.', '그림 그리기로 힐링해요', '홍대 아트센터', '서울특별시 마포구 홍익로 39',
 '2025-07-20 14:00:00', 10, 3, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5518, 126.9219, 180,
 2, 'ART', 't1@aaa.aaa', true, NOW(), NOW()),

('제주도 여행 동행', '제주도 3박 4일 여행을 함께할 동행을 구합니다.', '제주도 힐링 여행 같이해요', '제주공항', '제주특별자치도 제주시 공항로 2',
 '2025-08-15 10:00:00', 4, 2, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 33.5066, 126.4920,
 860, 2, 'TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),

('맛집 탐방 모임', '강남 맛집을 함께 탐방하는 모임입니다.', '강남 맛집 투어', '강남역 2번 출구', '서울특별시 강남구 강남대로 396',
 '2025-07-18 18:30:00', 6, 4, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.4979, 127.0276, 120,
 3, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),

('독서 토론 모임', 'IT 서적을 읽고 토론하는 개발자 모임입니다.', '개발자 독서 모임', '강남 스터디카페', '서울특별시 강남구 테헤란로 152',
 '2025-07-22 19:00:00', 8, 4, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5009, 127.0364,
 120, 4, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),

('수채화 클래스', '기초부터 배우는 수채화 그리기 모임', '수채화 입문 클래스', '이태원 화실', '서울특별시 용산구 이태원로 150',
 '2025-07-27 15:00:00', 8, 3, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5345, 126.9947,
 95, 3, 'ART', 't1@aaa.aaa', true, NOW(), NOW()),

-- t2@aaa.aaa 리더 모임들
('보드게임 카페 모임', '다양한 보드게임을 즐기는 모임입니다.', '보드게임으로 소통해요', '홍대 보드게임카페', '서울특별시 마포구 와우산로 94',
 '2025-07-19 19:00:00', 8, 5, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5563, 126.9223, 150,
 5, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),

('뮤지컬 관람 모임', '라이온킹 뮤지컬을 함께 관람합니다.', '뮤지컬 감상 모임', '샤롯데씨어터', '서울특별시 송파구 올림픽로 240',
 '2025-07-25 19:30:00', 5, 3, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5115, 127.0980,
 180, 4, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),

('주말 등산 모임', '관악산 등산을 함께하는 모임입니다.', '건강한 주말 등산', '관악산 입구', '서울특별시 관악구 관악로 1', '2025-07-21 08:00:00',
 12, 7, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.4486, 126.9466, 240, 2, 'SPORT',
 't2@aaa.aaa', true, NOW(), NOW()),

('클래식 콘서트 모임', '예술의전당에서 클래식 콘서트를 감상합니다.', '클래식 음악 감상', '예술의전당', '서울특별시 서초구 남부순환로 2406',
 '2025-07-30 19:30:00', 6, 2, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.4797, 127.0114,
 88, 3, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),

('테니스 레슨 모임', '초보자를 위한 테니스 레슨 모임입니다.', '테니스 기초 배우기', '올림픽공원 테니스장', '서울특별시 송파구 올림픽로 424',
 '2025-08-03 09:00:00', 10, 6, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5220, 127.1212,
 142, 2, 'SPORT', 't2@aaa.aaa', true, NOW(), NOW()),

-- t3@aaa.aaa 리더 모임들
('영화 감상 모임', '최신 영화를 함께 보고 이야기하는 모임입니다.', '영화 보고 토론해요', 'CGV 강남점', '서울특별시 강남구 강남대로 438',
 '2025-07-23 20:00:00', 6, 3, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.4988, 127.0266,
 150, 1, 'MOVIE', 't3@aaa.aaa', true, NOW(), NOW()),

('요리 클래스 모임', '이탈리안 요리를 배우는 클래스입니다.', '파스타 만들기 배워요', '이태원 요리학원', '서울특별시 용산구 이태원로 200',
 '2025-07-24 15:00:00', 8, 6, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5347, 126.9947,
 120, 3, 'FOOD', 't3@aaa.aaa', true, NOW(), NOW()),

('사진 촬영 모임', '한강에서 야경 사진을 찍는 모임입니다.', '한강 야경 사진 찍기', '반포한강공원', '서울특별시 서초구 신반포로 11',
 '2025-07-26 18:00:00', 6, 2, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5184, 126.9961,
 180, 2, 'ART', 't3@aaa.aaa', true, NOW(), NOW()),

('디저트 카페 투어', '홍대 디저트 카페를 돌아다니는 모임', '달콤한 디저트 투어', '홍대입구역', '서울특별시 마포구 양화로 160',
 '2025-08-05 14:00:00', 5, 3, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5567, 126.9230,
 76, 4, 'FOOD', 't3@aaa.aaa', true, NOW(), NOW()),

('독립영화 상영회', '독립영화를 보고 감독과의 대화 시간', '인디영화 감상', '시네마테크', '서울특별시 종로구 돈화문로 13',
 '2025-08-07 18:00:00', 15, 8, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5759, 126.9987,
 125, 3, 'MOVIE', 't3@aaa.aaa', true, NOW(), NOW()),

-- t4@aaa.aaa 리더 모임들
('캘리그라피 클래스', '아름다운 손글씨를 배우는 모임', '캘리그라피 입문', '압구정 문화센터', '서울특별시 강남구 압구정로 165',
 '2025-08-10 10:00:00', 12, 5, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5273, 127.0286,
 89, 2, 'ART', 't4@aaa.aaa', true, NOW(), NOW()),

('부산 여행 동행', '부산 2박 3일 여행 동행을 구합니다', '부산 바다 여행', '부산역', '부산광역시 동구 중앙대로 206',
 '2025-08-20 07:00:00', 6, 4, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 35.1164, 129.0414,
 234, 3, 'TRAVEL', 't4@aaa.aaa', true, NOW(), NOW()),

('한식 요리 클래스', '전통 한식을 배우는 요리 모임', '김치찌개 만들기', '종로 요리학원', '서울특별시 종로구 종로 51',
 '2025-08-12 16:00:00', 8, 7, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5702, 126.9836,
 156, 3, 'FOOD', 't4@aaa.aaa', true, NOW(), NOW()),

('영어 회화 모임', '영어로 자유롭게 대화하는 모임', '영어 스피킹 연습', '강남 영어카페', '서울특별시 강남구 테헤란로 427',
 '2025-08-14 19:00:00', 10, 6, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5058, 127.0583,
 98, 2, 'STUDY', 't4@aaa.aaa', true, NOW(), NOW()),

('심야 영화 모임', '심야 상영 영화를 보는 모임', '밤샘 영화 관람', '용산 아이파크몰 CGV', '서울특별시 용산구 한강대로 23',
 '2025-08-16 23:00:00', 8, 2, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5305, 126.9647,
 67, 5, 'MOVIE', 't4@aaa.aaa', true, NOW(), NOW()),

-- t5@aaa.aaa 리더 모임들
('도자기 만들기', '직접 도자기를 만들고 구워보는 체험', '도예 체험 클래스', '인사동 도예공방', '서울특별시 종로구 인사동길 62',
 '2025-08-18 13:00:00', 6, 3, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5718, 126.9857,
 112, 4, 'ART', 't5@aaa.aaa', true, NOW(), NOW()),

('강릉 여행 모임', '강릉 바다와 커피거리 여행', '강릉 1박 2일 여행', '강릉역', '강원특별자치도 강릉시 중앙로 1',
 '2025-08-25 08:00:00', 8, 5, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.7633, 128.8997,
 187, 2, 'TRAVEL', 't5@aaa.aaa', true, NOW(), NOW()),

('브런치 카페 모임', '을지로 브런치 카페 투어', '브런치 맛집 탐방', '을지로3가역', '서울특별시 중구 을지로 100',
 '2025-08-22 11:00:00', 4, 2, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5663, 126.9908,
 45, 3, 'FOOD', 't5@aaa.aaa', true, NOW(), NOW()),

('배드민턴 동호회', '주말 배드민턴을 함께 치는 모임', '배드민턴 운동 모임', '잠실 체육관', '서울특별시 송파구 올림픽로 25',
 '2025-08-24 10:00:00', 16, 9, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5145, 127.0741,
 201, 3, 'SPORT', 't5@aaa.aaa', true, NOW(), NOW()),

('게임 대회 모임', 'PC방에서 롤 토너먼트 개최', 'LOL 토너먼트', '강남 PC방', '서울특별시 강남구 강남대로 320',
 '2025-08-26 20:00:00', 10, 8, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.4976, 127.0279,
 189, 4, 'GAME', 't5@aaa.aaa', true, NOW(), NOW()),

-- 추가 모임들
('재즈 바 투어', '홍대 재즈바를 돌아다니는 모임', '재즈 음악 감상', '홍대 클럽거리', '서울특별시 마포구 어울마당로 35',
 '2025-08-28 21:00:00', 6, 4, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5540, 126.9226,
 134, 4, 'CULTURE', 't6@aaa.aaa', true, NOW(), NOW()),

('파이썬 스터디', '파이썬 기초부터 실무까지', '파이썬 프로그래밍', '역삼 코워킹스페이스', '서울특별시 강남구 역삼로 180',
 '2025-08-30 19:00:00', 12, 7, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.4996, 127.0357,
 167, 8, 'STUDY', 't6@aaa.aaa', true, NOW(), NOW()),

('애니메이션 상영회', '지브리 애니메이션 단체 관람', '지브리 영화제', '용산 아이파크몰', '서울특별시 용산구 한강대로 23',
 '2025-09-01 15:00:00', 20, 12, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5305, 126.9647,
 298, 2, 'MOVIE', 't6@aaa.aaa', true, NOW(), NOW()),

('일본 여행 준비', '일본 여행 계획을 함께 세우는 모임', '일본 여행 플래닝', '강남 스터디룸', '서울특별시 강남구 논현로 842',
 '2025-09-03 18:00:00', 8, 3, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5120, 127.0386,
 89, 2, 'TRAVEL', 't6@aaa.aaa', true, NOW(), NOW()),

('마라톤 준비 모임', '서울 마라톤 대회 준비 러닝 모임', '마라톤 훈련', '한강공원', '서울특별시 영등포구 여의동로 330',
 '2025-09-05 06:00:00', 15, 11, 'https://team08-funfun.s3.ap-northeast-2.amazonaws.com/groups/0dd754ff-18ae-4172-8487-3632d9540b4e.jpg', 'RECRUITING', 37.5287, 126.9336,
 276, 2, 'SPORT', 't6@aaa.aaa', true, NOW(), NOW());

-- 임의 데이터
-- 강남구: 26개
-- 종로구: 21개
-- 마포구: 16개
-- 용산구: 11개
-- 송파구: 6개
-- 서대문구: 6개
-- 중구: 6개
-- 광진구: 4개
-- 영등포구: 3개
-- 관악구: 2개
-- 성동구: 2개
-- 서초구: 2개
-- 노원구: 1개, 온라인 이라는 컬럼명 주석처리 나중에 논의
INSERT INTO "group" (title, explain, simple_explain, place_name, address, group_date,
                     max_people, now_people, image_url, status, latitude, longitude,
                     during, category, leader_id, activated, created_at, modified_at)
VALUES
('남산타워 야경 촬영', '서울 남산타워에서 아름다운 야경을 담는 사진 모임입니다. 삼각대 필수!', '남산타워 야경 사진 찍기', '남산타워', '서울특별시 용산구 남산공원길 105', '2025-08-01 19:30:00', 7, 3, 'https://example.com/photo1000.jpg', 'RECRUITING', 37.5512, 126.9882, 150, 'ART', 't1@aaa.aaa', true, NOW(), NOW()),
('북한산 등반 & 피크닉', '북한산 백운대 등반 후 정상에서 간단한 피크닉을 즐기는 모임입니다.', '북한산 등반', '북한산 국립공원', '서울특별시 도봉구 도봉산길 86', '2025-08-02 08:00:00', 10, 5, 'https://example.com/photo1001.jpg', 'RECRUITING', 37.6593, 127.0142, 300, 'SPORT', 't2@aaa.aaa', true, NOW(), NOW()),
('경복궁 한복 체험', '경복궁에서 한복을 입고 한국의 전통 문화를 체험하는 모임입니다.', '경복궁 한복 입기', '경복궁', '서울특별시 종로구 사직로 161', '2025-08-03 14:00:00', 8, 4, 'https://example.com/photo1002.jpg', 'RECRUITING', 37.5796, 126.9770, 180, 'CULTURE', 't3@aaa.aaa', true, NOW(), NOW()),
('강남 맛집 투어', '강남의 숨겨진 맛집들을 탐방하며 미식의 즐거움을 나누는 모임입니다.', '강남 맛집 탐방', '강남역', '서울특별시 강남구 강남대로 396', '2025-08-04 18:30:00', 6, 2, 'https://example.com/photo1003.jpg', 'RECRUITING', 37.4980, 127.0276, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('보드게임 카페 정복', '신촌 보드게임 카페에서 다양한 보드게임을 즐기는 모임입니다.', '보드게임 데이', '보드게임 카페', '서울특별시 서대문구 연세로 5가길 16', '2025-08-05 15:00:00', 9, 3, 'https://example.com/photo1004.jpg', 'RECRUITING', 37.5592, 126.9372, 240, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('독서 & 토론 클럽', '선정된 책을 읽고 함께 깊이 있는 토론을 나누는 독서 모임입니다.', '책 읽고 토론하기', '종로 도서관', '서울특별시 종로구 사직로9길 7-18', '2025-08-06 10:00:00', 7, 2, 'https://example.com/photo1005.jpg', 'RECRUITING', 37.5750, 126.9691, 180, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('영화 범죄도시4 관람', '최신 영화 범죄도시4를 함께 관람하고 감상을 공유하는 모임입니다.', '영화 보기', 'CGV 용산아이파크몰', '서울특별시 용산구 한강대로23길 55', '2025-08-07 19:00:00', 5, 1, 'https://example.com/photo1006.jpg', 'RECRUITING', 37.5303, 126.9654, 150, 'MOVIE', 't1@aaa.aaa', true, NOW(), NOW()),
('서울숲 스냅사진 촬영', '서울숲에서 자연광을 이용한 인물 스냅사진 촬영 모임입니다.', '서울숲 스냅 찍기', '서울숲', '서울특별시 성동구 뚝섬로 273', '2025-08-08 10:00:00', 6, 2, 'https://example.com/photo1007.jpg', 'RECRUITING', 37.5442, 127.0401, 120, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('자전거 타고 한강 종주', '여의도에서 잠실까지 한강 자전거 도로를 따라 라이딩하는 모임입니다.', '한강 자전거 타기', '여의도 한강공원', '서울특별시 영등포구 여의동로 330', '2025-08-09 09:00:00', 8, 3, 'https://example.com/photo1008.jpg', 'RECRUITING', 37.5283, 126.9329, 210, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('이태원 세계 음식 탐방', '이태원에서 다양한 나라의 이국적인 음식들을 맛보는 모임입니다.', '이태원 음식 맛보기', '이태원역', '서울특별시 용산구 이태원로 179', '2025-08-10 18:00:00', 7, 4, 'https://example.com/photo1009.jpg', 'RECRUITING', 37.5345, 126.9945, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('루미큐브 대회', '루미큐브를 좋아하는 사람들과 함께 실력 겨루기 대회!', '루미큐브 한판', '홍대 입구 보드게임 카페', '서울특별시 마포구 홍익로6길 53', '2025-08-11 14:00:00', 12, 6, 'https://example.com/photo1010.jpg', 'RECRUITING', 37.5566, 126.9234, 180, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('주식 스터디 그룹', '주식 투자에 관심 있는 분들을 위한 기초부터 심화까지 스터디 그룹입니다.', '주식 공부', '강남 토즈 스터디', '서울특별시 강남구 테헤란로 132', '2025-08-12 19:00:00', 10, 5, 'https://example.com/photo1011.jpg', 'RECRUITING', 37.5029, 127.0317, 150, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('뮤지컬 레미제라블 관람', '뮤지컬 레미제라블을 함께 관람하고 감동을 나누는 모임입니다.', '뮤지컬 보기', '블루스퀘어 신한카드홀', '서울특별시 용산구 이태원로 294', '2025-08-13 19:30:00', 8, 3, 'https://example.com/photo1012.jpg', 'RECRUITING', 37.5385, 127.0051, 180, 'CULTURE', 't1@aaa.aaa', true, NOW(), NOW()),
('남산 둘레길 걷기', '남산 둘레길을 따라 천천히 걸으며 서울의 풍경을 감상하는 걷기 모임입니다.', '남산 걷기', '남산공원', '서울특별시 중구 삼일대로 231', '2025-08-14 10:00:00', 7, 2, 'https://example.com/photo1013.jpg', 'RECRUITING', 37.5501, 126.9902, 120, 'SPORT', 't2@aaa.aaa', true, NOW(), NOW()),
('을지로 노포 탐방', '을지로의 오래된 노포들을 찾아다니며 추억의 맛을 즐기는 모임입니다.', '을지로 맛집 탐방', '을지로3가역', '서울특별시 중구 을지로 12', '2025-08-15 17:00:00', 6, 3, 'https://example.com/photo1014.jpg', 'RECRUITING', 37.5663, 126.9918, 90, 'FOOD', 't3@aaa.aaa', true, NOW(), NOW()),
('연극 쉬어 매드니스 관람', '대학로 연극 쉬어 매드니스를 보고 추리하는 재미를 느끼는 모임입니다.', '연극 보기', '대학로', '서울특별시 종로구 대학로 108', '2025-08-01 16:00:00', 5, 1, 'https://example.com/photo1015.jpg', 'RECRUITING', 37.5822, 127.0016, 150, 'MOVIE', 't1@aaa.aaa', true, NOW(), NOW()),
('클라이밍 체험', '실내 클라이밍 센터에서 클라이밍을 배우고 체험하는 모임입니다.', '클라이밍 배우기', '더클라임 홍대', '서울특별시 마포구 양화로 165', '2025-08-02 19:00:00', 8, 4, 'https://example.com/photo1016.jpg', 'RECRUITING', 37.5583, 126.9221, 180, 'SPORT', 't2@aaa.aaa', true, NOW(), NOW()),
('종묘 산책 및 역사 탐방', '유네스코 세계유산 종묘를 산책하며 한국의 역사와 건축을 배우는 모임입니다.', '종묘 걷기', '종묘', '서울특별시 종로구 종로 157', '2025-08-03 11:00:00', 7, 3, 'https://example.com/photo1017.jpg', 'RECRUITING', 37.5750, 126.9942, 120, 'CULTURE', 't3@aaa.aaa', true, NOW(), NOW()),
('디저트 카페 투어', '가로수길의 유명 디저트 카페들을 방문하여 달콤한 시간을 보내는 모임입니다.', '디저트 먹방', '가로수길', '서울특별시 강남구 가로수길 52', '2025-08-04 14:30:00', 6, 2, 'https://example.com/photo1018.jpg', 'RECRUITING', 37.5218, 127.0227, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('영어 회화 스터디', '영어로 자유롭게 대화하며 회화 실력을 향상시키는 스터디 그룹입니다.', '영어 연습', '강남역 스터디룸', '서울특별시 강남구 테헤란로 1길 1', '2025-08-06 10:30:00', 8, 4, 'https://example.com/photo1020.jpg', 'RECRUITING', 37.4980, 127.0276, 120, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('국립중앙박물관 투어', '국립중앙박물관에서 한국의 역사와 예술을 심층적으로 알아보는 모임입니다.', '박물관 관람', '국립중앙박물관', '서울특별시 용산구 서빙고로 137', '2025-08-07 13:00:00', 7, 3, 'https://example.com/photo1021.jpg', 'RECRUITING', 37.5234, 126.9740, 180, 'CULTURE', 't1@aaa.aaa', true, NOW(), NOW()),
('북촌 한옥마을 산책', '북촌 한옥마을을 거닐며 전통 한옥의 아름다움을 만끽하는 모임입니다.', '북촌 걷기', '북촌 한옥마을', '서울특별시 종로구 계동길 37', '2025-08-08 14:00:00', 6, 2, 'https://example.com/photo1022.jpg', 'RECRUITING', 37.5826, 126.9830, 90, 'TRAVEL', 't2@aaa.aaa', true, NOW(), NOW()),
('남대문 시장 맛집 탐방', '남대문 시장의 숨겨진 맛집들을 찾아다니며 다양한 먹거리를 즐기는 모임입니다.', '남대문 맛집', '남대문 시장', '서울특별시 중구 남대문시장4길 21', '2025-08-09 11:30:00', 7, 4, 'https://example.com/photo1023.jpg', 'RECRUITING', 37.5599, 126.9765, 120, 'FOOD', 't3@aaa.aaa', true, NOW(), NOW()),
('방탈출 카페 도전', '친구들과 함께 머리를 맞대고 방탈출 게임에 도전하는 모임입니다.', '방탈출 하기', '강남 방탈출 카페', '서울특별시 강남구 강남대로 422', '2025-08-10 16:00:00', 4, 1, 'https://example.com/photo1024.jpg', 'RECRUITING', 37.5008, 127.0267, 90, 'GAME', 't1@aaa.aaa', true, NOW(), NOW()),
('글쓰기 모임', '자유로운 글쓰기를 통해 생각과 감정을 공유하고 피드백을 주고받는 모임입니다.', '글쓰기 연습', '홍대 카페', '서울특별시 마포구 어울마당로 60', '2025-08-11 19:00:00', 6, 2, 'https://example.com/photo1025.jpg', 'RECRUITING', 37.5576, 126.9246, 150, 'STUDY', 't2@aaa.aaa', true, NOW(), NOW()),
('인사동 전통문화 체험', '인사동에서 전통 차 마시기, 한지 공예 등 한국 전통 문화를 체험하는 모임입니다.', '인사동 문화 체험', '인사동 쌈지길', '서울특별시 종로구 인사동길 44', '2025-08-12 15:00:00', 8, 3, 'https://example.com/photo1026.jpg', 'RECRUITING', 37.5735, 126.9860, 180, 'CULTURE', 't3@aaa.aaa', true, NOW(), NOW()),
('러닝 크루 모집', '여의도 공원에서 함께 뛰며 건강을 챙기는 러닝 크루입니다.', '함께 달리기', '여의도 공원', '서울특별시 영등포구 여의도동 81', '2025-08-13 07:00:00', 10, 5, 'https://example.com/photo1027.jpg', 'RECRUITING', 37.5230, 126.9250, 90, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('을지로 힙지로 카페 투어', '힙지로의 감성적인 카페들을 방문하며 여유로운 시간을 보내는 모임입니다.', '힙지로 카페', '을지로4가역', '서울특별시 중구 을지로 156', '2025-08-14 13:00:00', 5, 2, 'https://example.com/photo1028.jpg', 'RECRUITING', 37.5663, 126.9918, 120, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
('롤 (리그 오브 레전드) 친목전', '리그 오브 레전드를 함께 즐기는 친목전 모임입니다.', '롤 한판', 'PC방', '서울특별시 강서구 강서로 389', '2025-08-15 18:00:00', 5, 3, 'https://example.com/photo1029.jpg', 'RECRUITING', 37.5583, 126.8407, 240, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
('자기 계발 도서 읽기', '자기 계발 도서를 함께 읽고 서로의 경험과 노하우를 공유하는 모임입니다.', '자기 계발', '교보문고 강남점', '서울특별시 서초구 강남대로 465', '2025-08-01 10:00:00', 7, 3, 'https://example.com/photo1030.jpg', 'RECRUITING', 37.5029, 127.0267, 180, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('독립영화 관람 & 토론', '다양한 독립영화를 함께 보고 영화에 대한 깊은 이야기를 나누는 모임입니다.', '독립영화 보기', '인디스페이스', '서울특별시 종로구 돈화문로 13', '2025-08-02 19:00:00', 6, 2, 'https://example.com/photo1031.jpg', 'RECRUITING', 37.5703, 126.9927, 150, 'MOVIE', 't2@aaa.aaa', true, NOW(), NOW()),
('미술관 데이트', '서울 시립 미술관에서 현대 미술 작품을 감상하는 모임입니다.', '미술 관람', '서울 시립 미술관', '서울특별시 중구 덕수궁길 61', '2025-08-03 14:00:00', 5, 1, 'https://example.com/photo1032.jpg', 'RECRUITING', 37.5659, 126.9749, 120, 'ART', 't3@aaa.aaa', true, NOW(), NOW()),
('한강변 조깅', '시원한 한강변을 따라 조깅하며 건강을 챙기는 모임입니다.', '한강 조깅', '뚝섬 한강공원', '서울특별시 광진구 자양동 123', '2025-08-04 06:30:00', 8, 4, 'https://example.com/photo1033.jpg', 'RECRUITING', 37.5348, 127.0673, 90, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('명동 길거리 음식 탐방', '명동의 활기찬 분위기 속에서 다양한 길거리 음식을 맛보는 모임입니다.', '명동 길거리', '명동 거리', '서울특별시 중구 명동길 43', '2025-08-05 17:00:00', 7, 3, 'https://example.com/photo1034.jpg', 'RECRUITING', 37.5610, 126.9859, 120, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
('포켓몬 GO 레이드', '포켓몬 GO 유저들과 함께 강력한 레이드 보스에 도전하는 모임입니다.', '포켓몬 잡기', '올림픽공원', '서울특별시 송파구 올림픽로 424', '2025-08-06 14:00:00', 10, 5, 'https://example.com/photo1035.jpg', 'RECRUITING', 37.5218, 127.1181, 180, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
('코딩 스터디 그룹 (파이썬)', '파이썬 코딩을 함께 공부하고 프로젝트를 진행하는 스터디 그룹입니다.', '파이썬 코딩', '위워크 선릉', '서울특별시 강남구 테헤란로 422', '2025-08-07 10:00:00', 9, 4, 'https://example.com/photo1036.jpg', 'RECRUITING', 37.5045, 127.0487, 210, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('서울 도심 속 고궁 투어', '서울의 아름다운 고궁들을 방문하며 역사를 배우는 모임입니다.', '고궁 탐방', '창덕궁', '서울특별시 종로구 율곡로 99', '2025-08-08 13:00:00', 8, 3, 'https://example.com/photo1037.jpg', 'RECRUITING', 37.5790, 126.9912, 150, 'TRAVEL', 't2@aaa.aaa', true, NOW(), NOW()),
('재즈 바 탐방', '분위기 좋은 재즈 바에서 라이브 공연을 감상하며 힐링하는 모임입니다.', '재즈 듣기', '홍대 재즈 바', '서울특별시 마포구 와우산로 29길 94', '2025-08-09 20:00:00', 6, 2, 'https://example.com/photo1038.jpg', 'RECRUITING', 37.5507, 126.9248, 120, 'CULTURE', 't3@aaa.aaa', true, NOW(), NOW()),
('볼링 번개', '퇴근 후 스트레스를 날릴 수 있는 볼링 번개 모임입니다.', '볼링 치기', '강변 볼링장', '서울특별시 광진구 광나루로56길 85', '2025-08-10 19:00:00', 7, 3, 'https://example.com/photo1039.jpg', 'RECRUITING', 37.5358, 127.0945, 120, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('전통 시장 맛집 탐방', '광장 시장에서 빈대떡, 마약김밥 등 전통 음식을 맛보는 모임입니다.', '광장 시장 맛집', '광장 시장', '서울특별시 종로구 창경궁로 88', '2025-08-11 12:00:00', 6, 2, 'https://example.com/photo1040.jpg', 'RECRUITING', 37.5709, 126.9976, 90, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
('VR 게임 체험', '최신 VR 게임을 함께 체험하며 가상 현실의 세계를 즐기는 모임입니다.', 'VR 게임 하기', 'VR 스퀘어 강남점', '서울특별시 강남구 테헤란로 132', '2025-08-12 15:00:00', 5, 1, 'https://example.com/photo1041.jpg', 'RECRUITING', 37.5029, 127.0317, 120, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
('스페인어 회화 스터디', '스페인어 초급자를 위한 회화 스터디 그룹입니다.', '스페인어 배우기', '강남 어학원', '서울특별시 강남구 강남대로 422', '2025-08-13 10:00:00', 7, 3, 'https://example.com/photo1042.jpg', 'RECRUITING', 37.5008, 127.0267, 120, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('한강 피크닉 & 보드게임', '한강 공원에서 피크닉을 즐기고 보드게임을 하며 여유로운 시간을 보내는 모임입니다.', '한강 피크닉', '잠원 한강공원', '서울특별시 서초구 잠원동 121-12', '2025-08-14 14:00:00', 8, 4, 'https://example.com/photo1043.jpg', 'RECRUITING', 37.5218, 127.0197, 180, 'TRAVEL', 't2@aaa.aaa', true, NOW(), NOW()),
('도예 공방 체험', '도예 공방에서 나만의 도자기를 만들어보는 특별한 체험 모임입니다.', '도자기 만들기', '홍대 도예 공방', '서울특별시 마포구 독막로3길 20', '2025-08-15 15:00:00', 6, 2, 'https://example.com/photo1044.jpg', 'RECRUITING', 37.5492, 126.9189, 180, 'ART', 't3@aaa.aaa', true, NOW(), NOW()),
('풋살 경기', '함께 땀 흘리며 스트레스를 해소하는 풋살 경기 모임입니다.', '풋살 한판', '뚝섬유원지 풋살장', '서울특별시 광진구 자양동 뚝섬로 729', '2025-08-01 19:30:00', 10, 5, 'https://example.com/photo1045.jpg', 'RECRUITING', 37.5348, 127.0673, 120, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('성수동 카페거리 투어', '성수동의 핫한 카페들을 탐방하며 분위기를 즐기는 모임입니다.', '성수동 카페', '성수동 카페거리', '서울특별시 성동구 성수이로7가길 15', '2025-08-02 14:00:00', 6, 3, 'https://example.com/photo1046.jpg', 'RECRUITING', 37.5447, 127.0560, 120, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
('PC방 리그 오브 레전드 친목전', 'PC방에서 리그 오브 레전드를 함께 즐기며 친목을 다지는 모임입니다.', '롤 PC방', '신촌 PC방', '서울특별시 서대문구 신촌로 183', '2025-08-03 17:00:00', 5, 2, 'https://example.com/photo1047.jpg', 'RECRUITING', 37.5584, 126.9367, 180, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
('포토샵 기초 스터디', '포토샵 기초 기능을 배우고 함께 실습하는 스터디 그룹입니다.', '포토샵 배우기', '강남 스터디룸', '서울특별시 강남구 테헤란로 132', '2025-08-04 11:00:00', 8, 4, 'https://example.com/photo1048.jpg', 'RECRUITING', 37.5029, 127.0317, 150, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('영화 인셉션 재관람 & 토론', '명작 인셉션을 다시 보고 심도 깊은 토론을 나누는 모임입니다.', '인셉션 보기', '롯데시네마 월드타워', '서울특별시 송파구 올림픽로 300', '2025-08-05 19:00:00', 7, 3, 'https://example.com/photo1049.jpg', 'RECRUITING', 37.5135, 127.1030, 180, 'MOVIE', 't2@aaa.aaa', true, NOW(), NOW()),
('야경 출사', '서울의 아름다운 야경 명소를 찾아 사진 촬영하는 모임입니다.', '야경 사진', '선유도 공원', '서울특별시 영등포구 선유로 343', '2025-08-06 18:30:00', 6, 2, 'https://example.com/photo1050.jpg', 'RECRUITING', 37.5401, 126.9023, 150, 'ART', 't3@aaa.aaa', true, NOW(), NOW()),
('배드민턴 모임', '실내 배드민턴장에서 함께 운동하며 친목을 다지는 모임입니다.', '배드민턴 치기', '잠실 배드민턴장', '서울특별시 송파구 올림픽로 25', '2025-08-07 19:00:00', 8, 4, 'https://example.com/photo1051.jpg', 'RECRUITING', 37.5145, 127.0545, 120, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('연남동 맛집 투어', '연남동의 개성 넘치는 맛집들을 탐방하며 미식의 즐거움을 나누는 모임입니다.', '연남동 맛집', '연남동', '서울특별시 마포구 동교로 260', '2025-08-08 18:00:00', 7, 3, 'https://example.com/photo1052.jpg', 'RECRUITING', 37.5620, 126.9248, 120, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
('TRPG (테이블탑 롤플레잉 게임)', '새로운 세계를 탐험하고 이야기를 만들어가는 TRPG 모임입니다.', 'TRPG 하기', '신촌 보드게임 카페', '서울특별시 서대문구 연세로 5가길 16', '2025-08-09 14:00:00', 5, 2, 'https://example.com/photo1053.jpg', 'RECRUITING', 37.5592, 126.9372, 240, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
('데이터 분석 기초 스터디', '데이터 분석의 기초를 함께 배우고 실습하는 스터디 그룹입니다.', '데이터 분석', '강남역 스터디룸', '서울특별시 강남구 테헤란로 1길 1', '2025-08-10 10:00:00', 9, 4, 'https://example.com/photo1054.jpg', 'RECRUITING', 37.4980, 127.0276, 180, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('국립극단 연극 관람', '국립극단의 수준 높은 연극을 함께 관람하는 모임입니다.', '연극 보기', '국립극장 달오름극장', '서울특별시 중구 장충단로 59', '2025-08-11 19:30:00', 8, 3, 'https://example.com/photo1055.jpg', 'RECRUITING', 37.5552, 127.0084, 150, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),
('아차산 등산', '아차산에 올라 서울의 시원한 전경을 감상하는 등산 모임입니다.', '아차산 오르기', '아차산', '서울특별시 광진구 워커힐로 177', '2025-08-12 08:30:00', 7, 2, 'https://example.com/photo1056.jpg', 'RECRUITING', 37.5501, 127.1084, 180, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('혜화역 맛집 투어', '대학로 혜화역 주변의 숨겨진 맛집들을 탐방하는 모임입니다.', '혜화 맛집', '혜화역', '서울특별시 종로구 대학로 10길', '2025-08-13 17:30:00', 6, 3, 'https://example.com/photo1057.jpg', 'RECRUITING', 37.5819, 127.0028, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 게임 친목 모임 (배그)', '배틀그라운드를 함께 즐기는 온라인 친목 모임입니다.', '배그 한판', '온라인', '온라인', '2025-08-05 20:00:00', 8, 4, 'https://example.com/photo1094.jpg', 'RECRUITING', 37.5665, 126.9780, 180, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 체스 친목전', '온라인으로 체스를 즐기며 실력을 겨루는 친목 모임입니다.', '온라인 체스', '온라인', '온라인', '2025-08-14 20:00:00', 10, 5, 'https://example.com/photo1058.jpg', 'RECRUITING', 37.5665, 126.9780, 120, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 보드게임 나잇', '온라인으로 보드게임을 함께 즐기는 나잇 모임입니다.', '온라인 보드게임', '온라인', '온라인', '2025-08-14 20:00:00', 10, 5, 'https://example.com/photo1118.jpg', 'RECRUITING', 37.5665, 126.9780, 150, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 게임 친목 모임', '인기 온라인 게임을 함께 즐기며 친목을 다지는 모임입니다.', '온라인 게임하기', 'PC방', '서울특별시 관악구 신림로 340', '2025-08-05 20:00:00', 10, 5, 'https://example.com/photo1019.jpg', 'RECRUITING', 37.4842, 126.9298, 240, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('토익 스터디 (LC & RC)', '토익 점수 향상을 위한 LC, RC 집중 스터디 그룹입니다.', '토익 공부', 'YBM 어학원', '서울특별시 강남구 테헤란로 132', '2025-08-15 10:00:00', 8, 4, 'https://example.com/photo1059.jpg', 'RECRUITING', 37.5029, 127.0317, 150, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('뮤지컬 오페라의 유령 관람', '뮤지컬 오페라의 유령을 함께 관람하며 감동을 나누는 모임입니다.', '오페라의 유령', '샤롯데씨어터', '서울특별시 송파구 올림픽로 240', '2025-08-02 19:00:00', 6, 2, 'https://example.com/photo1061.jpg', 'RECRUITING', 37.5126, 127.1009, 180, 'MOVIE', 't2@aaa.aaa', true, NOW(), NOW()),
('드로잉 클래스 (기초)', '드로잉 기초를 배우고 함께 그림을 그리는 클래스 모임입니다.', '그림 그리기', '홍대 드로잉 카페', '서울특별시 마포구 잔다리로 3안길 42', '2025-08-03 14:00:00', 5, 1, 'https://example.com/photo1062.jpg', 'RECRUITING', 37.5539, 126.9200, 180, 'ART', 't3@aaa.aaa', true, NOW(), NOW()),
('테니스 번개', '테니스를 좋아하는 사람들과 함께 즐기는 테니스 번개 모임입니다.', '테니스 치기', '올림픽공원 테니스장', '서울특별시 송파구 올림픽로 424', '2025-08-04 10:00:00', 8, 4, 'https://example.com/photo1063.jpg', 'RECRUITING', 37.5218, 127.1181, 120, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('수요 미식회 - 한식 편', '수요 미식회에 소개된 한식 맛집을 탐방하는 모임입니다.', '한식 맛집', '종로 한식골목', '서울특별시 종로구 삼일대로 428', '2025-08-05 12:00:00', 7, 3, 'https://example.com/photo1064.jpg', 'RECRUITING', 37.5709, 126.9976, 90, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
('마피아 게임', '친구들과 함께 추리력을 발휘하는 마피아 게임 모임입니다.', '마피아 게임', '신촌 카페', '서울특별시 서대문구 신촌역로 30', '2025-08-06 18:00:00', 10, 5, 'https://example.com/photo1065.jpg', 'RECRUITING', 37.5582, 126.9367, 150, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
('재테크 스터디', '재테크에 대한 정보를 공유하고 함께 공부하는 스터디 그룹입니다.', '재테크 공부', '강남 토즈 스터디', '서울특별시 강남구 테헤란로 132', '2025-08-07 19:00:00', 9, 4, 'https://example.com/photo1066.jpg', 'RECRUITING', 37.5029, 127.0317, 180, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('전시회 관람', '흥미로운 전시회를 함께 관람하고 예술적 영감을 나누는 모임입니다.', '전시회 보기', '대림미술관', '서울특별시 종로구 자하문로4길 21', '2025-08-09 15:00:00', 5, 1, 'https://example.com/photo1068.jpg', 'RECRUITING', 37.5857, 126.9715, 120, 'ART', 't3@aaa.aaa', true, NOW(), NOW()),
('요가 & 명상', '몸과 마음의 평화를 위한 요가와 명상 모임입니다.', '요가하기', '강남 요가 스튜디오', '서울특별시 강남구 논현로 175', '2025-08-10 09:00:00', 7, 3, 'https://example.com/photo1069.jpg', 'RECRUITING', 37.5085, 127.0276, 90, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('카페 탐방 - 연남동 편', '연남동의 개성 있는 카페들을 찾아다니며 커피와 디저트를 즐기는 모임입니다.', '연남동 카페', '연남동 카페거리', '서울특별시 마포구 연남동 240-27', '2025-08-11 14:00:00', 6, 2, 'https://example.com/photo1070.jpg', 'RECRUITING', 37.5620, 126.9248, 120, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
('e스포츠 관람', '인기 e스포츠 경기를 함께 관람하며 열기를 나누는 모임입니다.', 'e스포츠 보기', '롤파크', '서울특별시 종로구 종로 33', '2025-08-12 17:00:00', 10, 5, 'https://example.com/photo1071.jpg', 'RECRUITING', 37.5709, 126.9790, 180, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
('파이썬 웹 개발 스터디', '파이썬으로 웹 개발을 배우고 미니 프로젝트를 진행하는 스터디 그룹입니다.', '웹 개발 공부', '선릉 스터디룸', '서울특별시 강남구 테헤란로 441', '2025-08-13 19:00:00', 9, 4, 'https://example.com/photo1072.jpg', 'RECRUITING', 37.5056, 127.0494, 210, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('뮤지컬 위키드 관람', '뮤지컬 위키드를 함께 관람하며 환상적인 무대를 경험하는 모임입니다.', '위키드 보기', '블루스퀘어 신한카드홀', '서울특별시 용산구 이태원로 294', '2025-08-14 19:30:00', 8, 3, 'https://example.com/photo1073.jpg', 'RECRUITING', 37.5385, 127.0051, 180, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),
('미식 동호회 - 양식 편', '서울 시내 유명 양식 레스토랑을 탐방하는 미식 동호회입니다.', '양식 맛집', '청담동 레스토랑', '서울특별시 강남구 압구정로46길 5', '2025-08-01 18:30:00', 6, 2, 'https://example.com/photo1075.jpg', 'RECRUITING', 37.5255, 127.0396, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('닌텐도 스위치 파티', '닌텐도 스위치 게임을 함께 즐기며 파티를 여는 모임입니다.', '스위치 게임', '홍대 파티룸', '서울특별시 마포구 서교동 369-1', '2025-08-02 14:00:00', 8, 4, 'https://example.com/photo1076.jpg', 'RECRUITING', 37.5507, 126.9248, 180, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('외국어 교환 스터디 (일본어)', '일본어를 배우고 싶은 사람들과 일본어로 대화하는 스터디 모임입니다.', '일본어 공부', '종로 어학원', '서울특별시 종로구 종로 78', '2025-08-03 11:00:00', 7, 3, 'https://example.com/photo1077.jpg', 'RECRUITING', 37.5700, 126.9800, 120, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('한강 유람선 야경 투어', '한강 유람선을 타고 서울의 아름다운 야경을 감상하는 투어 모임입니다.', '한강 유람선', '여의도 한강공원 유람선 선착장', '서울특별시 영등포구 여의동로 280', '2025-08-04 19:00:00', 10, 5, 'https://example.com/photo1078.jpg', 'RECRUITING', 37.5270, 126.9360, 90, 'TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),
('국립현대미술관 관람', '국립현대미술관에서 현대 미술 작품을 감상하는 모임입니다.', '현대미술 관람', '국립현대미술관 서울관', '서울특별시 종로구 삼청로 30', '2025-08-05 14:00:00', 6, 2, 'https://example.com/photo1079.jpg', 'RECRUITING', 37.5772, 126.9805, 150, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('인도어 스포츠 - 탁구', '실내 탁구장에서 스트레스를 풀 수 있는 탁구 모임입니다.', '탁구 치기', '강남 탁구장', '서울특별시 강남구 역삼동 736-23', '2025-08-06 19:00:00', 8, 4, 'https://example.com/photo1080.jpg', 'RECRUITING', 37.5029, 127.0276, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('삼청동 브런치 & 갤러리', '삼청동에서 브런치를 즐기고 갤러리 투어를 하는 모임입니다.', '삼청동 브런치', '삼청동', '서울특별시 종로구 삼청로 7길', '2025-08-07 11:00:00', 5, 2, 'https://example.com/photo1081.jpg', 'RECRUITING', 37.5857, 126.9715, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('포커 친목 게임', '포커를 좋아하는 사람들과 함께 즐기는 친목 게임 모임입니다.', '포커 치기', '강남 보드게임 카페', '서울특별시 강남구 강남대로 422', '2025-08-08 20:00:00', 6, 3, 'https://example.com/photo1082.jpg', 'RECRUITING', 37.5008, 127.0267, 180, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('웹 디자인 기초 스터디', '웹 디자인의 기본 원리와 툴을 배우는 스터디 그룹입니다.', '웹 디자인', '신촌 스터디룸', '서울특별시 서대문구 신촌로 183', '2025-08-09 10:00:00', 9, 4, 'https://example.com/photo1083.jpg', 'RECRUITING', 37.5584, 126.9367, 150, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('서울 시티투어 버스', '서울 시티투어 버스를 타고 주요 명소를 둘러보는 모임입니다.', '서울 투어', '광화문역', '서울특별시 종로구 세종대로 172', '2025-08-10 13:00:00', 10, 5, 'https://example.com/photo1084.jpg', 'RECRUITING', 37.5760, 126.9769, 210, 'TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),
('K-POP 댄스 배우기', '최신 K-POP 댄스를 함께 배우고 즐기는 모임입니다.', 'K-POP 댄스', '홍대 댄스 스튜디오', '서울특별시 마포구 서교동 369-1', '2025-08-11 16:00:00', 8, 3, 'https://example.com/photo1085.jpg', 'RECRUITING', 37.5507, 126.9248, 120, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),
('축구 친선 경기', '축구를 좋아하는 사람들과 함께 즐기는 친선 경기 모임입니다.', '축구 한판', '효창운동장', '서울특별시 용산구 백범로 321', '2025-08-12 19:00:00', 12, 6, 'https://example.com/photo1086.jpg', 'RECRUITING', 37.5458, 126.9634, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('수제 맥주 탐방', '서울 시내 유명 수제 맥주 브루어리들을 탐방하는 모임입니다.', '수제 맥주', '이태원 맥주', '서울특별시 용산구 이태원로27가길 55', '2025-08-13 18:00:00', 7, 3, 'https://example.com/photo1087.jpg', 'RECRUITING', 37.5345, 126.9945, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('보드게임 마라톤', '하루 종일 다양한 보드게임을 즐기는 보드게임 마라톤 모임입니다.', '보드게임', '강남 보드게임 카페', '서울특별시 강남구 강남대로 422', '2025-08-14 13:00:00', 9, 4, 'https://example.com/photo1088.jpg', 'RECRUITING', 37.5008, 127.0267, 300, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('외국어 회화 스터디 (영어)', '영어로 자유롭게 대화하며 회화 실력을 향상시키는 스터디 그룹입니다.', '영어 연습', '신촌 스터디룸', '서울특별시 서대문구 신촌로 183', '2025-08-15 10:30:00', 8, 4, 'https://example.com/photo1089.jpg', 'RECRUITING', 37.5584, 126.9367, 120, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('궁궐 야간 특별 관람', '창덕궁 달빛기행 등 궁궐 야간 특별 관람을 함께 즐기는 모임입니다.', '궁궐 야경', '창경궁', '서울특별시 종로구 창경궁로 185', '2025-08-01 19:00:00', 6, 2, 'https://example.com/photo1090.jpg', 'RECRUITING', 37.5786, 126.9904, 90, 'TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),
('수채화 클래스', '수채화 기초를 배우고 아름다운 풍경화를 그리는 클래스 모임입니다.', '수채화 그리기', '홍대 화실', '서울특별시 마포구 와우산로 29길 56', '2025-08-02 15:00:00', 5, 1, 'https://example.com/photo1091.jpg', 'RECRUITING', 37.5507, 126.9248, 180, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('등산 동호회 - 관악산', '관악산 등반을 통해 건강과 친목을 다지는 등산 동호회입니다.', '관악산 등산', '관악산', '서울특별시 관악구 신림동 산56-1', '2025-08-03 08:00:00', 10, 5, 'https://example.com/photo1092.jpg', 'RECRUITING', 37.4475, 126.9634, 240, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('디저트 맛집 투어 - 가로수길', '가로수길의 유명 디저트 맛집들을 방문하는 모임입니다.', '디저트 먹방', '가로수길', '서울특별시 강남구 가로수길 52', '2025-08-04 14:00:00', 7, 3, 'https://example.com/photo1093.jpg', 'RECRUITING', 37.5218, 127.0227, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('독서 모임 - 베스트셀러', '최신 베스트셀러를 읽고 함께 토론하는 독서 모임입니다.', '책 읽기', '강남 교보문고', '서울특별시 서초구 강남대로 465', '2025-08-06 19:00:00', 6, 2, 'https://example.com/photo1095.jpg', 'RECRUITING', 37.5029, 127.0267, 150, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('영화 아바타 리마스터링 관람', 'IMAX로 아바타 리마스터링을 관람하는 모임입니다.', '아바타 보기', 'IMAX 용산', '서울특별시 용산구 한강대로23길 55', '2025-08-07 19:00:00', 7, 3, 'https://example.com/photo1096.jpg', 'RECRUITING', 37.5303, 126.9654, 210, 'MOVIE', 't1@aaa.aaa', true, NOW(), NOW()),
('사진 동호회 - 인물 촬영', '야외에서 인물 사진 촬영 기법을 배우고 실습하는 모임입니다.', '인물 사진', '서울숲', '서울특별시 성동구 뚝섬로 273', '2025-08-08 10:00:00', 6, 2, 'https://example.com/photo1097.jpg', 'RECRUITING', 37.5442, 127.0401, 120, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('농구 친선 경기', '농구를 좋아하는 사람들과 함께 즐기는 친선 경기 모임입니다.', '농구 한판', '잠실실내체육관', '서울특별시 송파구 올림픽로 25', '2025-08-09 14:00:00', 10, 5, 'https://example.com/photo1098.jpg', 'RECRUITING', 37.5145, 127.0545, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('전통주 시음회', '다양한 전통주를 맛보고 한국의 술 문화를 경험하는 모임입니다.', '전통주 맛보기', '북촌 한옥마을', '서울특별시 종로구 계동길 37', '2025-08-10 17:00:00', 7, 3, 'https://example.com/photo1099.jpg', 'RECRUITING', 37.5826, 126.9830, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('신촌 버스킹 구경', '신촌 연세로에서 펼쳐지는 버스킹 공연을 함께 구경하는 모임입니다.', '버스킹 구경', '신촌 연세로', '서울특별시 서대문구 연세로 1', '2025-08-11 18:00:00', 8, 4, 'https://example.com/photo1100.jpg', 'RECRUITING', 37.5583, 126.9367, 120, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),
('서울 근교 계곡 트래킹', '서울 근교의 아름다운 계곡을 따라 트래킹하는 모임입니다.', '계곡 트래킹', '수락산 계곡', '서울특별시 노원구 동일로242길 96', '2025-08-12 09:00:00', 7, 3, 'https://example.com/photo1101.jpg', 'RECRUITING', 37.6775, 127.0854, 210, 'TRAVEL', 't3@aaa.aaa', true, NOW(), NOW()),
('팝아트 체험 클래스', '쉽고 재미있게 팝아트 작품을 만들어보는 체험 클래스입니다.', '팝아트 만들기', '강남 팝아트 공방', '서울특별시 강남구 강남대로 422', '2025-08-13 15:00:00', 6, 2, 'https://example.com/photo1102.jpg', 'RECRUITING', 37.5008, 127.0267, 180, 'ART', 't1@aaa.aaa', true, NOW(), NOW()),
('러닝 크루 - 남산', '남산 둘레길을 함께 뛰는 러닝 크루 모임입니다.', '남산 러닝', '남산공원', '서울특별시 중구 삼일대로 231', '2025-08-14 07:00:00', 9, 4, 'https://example.com/photo1103.jpg', 'RECRUITING', 37.5501, 126.9902, 90, 'SPORT', 't2@aaa.aaa', true, NOW(), NOW()),
('망원동 레트로 감성 카페 투어', '망원동의 레트로 감성 가득한 카페들을 탐방하는 모임입니다.', '망원동 카페', '망원동 카페거리', '서울특별시 마포구 포은로 106', '2025-08-15 14:00:00', 5, 2, 'https://example.com/photo1104.jpg', 'RECRUITING', 37.5562, 126.9079, 120, 'FOOD', 't3@aaa.aaa', true, NOW(), NOW()),
('오버워치 친목전', '오버워치를 함께 즐기며 친목을 다지는 모임입니다.', '오버워치 한판', 'PC방', '서울특별시 종로구 종로 70', '2025-08-01 18:00:00', 8, 4, 'https://example.com/photo1105.jpg', 'RECRUITING', 37.5700, 126.9800, 180, 'GAME', 't1@aaa.aaa', true, NOW(), NOW()),
('주식 초보 스터디', '주식 투자를 처음 시작하는 분들을 위한 기초 스터디 그룹입니다.', '주식 기초', '선릉 스터디룸', '서울특별시 강남구 테헤란로 441', '2025-08-02 10:00:00', 7, 3, 'https://example.com/photo1106.jpg', 'RECRUITING', 37.5056, 127.0494, 150, 'STUDY', 't2@aaa.aaa', true, NOW(), NOW()),

('독립서점 탐방 & 독서', '독립서점을 방문하여 숨겨진 보물을 찾고 함께 독서하는 모임입니다.', '독립서점 찾기', '홍대 독립서점', '서울특별시 마포구 독막로7길 27', '2025-08-04 14:00:00', 6, 2, 'https://example.com/photo1108.jpg', 'RECRUITING', 37.5492, 126.9189, 120, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('재즈 공연 관람', '서울 시내 재즈 클럽에서 라이브 재즈 공연을 감상하는 모임입니다.', '재즈 보기', '이태원 재즈 클럽', '서울특별시 용산구 이태원로 224', '2025-08-05 20:00:00', 5, 1, 'https://example.com/photo1109.jpg', 'RECRUITING', 37.5345, 126.9945, 120, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),
('클라이밍 초보 체험', '클라이밍을 처음 접하는 분들을 위한 초보 체험 모임입니다.', '클라이밍 배우기', '더클라임 신사', '서울특별시 강남구 논현로 175', '2025-08-06 19:00:00', 7, 3, 'https://example.com/photo1110.jpg', 'RECRUITING', 37.5085, 127.0276, 180, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('수플레 팬케이크 맛집 투어', '홍대의 유명 수플레 팬케이크 맛집들을 탐방하는 모임입니다.', '수플레 먹방', '홍대 카페거리', '서울특별시 마포구 어울마당로 136-12', '2025-08-07 15:00:00', 6, 2, 'https://example.com/photo1111.jpg', 'RECRUITING', 37.5576, 126.9246, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('보드게임 개발 모임', '새로운 보드게임을 기획하고 개발하는 모임입니다.', '보드게임 개발', '강남 보드게임 카페', '서울특별시 강남구 강남대로 422', '2025-08-08 16:00:00', 5, 2, 'https://example.com/photo1112.jpg', 'RECRUITING', 37.5008, 127.0267, 180, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('파이썬 데이터 시각화 스터디', '파이썬으로 데이터 시각화 라이브러리를 배우는 스터디 그룹입니다.', '데이터 시각화', '강남역 스터디룸', '서울특별시 강남구 테헤란로 1길 1', '2025-08-09 10:00:00', 8, 4, 'https://example.com/photo1113.jpg', 'RECRUITING', 37.4980, 127.0276, 150, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('영화 밀수 관람', '최신 한국 영화 밀수를 함께 관람하고 감상을 공유하는 모임입니다.', '밀수 보기', '메가박스 코엑스', '서울특별시 강남구 봉은사로 524', '2025-08-10 19:00:00', 7, 3, 'https://example.com/photo1114.jpg', 'RECRUITING', 37.5100, 127.0583, 150, 'MOVIE', 't1@aaa.aaa', true, NOW(), NOW()),
('연필 소묘 기초 클래스', '연필 소묘의 기본을 배우고 정물화를 그리는 클래스 모임입니다.', '소묘 배우기', '신촌 화실', '서울특별시 서대문구 연세로 5다길 20', '2025-08-11 14:00:00', 6, 2, 'https://example.com/photo1115.jpg', 'RECRUITING', 37.5592, 126.9372, 180, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),

('종로 곱창 맛집 탐방', '종로의 유명 곱창 맛집들을 찾아다니며 곱창과 소주를 즐기는 모임입니다.', '종로 곱창', '종로 곱창골목', '서울특별시 종로구 종로17길 31', '2025-08-13 18:30:00', 7, 3, 'https://example.com/photo1117.jpg', 'RECRUITING', 37.5709, 126.9976, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('N잡러를 위한 스터디', 'N잡러를 꿈꾸는 사람들을 위한 정보 공유 및 스터디 그룹입니다.', 'N잡 스터디', '강남 토즈 스터디', '서울특별시 강남구 테헤란로 132', '2025-08-15 19:00:00', 9, 4, 'https://example.com/photo1119.jpg', 'RECRUITING', 37.5029, 127.0317, 180, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('서울 벚꽃 명소 투어 (2025)', '2025년 서울 벚꽃 명소를 찾아다니며 벚꽃을 감상하는 모임입니다.', '벚꽃 투어', '여의도 윤중로', '서울특별시 영등포구 여의서로', '2025-08-01 10:00:00', 8, 4, 'https://example.com/photo1120.jpg', 'RECRUITING', 37.5303, 126.9138, 120, 'TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),
('클래식 공연 관람', '예술의전당에서 클래식 공연을 함께 관람하는 모임입니다.', '클래식 보기', '예술의전당 콘서트홀', '서울특별시 서초구 남부순환로 2406', '2025-08-02 19:30:00', 7, 3, 'https://example.com/photo1121.jpg', 'RECRUITING', 37.4795, 127.0175, 150, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),
('러닝 크루 - 잠실', '잠실 한강공원을 함께 뛰는 러닝 크루 모임입니다.', '잠실 러닝', '잠실 한강공원', '서울특별시 송파구 잠실동 1-1', '2025-08-03 07:00:00', 10, 5, 'https://example.com/photo1122.jpg', 'RECRUITING', 37.5145, 127.0545, 90, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('서촌 맛집 탐방', '서촌의 고즈넉한 분위기 속 숨겨진 맛집들을 탐방하는 모임입니다.', '서촌 맛집', '서촌', '서울특별시 종로구 자하문로7길 19', '2025-08-04 18:00:00', 6, 2, 'https://example.com/photo1123.jpg', 'RECRUITING', 37.5760, 126.9710, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('PC 게임 대회 (롤, 배그 등)', 'PC 게임을 좋아하는 사람들과 함께 팀을 이루어 대회에 참가하는 모임입니다.', 'PC 게임 대회', '강남 PC방', '서울특별시 강남구 테헤란로 132', '2025-08-05 14:00:00', 12, 6, 'https://example.com/photo1124.jpg', 'RECRUITING', 37.5029, 127.0317, 300, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('경제 스터디 (주식, 부동산)', '경제 뉴스 분석 및 주식, 부동산 등 재테크를 공부하는 스터디 그룹입니다.', '경제 공부', '홍대 스터디룸', '서울특별시 마포구 양화로 165', '2025-08-06 19:00:00', 9, 4, 'https://example.com/photo1125.jpg', 'RECRUITING', 37.5583, 126.9221, 180, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('영화 기생충 재관람 & 심층 분석', '영화 기생충을 다시 보고 영화적 의미를 심층 분석하는 모임입니다.', '기생충 보기', 'CGV 용산아이파크몰', '서울특별시 용산구 한강대로23길 55', '2025-08-07 19:00:00', 6, 2, 'https://example.com/photo1126.jpg', 'RECRUITING', 37.5303, 126.9654, 180, 'MOVIE', 't1@aaa.aaa', true, NOW(), NOW()),
('팝 아트 전시회 관람', '팝 아트 전시회를 함께 관람하고 예술적 영감을 나누는 모임입니다.', '팝아트 관람', '서울 시립 미술관', '서울특별시 중구 덕수궁길 61', '2025-08-08 15:00:00', 5, 1, 'https://example.com/photo1127.jpg', 'RECRUITING', 37.5659, 126.9749, 120, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('등산 동호회 - 인왕산', '인왕산 등반을 통해 서울의 아름다운 풍경을 감상하는 등산 동호회입니다.', '인왕산 등산', '인왕산', '서울특별시 종로구 인왕산로 1', '2025-08-09 09:00:00', 7, 3, 'https://example.com/photo1128.jpg', 'RECRUITING', 37.5750, 126.9691, 150, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('성수동 갤러리 카페 투어', '성수동의 개성 있는 갤러리 카페들을 탐방하며 예술과 커피를 즐기는 모임입니다.', '성수동 갤러리', '성수동 갤러리 카페', '서울특별시 성동구 성수이로22길 37', '2025-08-10 14:00:00', 6, 2, 'https://example.com/photo1129.jpg', 'RECRUITING', 37.5447, 127.0560, 120, 'ART', 't1@aaa.aaa', true, NOW(), NOW()),
('영어 원서 읽기 모임', '영어 원서를 함께 읽고 토론하며 영어 실력을 향상시키는 모임입니다.', '영어 원서', '강남 교보문고', '서울특별시 서초구 강남대로 465', '2025-08-12 19:00:00', 7, 3, 'https://example.com/photo1131.jpg', 'RECRUITING', 37.5029, 127.0267, 150, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('서울 야경 명소 탐방', '서울의 숨겨진 야경 명소들을 찾아다니며 아름다운 풍경을 감상하는 모임입니다.', '서울 야경', '낙산공원', '서울특별시 종로구 낙산길 41', '2025-08-13 19:00:00', 6, 2, 'https://example.com/photo1132.jpg', 'RECRUITING', 37.5840, 127.0090, 120, 'TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),
('뮤지컬 맘마미아 관람', '뮤지컬 맘마미아를 함께 관람하고 즐거운 시간을 보내는 모임입니다.', '맘마미아 보기', '블루스퀘어 신한카드홀', '서울특별시 용산구 이태원로 294', '2025-08-14 19:30:00', 8, 4, 'https://example.com/photo1133.jpg', 'RECRUITING', 37.5385, 127.0051, 180, 'MOVIE', 't2@aaa.aaa', true, NOW(), NOW()),
('스트릿 댄스 배우기', '스트릿 댄스 기초 동작을 배우고 함께 즐기는 모임입니다.', '스트릿 댄스', '홍대 댄스 스튜디오', '서울특별시 마포구 서교동 369-1', '2025-08-15 16:00:00', 7, 3, 'https://example.com/photo1134.jpg', 'RECRUITING', 37.5507, 126.9248, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('홍대 길거리 음식 투어', '홍대의 활기찬 길거리에서 다양한 길거리 음식을 맛보는 모임입니다.', '홍대 길거리', '홍대입구역 9번 출구', '서울특별시 마포구 양화로 160', '2025-08-01 17:00:00', 6, 2, 'https://example.com/photo1135.jpg', 'RECRUITING', 37.5576, 126.9246, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('모바일 게임 친목전', '인기 모바일 게임을 함께 즐기며 친목을 다지는 모임입니다.', '모바일 게임하기', '강남 카페', '서울특별시 강남구 강남대로 396', '2025-08-02 14:00:00', 9, 4, 'https://example.com/photo1136.jpg', 'RECRUITING', 37.4980, 127.0276, 150, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('직무 역량 강화 스터디', '직무 역량 강화를 위한 전문 지식 및 스킬을 공부하는 스터디 그룹입니다.', '직무 스터디', '선릉 스터디룸', '서울특별시 강남구 테헤란로 441', '2025-08-03 10:00:00', 8, 4, 'https://example.com/photo1137.jpg', 'RECRUITING', 37.5056, 127.0494, 180, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('서울 한강 불꽃 축제 관람 (2025)', '2025년 서울 세계 불꽃 축제를 함께 관람하는 모임입니다.', '불꽃 축제', '여의도 한강공원', '서울특별시 영등포구 여의동로 330', '2025-08-04 18:00:00', 10, 5, 'https://example.com/photo1138.jpg', 'RECRUITING', 37.5283, 126.9329, 120, 'CULTURE', 't1@aaa.aaa', true, NOW(), NOW()),
('클레이 아트 체험', '클레이를 이용하여 귀여운 소품을 만들어보는 체험 모임입니다.', '클레이 만들기', '홍대 공방', '서울특별시 마포구 와우산로 29길 94', '2025-08-05 15:00:00', 6, 2, 'https://example.com/photo1139.jpg', 'RECRUITING', 37.5507, 126.9248, 180, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('주말 아침 조깅 & 브런치', '주말 아침에 조깅 후 맛있는 브런치를 즐기는 모임입니다.', '조깅 & 브런치', '서울숲', '서울특별시 성동구 뚝섬로 273', '2025-08-06 08:00:00', 7, 3, 'https://example.com/photo1140.jpg', 'RECRUITING', 37.5442, 127.0401, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('을지로 골목길 맛집 탐방', '을지로의 숨겨진 골목길 맛집들을 찾아다니는 모임입니다.', '을지로 골목길', '을지로 골목', '서울특별시 중구 을지로 12', '2025-08-07 18:30:00', 5, 2, 'https://example.com/photo1141.jpg', 'RECRUITING', 37.5663, 126.9918, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 게임 친목 모임 (스타크래프트)', '스타크래프트를 함께 즐기며 친목을 다지는 온라인 모임입니다.', '스타 한판', '온라인', '온라인', '2025-08-11 20:00:00', 8, 4, 'https://example.com/photo1130.jpg', 'RECRUITING', 37.5665, 126.9780, 180, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 보드게임 나잇 (아발론)', '아발론을 좋아하는 사람들과 함께 즐기는 온라인 보드게임 나잇입니다.', '아발론 한판', '온라인', '온라인', '2025-08-08 20:00:00', 10, 5, 'https://example.com/photo1142.jpg', 'RECRUITING', 37.5665, 126.9780, 120, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 방 탈출 게임', '온라인으로 함께 즐기는 방 탈출 게임 모임입니다.', '온라인 방탈출', '온라인', '온라인', '2025-08-05 20:00:00', 5, 2, 'https://example.com/photo1154.jpg', 'RECRUITING', 37.5665, 126.9780, 90, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('취업 스터디 그룹', '취업 준비생들을 위한 자소서 첨삭 및 면접 스터디 그룹입니다.', '취업 스터디', '강남역 스터디룸', '서울특별시 강남구 테헤란로 1길 1', '2025-08-09 10:00:00', 8, 4, 'https://example.com/photo1143.jpg', 'RECRUITING', 37.4980, 127.0276, 180, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('영화 탑건: 매버릭 재관람', 'IMAX로 탑건: 매버릭을 재관람하며 비행 액션을 즐기는 모임입니다.', '탑건 보기', 'IMAX 용산', '서울특별시 용산구 한강대로23길 55', '2025-08-10 19:00:00', 7, 3, 'https://example.com/photo1144.jpg', 'RECRUITING', 37.5303, 126.9654, 150, 'MOVIE', 't1@aaa.aaa', true, NOW(), NOW()),
('서울 거리 사진 촬영', '서울의 다양한 거리 풍경을 담는 사진 촬영 모임입니다.', '거리 사진', '명동 거리', '서울특별시 중구 명동길 43', '2025-08-11 14:00:00', 6, 2, 'https://example.com/photo1145.jpg', 'RECRUITING', 37.5610, 126.9859, 120, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('실내 골프 스크린', '실내 골프 스크린에서 함께 라운딩하며 친목을 다지는 모임입니다.', '골프 스크린', '강남 골프존', '서울특별시 강남구 테헤란로 406', '2025-08-12 19:00:00', 5, 2, 'https://example.com/photo1146.jpg', 'RECRUITING', 37.5029, 127.0317, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('전통차 체험 & 다도', '인사동에서 전통차를 맛보고 다도 문화를 체험하는 모임입니다.', '전통차', '인사동 전통찻집', '서울특별시 종로구 인사동길 19-17', '2025-08-13 15:00:00', 6, 3, 'https://example.com/photo1147.jpg', 'RECRUITING', 37.5735, 126.9860, 90, 'CULTURE', 't1@aaa.aaa', true, NOW(), NOW()),
('방 탈출 카페 연합', '여러 방 탈출 카페를 연달아 도전하는 연합 모임입니다.', '방탈출 연합', '신촌 방탈출 카페', '서울특별시 서대문구 연세로 5가길 16', '2025-08-14 13:00:00', 8, 4, 'https://example.com/photo1148.jpg', 'RECRUITING', 37.5592, 126.9372, 240, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('포트폴리오 제작 스터디', '디자인, 개발 등 포트폴리오 제작에 필요한 스킬을 공부하는 스터디 그룹입니다.', '포트폴리오', '홍대 스터디룸', '서울특별시 마포구 어울마당로 60', '2025-08-15 10:00:00', 9, 4, 'https://example.com/photo1149.jpg', 'RECRUITING', 37.5576, 126.9246, 180, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('서울 역사 탐방 - 경복궁 야간개장', '경복궁 야간개장을 통해 서울의 역사를 배우는 모임입니다.', '경복궁 야간', '경복궁', '서울특별시 종로구 사직로 161', '2025-08-01 19:00:00', 10, 5, 'https://example.com/photo1150.jpg', 'RECRUITING', 37.5796, 126.9770, 90, 'TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),
('클래식 음악 감상 모임', '다양한 클래식 음악을 함께 감상하고 이야기 나누는 모임입니다.', '클래식 감상', '강남 음악 감상실', '서울특별시 강남구 논현로 175', '2025-08-02 20:00:00', 7, 3, 'https://example.com/photo1151.jpg', 'RECRUITING', 37.5085, 127.0276, 120, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),
('실내 암벽등반 - 리드 클라이밍', '리드 클라이밍을 배우고 실력을 향상시키는 모임입니다.', '리드 클라이밍', '더클라임 신사', '서울특별시 강남구 논현로 175', '2025-08-03 16:00:00', 8, 4, 'https://example.com/photo1152.jpg', 'RECRUITING', 37.5085, 127.0276, 180, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('동대문 닭한마리 맛집 탐방', '동대문의 유명 닭한마리 맛집들을 탐방하는 모임입니다.', '닭한마리 먹방', '동대문 닭한마리 골목', '서울특별시 종로구 종로40가길 10', '2025-08-04 18:00:00', 6, 2, 'https://example.com/photo1153.jpg', 'RECRUITING', 37.5709, 126.9976, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('엑셀 기초 스터디', '엑셀의 기초 기능을 배우고 업무에 활용하는 스터디 그룹입니다.', '엑셀 배우기', '신촌 스터디룸', '서울특별시 서대문구 신촌로 183', '2025-08-06 10:00:00', 9, 4, 'https://example.com/photo1155.jpg', 'RECRUITING', 37.5584, 126.9367, 150, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('대학로 연극 데이트', '대학로에서 최신 연극을 함께 관람하는 데이트 모임입니다.', '연극 데이트', '대학로', '서울특별시 종로구 대학로 108', '2025-08-07 19:00:00', 4, 1, 'https://example.com/photo1156.jpg', 'RECRUITING', 37.5822, 127.0016, 150, 'MOVIE', 't1@aaa.aaa', true, NOW(), NOW()),
('만화 카페 & 드로잉', '만화 카페에서 자유롭게 만화를 보고 그림을 그리는 모임입니다.', '만화 & 그림', '홍대 만화 카페', '서울특별시 마포구 어울마당로 60', '2025-08-08 14:00:00', 6, 2, 'https://example.com/photo1157.jpg', 'RECRUITING', 37.5576, 126.9246, 180, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('실내 테니스 레슨', '실내 테니스장에서 테니스 레슨을 받고 게임을 즐기는 모임입니다.', '테니스 레슨', '잠실 실내테니스장', '서울특별시 송파구 올림픽로 25', '2025-08-09 10:00:00', 7, 3, 'https://example.com/photo1158.jpg', 'RECRUITING', 37.5145, 127.0545, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('경리단길 맛집 & 루프탑', '경리단길의 맛집에서 식사하고 루프탑 카페에서 야경을 즐기는 모임입니다.', '경리단길 투어', '경리단길', '서울특별시 용산구 회나무로 44길', '2025-08-10 18:30:00', 6, 3, 'https://example.com/photo1159.jpg', 'RECRUITING', 37.5414, 126.9897, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('자격증 스터디 그룹', '각자 목표하는 자격증을 함께 공부하고 정보 공유하는 그룹입니다.', '자격증 공부', '강남 토즈 스터디', '서울특별시 강남구 테헤란로 132', '2025-08-12 19:00:00', 8, 4, 'https://example.com/photo1161.jpg', 'RECRUITING', 37.5029, 127.0317, 180, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),

('심야 영화 관람', '심야에 영화를 보고 새벽 공기를 마시며 귀가하는 모임입니다.', '심야 영화', 'CGV 홍대', '서울특별시 마포구 양화로 186', '2025-08-14 22:00:00', 6, 2, 'https://example.com/photo1163.jpg', 'RECRUITING', 37.5576, 126.9246, 150, 'MOVIE', 't2@aaa.aaa', true, NOW(), NOW()),
('한강 노을 사진 촬영', '한강에서 아름다운 노을을 담는 사진 촬영 모임입니다.', '한강 노을', '반포한강공원', '서울특별시 서초구 신반포로 11', '2025-08-15 18:00:00', 5, 1, 'https://example.com/photo1164.jpg', 'RECRUITING', 37.5184, 126.9961, 90, 'ART', 't3@aaa.aaa', true, NOW(), NOW()),
('러닝 크루 - 잠원 한강', '잠원 한강공원을 함께 뛰는 러닝 크루 모임입니다.', '잠원 러닝', '잠원 한강공원', '서울특별시 서초구 잠원동 121-12', '2025-08-01 06:30:00', 8, 4, 'https://example.com/photo1165.jpg', 'RECRUITING', 37.5218, 127.0197, 90, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('성수동 핫플 맛집 투어', '성수동의 최신 핫플레이스 맛집들을 탐방하는 모임입니다.', '성수동 핫플', '성수동', '서울특별시 성동구 성수이로 78', '2025-08-02 18:00:00', 7, 3, 'https://example.com/photo1166.jpg', 'RECRUITING', 37.5447, 127.0560, 120, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 TRPG (테이블탑 롤플레잉 게임)', '온라인으로 새로운 세계를 탐험하고 이야기를 만들어가는 TRPG 모임입니다.', '온라인 TRPG', '온라인', '온라인', '2025-08-11 20:00:00', 5, 2, 'https://example.com/photo1160.jpg', 'RECRUITING', 37.5665, 126.9780, 240, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 보드게임 나잇 (스플렌더)', '스플렌더를 좋아하는 사람들과 함께 즐기는 온라인 보드게임 나잇입니다.', '스플렌더 한판', '온라인', '온라인', '2025-08-03 20:00:00', 6, 2, 'https://example.com/photo1167.jpg', 'RECRUITING', 37.5665, 126.9780, 90, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 게임 친목 모임 (발로란트)', '발로란트를 함께 즐기는 온라인 친목 모임입니다.', '발로란트 한판', '온라인', '온라인', '2025-08-09 20:00:00', 8, 4, 'https://example.com/photo1173.jpg', 'RECRUITING', 37.5665, 126.9780, 180, 'GAME', 't3@aaa.aaa', true, NOW(), NOW()),
('IT 트렌드 스터디', '최신 IT 트렌드에 대해 함께 공부하고 토론하는 스터디 그룹입니다.', 'IT 트렌드', '선릉 스터디룸', '서울특별시 강남구 테헤란로 441', '2025-08-04 19:00:00', 9, 4, 'https://example.com/photo1168.jpg', 'RECRUITING', 37.5056, 127.0494, 180, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('서울 야간 도보 투어', '서울의 밤을 걸으며 숨겨진 이야기들을 듣는 야간 도보 투어입니다.', '야간 도보', '청계천', '서울특별시 종로구 청계천로 1', '2025-08-05 19:00:00', 8, 3, 'https://example.com/photo1169.jpg', 'RECRUITING', 37.5694, 127.0003, 120, 'TRAVEL', 't2@aaa.aaa', true, NOW(), NOW()),
('국립중앙박물관 야간 개장', '국립중앙박물관 야간 개장을 통해 한국의 역사를 탐방하는 모임입니다.', '박물관 야간', '국립중앙박물관', '서울특별시 용산구 서빙고로 137', '2025-08-06 19:00:00', 7, 3, 'https://example.com/photo1170.jpg', 'RECRUITING', 37.5234, 126.9740, 120, 'CULTURE', 't3@aaa.aaa', true, NOW(), NOW()),
('실내 배드민턴 레슨', '실내 배드민턴장에서 배드민턴 레슨을 받고 게임을 즐기는 모임입니다.', '배드민턴 레슨', '잠실 배드민턴장', '서울특별시 송파구 올림픽로 25', '2025-08-07 19:00:00', 6, 2, 'https://example.com/photo1171.jpg', 'RECRUITING', 37.5145, 127.0545, 120, 'SPORT', 't1@aaa.aaa', true, NOW(), NOW()),
('광장시장 먹거리 투어', '광장시장의 다양한 먹거리를 맛보는 투어 모임입니다.', '광장시장 먹방', '광장 시장', '서울특별시 종로구 창경궁로 88', '2025-08-08 12:00:00', 7, 3, 'https://example.com/photo1172.jpg', 'RECRUITING', 37.5709, 126.9976, 90, 'FOOD', 't2@aaa.aaa', true, NOW(), NOW()),
('외국어 회화 스터디 (중국어)', '중국어 초급자를 위한 회화 스터디 그룹입니다.', '중국어 공부', '종로 어학원', '서울특별시 종로구 종로 78', '2025-08-10 10:00:00', 9, 4, 'https://example.com/photo1174.jpg', 'RECRUITING', 37.5700, 126.9800, 120, 'STUDY', 't1@aaa.aaa', true, NOW(), NOW()),
('서울의 숨겨진 골목길 투어', '서울의 오래된 골목길들을 탐험하며 숨겨진 매력을 발견하는 모임입니다.', '골목길 투어', '익선동 골목', '서울특별시 종로구 돈화문로11길 30', '2025-08-11 14:00:00', 6, 2, 'https://example.com/photo1175.jpg', 'RECRUITING', 37.5746, 126.9904, 120, 'TRAVEL', 't2@aaa.aaa', true, NOW(), NOW()),
('뮤지컬 데스노트 관람', '뮤지컬 데스노트를 함께 관람하며 스릴 넘치는 스토리를 즐기는 모임입니다.', '데스노트 보기', '충무아트센터 대극장', '서울특별시 중구 퇴계로 387', '2025-08-12 19:30:00', 7, 3, 'https://example.com/photo1176.jpg', 'RECRUITING', 37.5614, 127.0094, 180, 'MOVIE', 't3@aaa.aaa', true, NOW(), NOW()),
('서울 역사 박물관 관람', '서울 역사 박물관에서 서울의 역사를 배우는 모임입니다.', '서울 역사', '서울 역사 박물관', '서울특별시 종로구 새문안로 55', '2025-08-13 13:00:00', 8, 4, 'https://example.com/photo1177.jpg', 'RECRUITING', 37.5701, 126.9710, 150, 'CULTURE', 't1@aaa.aaa', true, NOW(), NOW()),
('실내 양궁 체험', '실내 양궁장에서 양궁을 배우고 체험하는 모임입니다.', '양궁 체험', '강남 양궁 카페', '서울특별시 강남구 테헤란로 132', '2025-08-14 16:00:00', 5, 2, 'https://example.com/photo1178.jpg', 'RECRUITING', 37.5029, 127.0317, 90, 'SPORT', 't2@aaa.aaa', true, NOW(), NOW()),
('강남역 카페거리 투어', '강남역 주변의 트렌디한 카페들을 탐방하는 모임입니다.', '강남역 카페', '강남역 카페거리', '서울특별시 강남구 강남대로 396', '2025-08-15 14:00:00', 6, 2, 'https://example.com/photo1179.jpg', 'RECRUITING', 37.4980, 127.0276, 90, 'FOOD', 't3@aaa.aaa', true, NOW(), NOW()),
('취미 발레 배우기', '취미로 발레를 배우고 우아한 동작을 익히는 모임입니다.', '발레 배우기', '강남 발레 스튜디오', '서울특별시 강남구 논현로 175', '2025-08-02 19:00:00', 7, 3, 'https://example.com/photo1181.jpg', 'RECRUITING', 37.5085, 127.0276, 120, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),

('영화 어벤져스: 엔드게임 재관람', 'IMAX로 어벤져스: 엔드게임을 재관람하는 모임입니다.', '어벤져스 보기', 'IMAX 용산', '서울특별시 용산구 한강대로23길 55', '2025-08-04 19:00:00', 9, 4, 'https://example.com/photo1183.jpg', 'RECRUITING', 37.5303, 126.9654, 210, 'MOVIE', 't1@aaa.aaa', true, NOW(), NOW()),
('성인 취미 미술 - 유화', '유화를 배우고 나만의 작품을 완성하는 성인 취미 미술 모임입니다.', '유화 그리기', '홍대 미술 공방', '서울특별시 마포구 독막로3길 20', '2025-08-05 15:00:00', 6, 2, 'https://example.com/photo1184.jpg', 'RECRUITING', 37.5492, 126.9189, 180, 'ART', 't2@aaa.aaa', true, NOW(), NOW()),
('풋살 번개', '퇴근 후 스트레스를 날릴 수 있는 풋살 번개 모임입니다.', '풋살 한판', '뚝섬유원지 풋살장', '서울특별시 광진구 자양동 뚝섬로 729', '2025-08-06 19:30:00', 10, 5, 'https://example.com/photo1185.jpg', 'RECRUITING', 37.5348, 127.0673, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('익선동 한옥 카페 투어', '익선동의 고즈넉한 한옥 카페들을 탐방하는 모임입니다.', '익선동 카페', '익선동 카페거리', '서울특별시 종로구 수표로28길 17-26', '2025-08-07 14:00:00', 5, 2, 'https://example.com/photo1186.jpg', 'RECRUITING', 37.5746, 126.9904, 120, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 게임 친목 모임 (로스트아크)', '로스트아크를 함께 즐기는 온라인 친목 모임입니다.', '로아 한판', '온라인', '온라인', '2025-08-01 20:00:00', 10, 5, 'https://example.com/photo1180.jpg', 'RECRUITING', 37.5665, 126.9780, 240, 'GAME', 't1@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 보드게임 나잇 (카탄)', '카탄을 좋아하는 사람들과 함께 즐기는 온라인 보드게임 나잇입니다.', '카탄 한판', '온라인', '온라인', '2025-08-08 20:00:00', 7, 3, 'https://example.com/photo1187.jpg', 'RECRUITING', 37.5665, 126.9780, 120, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
-- ('온라인 게임 친목 모임 (오버워치2)', '오버워치2를 함께 즐기는 온라인 친목 모임입니다.', '오버워치2 한판', '온라인', '온라인', '2025-08-14 20:00:00', 8, 4, 'https://example.com/photo1193.jpg', 'RECRUITING', 37.5665, 126.9780, 180, 'GAME', 't2@aaa.aaa', true, NOW(), NOW()),
('CSAT 스터디 그룹', '수능 준비생들을 위한 CSAT 과목별 스터디 그룹입니다.', '수능 공부', '종로 스터디룸', '서울특별시 종로구 종로 78', '2025-08-09 10:00:00', 8, 4, 'https://example.com/photo1188.jpg', 'RECRUITING', 37.5700, 126.9800, 180, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW()),
('서울 야경 버스 투어', '서울의 주요 야경 명소를 둘러보는 버스 투어 모임입니다.', '야경 버스', '광화문역', '서울특별시 종로구 세종대로 172', '2025-08-10 19:00:00', 10, 5, 'https://example.com/photo1189.jpg', 'RECRUITING', 37.5760, 126.9769, 120, 'TRAVEL', 't1@aaa.aaa', true, NOW(), NOW()),
('국립국악원 국악 공연', '국립국악원에서 전통 국악 공연을 함께 관람하는 모임입니다.', '국악 보기', '국립국악원 예악당', '서울특별시 서초구 남부순환로 2364', '2025-08-11 19:30:00', 7, 3, 'https://example.com/photo1190.jpg', 'RECRUITING', 37.4795, 127.0175, 90, 'CULTURE', 't2@aaa.aaa', true, NOW(), NOW()),
('실내 볼링 번개', '실내 볼링장에서 가볍게 볼링을 즐기는 번개 모임입니다.', '볼링 치기', '강변 볼링장', '서울특별시 광진구 광나루로56길 85', '2025-08-12 19:00:00', 6, 2, 'https://example.com/photo1191.jpg', 'RECRUITING', 37.5358, 127.0945, 120, 'SPORT', 't3@aaa.aaa', true, NOW(), NOW()),
('한정식 맛집 탐방', '서울 시내 유명 한정식 맛집들을 탐방하는 모임입니다.', '한정식 맛집', '강남 한정식', '서울특별시 강남구 테헤란로 132', '2025-08-13 18:00:00', 5, 2, 'https://example.com/photo1192.jpg', 'RECRUITING', 37.5029, 127.0317, 90, 'FOOD', 't1@aaa.aaa', true, NOW(), NOW()),
('영어 토론 스터디', '시사 이슈에 대해 영어로 토론하며 회화 실력을 향상시키는 스터디 그룹입니다.', '영어 토론', '강남역 스터디룸', '서울특별시 강남구 테헤란로 1길 1', '2025-08-15 10:30:00', 7, 3, 'https://example.com/photo1194.jpg', 'RECRUITING', 37.4980, 127.0276, 120, 'STUDY', 't3@aaa.aaa', true, NOW(), NOW());

-- Group Hashtag 테이블 데이터 삽입 (30개 그룹용)
INSERT INTO group_hashtag (tag, group_id, created_at, modified_at)
VALUES
-- 그림 그리기 모임 (group_id = 1)
('예술', 1, NOW(), NOW()),
('힐링', 1, NOW(), NOW()),
('창작', 1, NOW(), NOW()),

-- 제주도 여행 동행 (group_id = 2)
('여행', 2, NOW(), NOW()),
('제주도', 2, NOW(), NOW()),
('바다', 2, NOW(), NOW()),

-- 맛집 탐방 모임 (group_id = 3)
('맛집', 3, NOW(), NOW()),
('강남', 3, NOW(), NOW()),
('투어', 3, NOW(), NOW()),

-- 독서 토론 모임 (group_id = 4)
('독서', 4, NOW(), NOW()),
('IT', 4, NOW(), NOW()),
('토론', 4, NOW(), NOW()),

-- 수채화 클래스 (group_id = 5)
('수채화', 5, NOW(), NOW()),
('그림', 5, NOW(), NOW()),
('클래스', 5, NOW(), NOW()),

-- 보드게임 카페 모임 (group_id = 6)
('보드게임', 6, NOW(), NOW()),
('소통', 6, NOW(), NOW()),
('재미', 6, NOW(), NOW()),

-- 뮤지컬 관람 모임 (group_id = 7)
('뮤지컬', 7, NOW(), NOW()),
('문화', 7, NOW(), NOW()),
('라이온킹', 7, NOW(), NOW()),

-- 주말 등산 모임 (group_id = 8)
('등산', 8, NOW(), NOW()),
('건강', 8, NOW(), NOW()),
('주말', 8, NOW(), NOW()),

-- 클래식 콘서트 모임 (group_id = 9)
('클래식', 9, NOW(), NOW()),
('콘서트', 9, NOW(), NOW()),
('음악', 9, NOW(), NOW()),

-- 테니스 레슨 모임 (group_id = 10)
('테니스', 10, NOW(), NOW()),
('레슨', 10, NOW(), NOW()),
('초보자', 10, NOW(), NOW()),

-- 영화 감상 모임 (group_id = 11)
('영화', 11, NOW(), NOW()),
('토론', 11, NOW(), NOW()),
('감상', 11, NOW(), NOW()),

-- 요리 클래스 모임 (group_id = 12)
('요리', 12, NOW(), NOW()),
('파스타', 12, NOW(), NOW()),
('이탈리안', 12, NOW(), NOW()),

-- 사진 촬영 모임 (group_id = 13)
('사진', 13, NOW(), NOW()),
('야경', 13, NOW(), NOW()),
('한강', 13, NOW(), NOW()),

-- 디저트 카페 투어 (group_id = 14)
('디저트', 14, NOW(), NOW()),
('카페', 14, NOW(), NOW()),
('홍대', 14, NOW(), NOW()),

-- 독립영화 상영회 (group_id = 15)
('독립영화', 15, NOW(), NOW()),
('인디', 15, NOW(), NOW()),
('감독', 15, NOW(), NOW()),

-- 캘리그라피 클래스 (group_id = 16)
('캘리그라피', 16, NOW(), NOW()),
('손글씨', 16, NOW(), NOW()),
('예술', 16, NOW(), NOW()),

-- 부산 여행 동행 (group_id = 17)
('부산', 17, NOW(), NOW()),
('여행', 17, NOW(), NOW()),
('바다', 17, NOW(), NOW()),

-- 한식 요리 클래스 (group_id = 18)
('한식', 18, NOW(), NOW()),
('김치찌개', 18, NOW(), NOW()),
('전통', 18, NOW(), NOW()),

-- 영어 회화 모임 (group_id = 19)
('영어', 19, NOW(), NOW()),
('회화', 19, NOW(), NOW()),
('스피킹', 19, NOW(), NOW()),

-- 심야 영화 모임 (group_id = 20)
('심야', 20, NOW(), NOW()),
('영화', 20, NOW(), NOW()),
('밤샘', 20, NOW(), NOW()),

-- 도자기 만들기 (group_id = 21)
('도자기', 21, NOW(), NOW()),
('체험', 21, NOW(), NOW()),
('도예', 21, NOW(), NOW()),

-- 강릉 여행 모임 (group_id = 22)
('강릉', 22, NOW(), NOW()),
('커피', 22, NOW(), NOW()),
('바다', 22, NOW(), NOW()),

-- 브런치 카페 모임 (group_id = 23)
('브런치', 23, NOW(), NOW()),
('카페', 23, NOW(), NOW()),
('을지로', 23, NOW(), NOW()),

-- 배드민턴 동호회 (group_id = 24)
('배드민턴', 24, NOW(), NOW()),
('운동', 24, NOW(), NOW()),
('동호회', 24, NOW(), NOW()),

-- 게임 대회 모임 (group_id = 25)
('게임', 25, NOW(), NOW()),
('LOL', 25, NOW(), NOW()),
('토너먼트', 25, NOW(), NOW()),

-- 재즈 바 투어 (group_id = 26)
('재즈', 26, NOW(), NOW()),
('바', 26, NOW(), NOW()),
('음악', 26, NOW(), NOW()),

-- 파이썬 스터디 (group_id = 27)
('파이썬', 27, NOW(), NOW()),
('프로그래밍', 27, NOW(), NOW()),
('스터디', 27, NOW(), NOW()),

-- 애니메이션 상영회 (group_id = 28)
('애니메이션', 28, NOW(), NOW()),
('지브리', 28, NOW(), NOW()),
('영화제', 28, NOW(), NOW()),

-- 일본 여행 준비 (group_id = 29)
('일본', 29, NOW(), NOW()),
('여행', 29, NOW(), NOW()),
('플래닝', 29, NOW(), NOW()),

-- 마라톤 준비 모임 (group_id = 30)
('마라톤', 30, NOW(), NOW()),
('러닝', 30, NOW(), NOW()),
('훈련', 30, NOW(), NOW());

-- Participant 테이블 데이터 삽입 (리더들 자동 참여)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES
-- t1@aaa.aaa 리더 모임들 (group_id: 1,2,3,4,5)
('LEADER', 'APPROVED', 't1@aaa.aaa', 1, NOW(), NOW()), -- 그림 그리기 모임
('LEADER', 'APPROVED', 't1@aaa.aaa', 2, NOW(), NOW()), -- 제주도 여행 동행
('LEADER', 'APPROVED', 't1@aaa.aaa', 3, NOW(), NOW()), -- 맛집 탐방 모임
('LEADER', 'APPROVED', 't1@aaa.aaa', 4, NOW(), NOW()), -- 독서 토론 모임
('LEADER', 'APPROVED', 't1@aaa.aaa', 5, NOW(), NOW()), -- 수채화 클래스

-- t2@aaa.aaa 리더 모임들 (group_id: 6,7,8,9,10)
('LEADER', 'APPROVED', 't2@aaa.aaa', 6, NOW(), NOW()), -- 보드게임 카페 모임
('LEADER', 'APPROVED', 't2@aaa.aaa', 7, NOW(), NOW()), -- 뮤지컬 관람 모임
('LEADER', 'APPROVED', 't2@aaa.aaa', 8, NOW(), NOW()), -- 주말 등산 모임
('LEADER', 'APPROVED', 't2@aaa.aaa', 9, NOW(), NOW()), -- 클래식 콘서트 모임
('LEADER', 'APPROVED', 't2@aaa.aaa', 10, NOW(), NOW()), -- 테니스 레슨 모임

-- t3@aaa.aaa 리더 모임들 (group_id: 11,12,13,14,15)
('LEADER', 'APPROVED', 't3@aaa.aaa', 11, NOW(), NOW()), -- 영화 감상 모임
('LEADER', 'APPROVED', 't3@aaa.aaa', 12, NOW(), NOW()), -- 요리 클래스 모임
('LEADER', 'APPROVED', 't3@aaa.aaa', 13, NOW(), NOW()), -- 사진 촬영 모임
('LEADER', 'APPROVED', 't3@aaa.aaa', 14, NOW(), NOW()), -- 디저트 카페 투어
('LEADER', 'APPROVED', 't3@aaa.aaa', 15, NOW(), NOW()), -- 독립영화 상영회

-- t4@aaa.aaa 리더 모임들 (group_id: 16,17,18,19,20)
('LEADER', 'APPROVED', 't4@aaa.aaa', 16, NOW(), NOW()), -- 캘리그라피 클래스
('LEADER', 'APPROVED', 't4@aaa.aaa', 17, NOW(), NOW()), -- 부산 여행 동행
('LEADER', 'APPROVED', 't4@aaa.aaa', 18, NOW(), NOW()), -- 한식 요리 클래스
('LEADER', 'APPROVED', 't4@aaa.aaa', 19, NOW(), NOW()), -- 영어 회화 모임
('LEADER', 'APPROVED', 't4@aaa.aaa', 20, NOW(), NOW()), -- 심야 영화 모임

-- t5@aaa.aaa 리더 모임들 (group_id: 21,22,23,24,25)
('LEADER', 'APPROVED', 't5@aaa.aaa', 21, NOW(), NOW()), -- 도자기 만들기
('LEADER', 'APPROVED', 't5@aaa.aaa', 22, NOW(), NOW()), -- 강릉 여행 모임
('LEADER', 'APPROVED', 't5@aaa.aaa', 23, NOW(), NOW()), -- 브런치 카페 모임
('LEADER', 'APPROVED', 't5@aaa.aaa', 24, NOW(), NOW()), -- 배드민턴 동호회
('LEADER', 'APPROVED', 't5@aaa.aaa', 25, NOW(), NOW()), -- 게임 대회 모임

-- t6@aaa.aaa 리더 모임들 (group_id: 26,27,28,29,30)
('LEADER', 'APPROVED', 't6@aaa.aaa', 26, NOW(), NOW()), -- 재즈 바 투어
('LEADER', 'APPROVED', 't6@aaa.aaa', 27, NOW(), NOW()), -- 파이썬 스터디
('LEADER', 'APPROVED', 't6@aaa.aaa', 28, NOW(), NOW()), -- 애니메이션 상영회
('LEADER', 'APPROVED', 't6@aaa.aaa', 29, NOW(), NOW()), -- 일본 여행 준비
('LEADER', 'APPROVED', 't6@aaa.aaa', 30, NOW(), NOW()); -- 마라톤 준비 모임
-- 사진 촬영 모임

-- Calendar 테이블 데이터 삽입
INSERT INTO calendar (email, selected_date, type, content_id, group_id, activated, created_at, modified_at)
VALUES
-- t1
('t1@aaa.aaa', '2025-07-15 10:00:00', 'CONTENT', 3, NULL, true, NOW(), NOW()),
('t1@aaa.aaa', NULL, 'GROUP', NULL, 1, true, NOW(), NOW()),

-- t2
('t2@aaa.aaa', '2025-07-02 14:00:00', 'CONTENT', 2, NULL, true, NOW(), NOW()),
('t2@aaa.aaa', NULL, 'GROUP', NULL, 2, true, NOW(), NOW()),

-- t3
('t3@aaa.aaa', '2025-07-13 09:00:00', 'CONTENT', 5, NULL, true, NOW(), NOW()),
('t3@aaa.aaa', NULL, 'GROUP', NULL, 3, true, NOW(), NOW()),

-- t4
('t4@aaa.aaa', '2025-07-05 11:00:00', 'CONTENT', 4, NULL, true, NOW(), NOW()),
('t4@aaa.aaa', NULL, 'GROUP', NULL, 4, true, NOW(), NOW()),

-- t5
('t5@aaa.aaa', '2025-07-10 13:00:00', 'CONTENT', 1, NULL, true, NOW(), NOW()),
('t5@aaa.aaa', NULL, 'GROUP', NULL, 5, true, NOW(), NOW()),

-- t6
('t6@aaa.aaa', '2025-07-10 17:00:00', 'CONTENT', 6, NULL, true, NOW(), NOW()),
('t6@aaa.aaa', NULL, 'GROUP', NULL, 6, true, NOW(), NOW()),

-- t7
('t7@aaa.aaa', '2025-07-04 15:00:00', 'CONTENT', 7, NULL, true, NOW(), NOW()),
('t7@aaa.aaa', NULL, 'GROUP', NULL, 7, true, NOW(), NOW()),

-- t8
('t8@aaa.aaa', '2025-07-16 10:30:00', 'CONTENT', 8, NULL, true, NOW(), NOW()),
('t8@aaa.aaa', NULL, 'GROUP', NULL, 8, true, NOW(), NOW()),

-- t9
('t9@aaa.aaa', '2025-07-21 08:00:00', 'CONTENT', 9, NULL, true, NOW(), NOW()),
('t9@aaa.aaa', NULL, 'GROUP', NULL, 9, true, NOW(), NOW()),

-- t10
('t10@aaa.aaa', '2025-07-18 19:00:00', 'CONTENT', 10, NULL, true, NOW(), NOW()),
('t10@aaa.aaa', NULL, 'GROUP', NULL, 10, true, NOW(), NOW());

ALTER TABLE report ALTER COLUMN resolved SET DEFAULT false;
-- Report 테이블 데이터 삽입 (모임 게시글 신고)
INSERT INTO report (reason, type, target_id, reporting_user_id, reported_user_id, activated, created_at, modified_at)
VALUES
    ('비매너적인 모임 운영이에요', 'POST', 1, 't2@aaa.aaa', 't1@aaa.aaa', true, NOW(), NOW()),
    ('허위 정보를 포함하고 있어요', 'POST', 6, 't3@aaa.aaa', 't2@aaa.aaa', true, NOW(), NOW()),
    ('불쾌한 표현이 있어요', 'POST', 10, 't5@aaa.aaa', 't3@aaa.aaa', true, NOW(), NOW());

ALTER TABLE contact ALTER COLUMN activated SET DEFAULT true;

-- Contact 테이블 데이터 삽입
INSERT INTO contact (title, content, category, status, answer, answered_at, user_id, created_at, modified_at)
VALUES
    ('앱 사용 중 오류가 발생해요', '로그인 중 자꾸 튕깁니다.', 'GENERAL', 'PENDING', NULL, NULL, 't1@aaa.aaa', NOW(), NOW()),
    ( '신고 기능 문의', '신고 버튼이 잘 안 눌려요.', 'REPORT', 'COMPLETE', '버그 수정 예정입니다.', NOW(), 't2@aaa.aaa', NOW(), NOW()),
    ( '탈퇴 관련 질문', '탈퇴하면 정보가 모두 삭제되나요?', 'GENERAL', 'PENDING', NULL, NULL, 't3@aaa.aaa', NOW(), NOW()),
    ( '특정 유저 신고합니다', '욕설을 자주 합니다.', 'REPORT', 'COMPLETE', '조치 완료했습니다.', NOW(), 't1@aaa.aaa', NOW(), NOW()),
    ( '기능 개선 제안', '다크모드 추가해주세요.', 'GENERAL', 'PENDING', NULL, NULL, 't2@aaa.aaa', NOW(), NOW()),
    ( '이미지 업로드 오류', '이미지 등록이 안 됩니다.', 'GENERAL', 'COMPLETE', '버그 패치 완료.', NOW(), 't3@aaa.aaa', NOW(), NOW()),
    ( '광고 관련 문의', '광고가 너무 많이 나와요.', 'GENERAL', 'PENDING', NULL, NULL, 't1@aaa.aaa', NOW(), NOW()),
    ( '유저 신고합니다', '계속 비방 메시지를 보내요.', 'REPORT', 'COMPLETE', '차단 처리 완료.', NOW(), 't2@aaa.aaa', NOW(), NOW()),
    ( '앱 종료 현상', '홈에서 자꾸 꺼져요.', 'GENERAL', 'PENDING', NULL, NULL, 't3@aaa.aaa', NOW(), NOW()),
    ( '허위 정보 신고', '모임 설명이 다릅니다.', 'REPORT', 'COMPLETE', '강제 수정 요청함.', NOW(), 't1@aaa.aaa', NOW(), NOW());

INSERT INTO contact_image (image_url, contact_id)
VALUES
    ( 'https://example.com/image1.png', 1),
    ( 'https://example.com/image2.png', 2),
    ( 'https://example.com/image3.png', 3),
    ( 'https://example.com/image4.png', 4),
    ( 'https://example.com/image5.png', 5),
    ( 'https://example.com/image6.png', 6),
    ( 'https://example.com/image7.png', 7),
    ( 'https://example.com/image8.png', 8),
    ( 'https://example.com/image9.png', 9),
    ( 'https://example.com/image10.png', 10);

INSERT INTO group_chat_room (group_id, status, name, created_at, modified_at)
VALUES
    (1, 'GROUP_CHAT', '1번 그룹 채팅방', NOW(), NOW()),
    (2, 'GROUP_CHAT', '2번 그룹 채팅방', NOW(), NOW()),
    (3, 'GROUP_CHAT', '3번 그룹 채팅방', NOW(), NOW()),
    (4, 'GROUP_CHAT', '4번 그룹 채팅방', NOW(), NOW()),
    (5, 'GROUP_CHAT', '5번 그룹 채팅방', NOW(), NOW()),
    (6, 'GROUP_CHAT', '6번 그룹 채팅방', NOW(), NOW()),
    (7, 'GROUP_CHAT', '7번 그룹 채팅방', NOW(), NOW()),
    (8, 'GROUP_CHAT', '8번 그룹 채팅방', NOW(), NOW()),
    (9, 'GROUP_CHAT', '9번 그룹 채팅방', NOW(), NOW()),
    (10, 'GROUP_CHAT', '10번 그룹 채팅방', NOW(), NOW()),
    (11, 'GROUP_CHAT', '11번 그룹 채팅방', NOW(), NOW()),
    (12, 'GROUP_CHAT', '12번 그룹 채팅방', NOW(), NOW()),
    (13, 'GROUP_CHAT', '13번 그룹 채팅방', NOW(), NOW()),
    (14, 'GROUP_CHAT', '14번 그룹 채팅방', NOW(), NOW()),
    (15, 'GROUP_CHAT', '15번 그룹 채팅방', NOW(), NOW()),
    (16, 'GROUP_CHAT', '16번 그룹 채팅방', NOW(), NOW()),
    (17, 'GROUP_CHAT', '17번 그룹 채팅방', NOW(), NOW()),
    (18, 'GROUP_CHAT', '18번 그룹 채팅방', NOW(), NOW()),
    (19, 'GROUP_CHAT', '19번 그룹 채팅방', NOW(), NOW()),
    (20, 'GROUP_CHAT', '20번 그룹 채팅방', NOW(), NOW()),
    (21, 'GROUP_CHAT', '21번 그룹 채팅방', NOW(), NOW()),
    (22, 'GROUP_CHAT', '22번 그룹 채팅방', NOW(), NOW()),
    (23, 'GROUP_CHAT', '23번 그룹 채팅방', NOW(), NOW()),
    (24, 'GROUP_CHAT', '24번 그룹 채팅방', NOW(), NOW()),
    (25, 'GROUP_CHAT', '25번 그룹 채팅방', NOW(), NOW()),
    (26, 'GROUP_CHAT', '26번 그룹 채팅방', NOW(), NOW()),
    (27, 'GROUP_CHAT', '27번 그룹 채팅방', NOW(), NOW()),
    (28, 'GROUP_CHAT', '28번 그룹 채팅방', NOW(), NOW()),
    (29, 'GROUP_CHAT', '29번 그룹 채팅방', NOW(), NOW()),
    (30, 'GROUP_CHAT', '30번 그룹 채팅방', NOW(), NOW());

-- 추가 참가자 데이터 (그룹 1-10은 기존 데이터 유지, 11-30 추가)

-- 그룹 1: 그림 그리기 모임 (now_people: 3, 리더 포함이므로 +2명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't4@aaa.aaa', 1, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 1, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 1, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't8@aaa.aaa', 1, NOW(), NOW());

-- 그룹 2: 제주도 여행 동행 (now_people: 2, 리더 포함이므로 +1명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't5@aaa.aaa', 2, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 2, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 2, NOW(), NOW());

-- 그룹 3: 맛집 탐방 모임 (now_people: 4, 리더 포함이므로 +3명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't6@aaa.aaa', 3, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 3, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 3, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't4@aaa.aaa', 3, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 3, NOW(), NOW());

-- 그룹 4: 독서 토론 모임 (now_people: 4, 리더 포함이므로 +3명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't9@aaa.aaa', 4, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't10@aaa.aaa', 4, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't5@aaa.aaa', 4, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't6@aaa.aaa', 4, NOW(), NOW());

-- 그룹 5: 수채화 클래스 (now_people: 3, 리더 포함이므로 +2명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't3@aaa.aaa', 5, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 5, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 5, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't8@aaa.aaa', 5, NOW(), NOW());

-- 그룹 6: 보드게임 카페 모임 (now_people: 5, 리더 포함이므로 +4명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't4@aaa.aaa', 6, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 6, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 6, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't9@aaa.aaa', 6, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 6, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't1@aaa.aaa', 6, NOW(), NOW());

-- 그룹 7: 뮤지컬 관람 모임 (now_people: 3, 리더 포함이므로 +2명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 7, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 7, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't5@aaa.aaa', 7, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't6@aaa.aaa', 7, NOW(), NOW());

-- 그룹 8: 주말 등산 모임 (now_people: 7, 리더 포함이므로 +6명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 8, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 8, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 8, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 8, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 8, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't10@aaa.aaa', 8, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't5@aaa.aaa', 8, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 8, NOW(), NOW());

-- 그룹 9: 클래식 콘서트 모임 (now_people: 2, 리더 포함이므로 +1명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't5@aaa.aaa', 9, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 9, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 9, NOW(), NOW());

-- 그룹 10: 테니스 레슨 모임 (now_people: 6, 리더 포함이므로 +5명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 10, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 10, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 10, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 10, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 10, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 10, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 10, NOW(), NOW());

-- 그룹 11: 영화 감상 모임 (now_people: 3, 리더 포함이므로 +2명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 11, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't2@aaa.aaa', 11, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't4@aaa.aaa', 11, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 11, NOW(), NOW());

-- 그룹 12: 요리 클래스 모임 (now_people: 6, 리더 포함이므로 +5명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 12, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't2@aaa.aaa', 12, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 12, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 12, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 12, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't8@aaa.aaa', 12, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 12, NOW(), NOW());

-- 그룹 13: 사진 촬영 모임 (now_people: 2, 리더 포함이므로 +1명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't2@aaa.aaa', 13, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't5@aaa.aaa', 13, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 13, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 13, NOW(), NOW());

-- 그룹 14: 디저트 카페 투어 (now_people: 3, 리더 포함이므로 +2명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 14, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't5@aaa.aaa', 14, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't8@aaa.aaa', 14, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 14, NOW(), NOW());

-- 그룹 15: 독립영화 상영회 (now_people: 8, 리더 포함이므로 +7명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 15, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't2@aaa.aaa', 15, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 15, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't5@aaa.aaa', 15, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 15, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 15, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 15, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 15, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 15, NOW(), NOW());

-- 그룹 16: 캘리그라피 클래스 (now_people: 5, 리더 포함이므로 +4명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 16, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 16, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't5@aaa.aaa', 16, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 16, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 16, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 16, NOW(), NOW());

-- 그룹 17: 부산 여행 동행 (now_people: 4, 리더 포함이므로 +3명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't2@aaa.aaa', 17, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 17, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 17, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't3@aaa.aaa', 17, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't5@aaa.aaa', 17, NOW(), NOW());

-- 그룹 18: 한식 요리 클래스 (now_people: 7, 리더 포함이므로 +6명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 18, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't2@aaa.aaa', 18, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 18, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't5@aaa.aaa', 18, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 18, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't9@aaa.aaa', 18, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't6@aaa.aaa', 18, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 18, NOW(), NOW());

-- 그룹 19: 영어 회화 모임 (now_people: 6, 리더 포함이므로 +5명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't2@aaa.aaa', 19, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 19, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 19, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 19, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't10@aaa.aaa', 19, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't5@aaa.aaa', 19, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 19, NOW(), NOW());

-- 그룹 20: 심야 영화 모임 (now_people: 2, 리더 포함이므로 +1명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't6@aaa.aaa', 20, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't1@aaa.aaa', 20, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 20, NOW(), NOW());

-- 그룹 21: 도자기 만들기 (now_people: 3, 리더 포함이므로 +2명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't2@aaa.aaa', 21, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 21, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 21, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't8@aaa.aaa', 21, NOW(), NOW());

-- 그룹 22: 강릉 여행 모임 (now_people: 5, 리더 포함이므로 +4명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 22, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 22, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 22, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't9@aaa.aaa', 22, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't4@aaa.aaa', 22, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't8@aaa.aaa', 22, NOW(), NOW());

-- 그룹 23: 브런치 카페 모임 (now_people: 2, 리더 포함이므로 +1명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't7@aaa.aaa', 23, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't2@aaa.aaa', 23, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 23, NOW(), NOW());

-- 그룹 24: 배드민턴 동호회 (now_people: 9, 리더 포함이므로 +8명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 24, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't2@aaa.aaa', 24, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 24, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 24, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 24, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 24, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 24, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't9@aaa.aaa', 24, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 24, NOW(), NOW());

-- 그룹 25: 게임 대회 모임 (now_people: 8, 리더 포함이므로 +7명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 25, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't2@aaa.aaa', 25, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 25, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 25, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't6@aaa.aaa', 25, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 25, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 25, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't9@aaa.aaa', 25, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 25, NOW(), NOW());

-- 그룹 26: 재즈 바 투어 (now_people: 4, 리더 포함이므로 +3명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't2@aaa.aaa', 26, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 26, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 26, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't3@aaa.aaa', 26, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't5@aaa.aaa', 26, NOW(), NOW());

-- 그룹 27: 파이썬 스터디 (now_people: 7, 리더 포함이므로 +6명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 27, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 27, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 27, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't5@aaa.aaa', 27, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 27, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't9@aaa.aaa', 27, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't2@aaa.aaa', 27, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't10@aaa.aaa', 27, NOW(), NOW());

-- 그룹 28: 애니메이션 상영회 (now_people: 12, 리더 포함이므로 +11명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 28, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't2@aaa.aaa', 28, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 28, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 28, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't5@aaa.aaa', 28, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 28, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 28, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't9@aaa.aaa', 28, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't10@aaa.aaa', 28, NOW(), NOW());


-- 그룹 29: 일본 여행 준비 (now_people: 3, 리더 포함이므로 +2명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't5@aaa.aaa', 29, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 29, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't2@aaa.aaa', 29, NOW(), NOW()),
       ('MEMBER', 'PENDING', 't7@aaa.aaa', 29, NOW(), NOW());

-- 그룹 30: 마라톤 준비 모임 (now_people: 11, 리더 포함이므로 +10명 APPROVED)
INSERT INTO participant (role, status, user_id, group_id, created_at, modified_at)
VALUES ('MEMBER', 'APPROVED', 't1@aaa.aaa', 30, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't2@aaa.aaa', 30, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't3@aaa.aaa', 30, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't4@aaa.aaa', 30, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't5@aaa.aaa', 30, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't7@aaa.aaa', 30, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't8@aaa.aaa', 30, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't9@aaa.aaa', 30, NOW(), NOW()),
       ('MEMBER', 'APPROVED', 't10@aaa.aaa', 30, NOW(), NOW());
