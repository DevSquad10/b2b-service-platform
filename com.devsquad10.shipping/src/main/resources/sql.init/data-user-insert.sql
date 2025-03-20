-- 유저 기본 180개(배송담당자) 데이터 삽입 (임의의 UUID 사용)
CREATE TABLE IF NOT EXISTS p_user AS
SELECT
    gen_random_uuid() AS id,
    '사용자' || LPAD(gs::TEXT, 3, '0') AS username,
    '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' AS password,
    'tester' || LPAD(gs::TEXT, 3, '0') || '@example.com' AS email,
    'slack' || LPAD(gs::TEXT, 3, '0') AS slack_id,
    'DVL_AGENT' AS role,
    now() - (random() * interval '365 days') AS created_at,
    '사용자' || LPAD(gs::TEXT, 3, '0') AS created_by,
    NULL AS updated_at,
    NULL AS updated_by,
    NULL AS deleted_at,
    NULL AS deleted_by
FROM
    generate_series(1, 180) AS gs;


-- 예시
INSERT INTO p_user (id, username, password, email, slack_id, role, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by)
VALUES
    ('55555555-5555-5555-5555-555555555001', '김나연', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester001@example.com', 'tester001', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555002', '강연우', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester002@example.com', 'tester002', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555003', '김수민', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester003@example.com', 'tester003', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555004', '최수영', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester004@example.com', 'tester004', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555005', '김윤아', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester005@example.com', 'tester005', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555006', '이지수', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester006@example.com', 'tester006', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555007', '안정우', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester007@example.com', 'tester007', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555008', '박재현', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester008@example.com', 'tester008', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555009', '서수빈', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester009@example.com', 'tester009', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555010', '이수아', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester010@example.com', 'tester010', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555011', '허민우', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester011@example.com', 'tester011', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555012', '김민석', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tester012@example.com', 'tester012', 'DVL_AGENT', now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null),
    ('55555555-5555-5555-5555-555555555017', '황미나', 'pw017', "tester017@example.com", "tester017", "DVL_AGENT", now(), '00000000-0000-0000-0000-000000000001', now(), '00000000-0000-0000-0000-000000000001', null, null)
    ON CONFLICT (id) DO NOTHING;