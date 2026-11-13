INSERT INTO users(first_name,last_name)
VALUES
    ('Ana','Popescu'),
    ('Maria','Ionescu'),
    ('Andrei','Pop'),
    ('Ioana','Muresan'),
    ('Vlad','Dumitrescu'),
    ('Elena','Marin'),
    ('Radu','Stan'),
    ('Sofia','Georgescu'),
    ('Mihai','Dobre'),
    ('Diana','Rusu');


WITH pairs(first1, last1, first2, last2, friends_from) AS (
    VALUES
        ('Ana','Popescu','Maria','Ionescu','2025-01-10'::timestamp),
        ('Ana','Popescu','Andrei','Pop','2025-02-15'::timestamp),
        ('Ana','Popescu','Ioana','Muresan','2025-03-20'::timestamp),
        ('Maria','Ionescu','Elena','Marin','2025-04-05'::timestamp),
        ('Maria','Ionescu','Vlad','Dumitrescu','2025-05-12'::timestamp),
        ('Andrei','Pop','Radu','Stan','2025-06-18'::timestamp),
        ('Ioana','Muresan','Sofia','Georgescu','2025-07-22'::timestamp),
        ('Vlad','Dumitrescu','Mihai','Dobre','2025-08-10'::timestamp),
        ('Elena','Marin','Diana','Rusu','2025-09-14'::timestamp),
        ('Radu','Stan','Sofia','Georgescu','2025-10-01'::timestamp),
        ('Mihai','Dobre','Diana','Rusu','2025-11-05'::timestamp),
        ('Ana','Popescu','Diana','Rusu','2025-12-12'::timestamp)
)
INSERT INTO friendships (id1, id2, friends_from)
SELECT
    LEAST(u1.id, u2.id),
    GREATEST(u1.id, u2.id),
    p.friends_from
FROM pairs p
         JOIN users u1
              ON u1.first_name = p.first1 AND u1.last_name = p.last1
         JOIN users u2
              ON u2.first_name = p.first2 AND u2.last_name = p.last2
WHERE u1.id <> u2.id
    ON CONFLICT (id1, id2) DO NOTHING;
