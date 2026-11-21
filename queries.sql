
SELECT id, isbn, title, author, year, rating, is_read
FROM books
ORDER BY year DESC, title ASC;


SELECT id, isbn, title, author, year, rating, is_read
FROM books
WHERE author = 'Andy Weir'
ORDER BY year ASC;


SELECT id, isbn, title, author, year, rating, is_read
FROM books
WHERE is_read = 0
ORDER BY title ASC;


SELECT id, isbn, title, author, year, rating, is_read
FROM books
WHERE title LIKE '%the%';


SELECT author, COUNT(*) AS book_count
FROM books
GROUP BY author
ORDER BY book_count DESC, author ASC;


SELECT AVG(rating) AS avg_rating
FROM books
WHERE rating > 0;


UPDATE books
SET is_read = 1, rating = 4
WHERE title = 'Sapiens';


SELECT id, isbn, title, author, year, rating, is_read
FROM books
WHERE rating = 0 AND is_read = 0 AND year < 2000;


DELETE FROM books
WHERE rating = 0 AND is_read = 0 AND year < 2000;