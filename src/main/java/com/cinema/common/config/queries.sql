-- =========================================
-- AUTH & USER QUERIES
-- =========================================

-- 1️⃣ Login (username OR phone)
SELECT *
FROM users
WHERE (username = ? OR phone = ?)
  AND password = ?;

-- 2️⃣ Check duplicate username / phone
SELECT 
    EXISTS (SELECT 1 FROM users WHERE username = ?) AS username_exists,
    EXISTS (SELECT 1 FROM users WHERE phone = ?) AS phone_exists;

-- 3️⃣ Get user profile
SELECT id, username, email, phone, balance, role, created_at
FROM users
WHERE id = ?;



-- =========================================
-- MOVIES & SHOWTIMES
-- =========================================

-- 4️⃣ All movies with revenue (VIEW)
SELECT *
FROM v_movie_revenue
ORDER BY income DESC;

-- 5️⃣ Top 5 movies
SELECT *
FROM v_movie_revenue
ORDER BY income DESC
LIMIT 5;

-- 6️⃣ Active showtimes for a movie
SELECT 
    s.id AS showtime_id,
    s.room,
    s.show_date,
    s.show_time
FROM showtimes s
WHERE s.movie_id = ?
  AND s.isActive = TRUE
ORDER BY s.show_date, s.show_time;

-- 7️⃣ Today showtimes
SELECT 
    m.title,
    s.room,
    s.show_time
FROM showtimes s
JOIN movies m ON m.id = s.movie_id
WHERE s.show_date = CURDATE()
  AND s.isActive = TRUE;



-- =========================================
-- SEAT AVAILABILITY (NO VIEW – PARAM REQUIRED)
-- =========================================

-- 8️⃣ Available seats for a showtime
SELECT 
    se.id AS seat_id,
    se.seat_row,
    se.seat_number,
    se.price
FROM seats se
WHERE se.room = (
    SELECT room FROM showtimes WHERE id = ?
)
AND se.id NOT IN (
    SELECT seat_id
    FROM booking_seats
    WHERE showtime_id = ?
)
ORDER BY se.seat_row, se.seat_number;

-- 9️⃣ Booked seats (VIEW)
SELECT *
FROM v_booked_seats
WHERE showtime_id = ?;



-- =========================================
-- BOOKINGS (USING VIEWS)
-- =========================================

-- 🔟 User booking history
SELECT *
FROM v_user_booking_history
WHERE user_id = ?
ORDER BY booked_at DESC;

-- 1️⃣1️⃣ Admin booking list
SELECT *
FROM v_booking_details
ORDER BY booked_at DESC;

-- 1️⃣2️⃣ Ticket details (PRINT)
SELECT *
FROM v_ticket_details
WHERE booking_id = ?;

-- 1️⃣3️⃣ Ticket details (User)
SELECT *
FROM v_ticket_details
WHERE username = ?
ORDER BY show_date DESC;

-- 1️⃣4️⃣ Cancel booking (refund + seat free)
DELETE FROM bookings
WHERE id = ?;



-- =========================================
-- REVENUE & REPORTS (VIEWS ONLY)
-- =========================================

-- 1️⃣5️⃣ Daily revenue (today)
SELECT *
FROM v_daily_revenue
WHERE booking_date = CURDATE();

-- 1️⃣6️⃣ Revenue by date range
SELECT *
FROM v_daily_revenue
WHERE booking_date BETWEEN ? AND ?
ORDER BY booking_date;

-- 1️⃣7️⃣ Monthly revenue
SELECT 
    YEAR(booking_date) AS year,
    MONTH(booking_date) AS month,
    SUM(daily_income) AS total_income
FROM v_daily_revenue
GROUP BY YEAR(booking_date), MONTH(booking_date)
ORDER BY year DESC, month DESC;



-- =========================================
-- DEPOSITS (ADMIN)
-- =========================================

-- 1️⃣8️⃣ Pending deposits
SELECT 
    d.id,
    u.username,
    d.amount,
    d.payment_method,
    d.sender_name,
    d.transaction_last_6,
    d.created_at
FROM deposits d
JOIN users u ON u.id = d.user_id
WHERE d.status = 'pending'
ORDER BY d.created_at ASC;

-- 1️⃣9️⃣ Approve deposit (balance auto-updated)
UPDATE users u
JOIN deposits d ON d.user_id = u.id
SET 
    u.balance = u.balance + d.amount,
    d.status = 'approved'
WHERE d.id = ?;

-- 2️⃣0️⃣ Reject deposit
UPDATE deposits
SET status = 'rejected',
    admin_note = ?
WHERE id = ?;



-- =========================================
-- MAINTENANCE & DEBUG
-- =========================================

-- 2️⃣1️⃣ Double-booking check (should be ZERO)
SELECT showtime_id, seat_id, COUNT(*) AS duplicates
FROM booking_seats
GROUP BY showtime_id, seat_id
HAVING COUNT(*) > 1;

-- 2️⃣2️⃣ Negative balance check (should be ZERO)
SELECT *
FROM users
WHERE balance < 0;

-- 2️⃣3️⃣ Movies with no bookings
SELECT m.id, m.title
FROM movies m
LEFT JOIN showtimes s ON s.movie_id = m.id
LEFT JOIN bookings b ON b.showtime_id = s.id
WHERE b.id IS NULL;