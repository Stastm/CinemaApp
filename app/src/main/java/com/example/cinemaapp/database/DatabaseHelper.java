package com.example.cinemaapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cinemaapp.models.Movie;
import com.example.cinemaapp.models.Review;
import com.example.cinemaapp.models.Seat;
import com.example.cinemaapp.models.Session;
import com.example.cinemaapp.models.Ticket;
import com.example.cinemaapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cinema.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_USERS = "Users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_USERNAME = "username";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ROLE = "role";

    public static final String TABLE_MOVIES = "Movies";
    public static final String COL_MOVIE_ID = "id";
    public static final String COL_MOVIE_TITLE = "title";
    public static final String COL_MOVIE_DESCRIPTION = "description";
    public static final String COL_MOVIE_DURATION = "duration";
    public static final String COL_MOVIE_POSTER = "poster";

    public static final String TABLE_SESSIONS = "Sessions";
    public static final String COL_SESSION_ID = "id";
    public static final String COL_SESSION_MOVIE_ID = "movie_id";
    public static final String COL_SESSION_DATE = "date";
    public static final String COL_SESSION_TIME = "time";
    public static final String COL_SESSION_PRICE = "price";

    public static final String TABLE_SEATS = "Seats";
    public static final String COL_SEAT_ID = "id";
    public static final String COL_SEAT_SESSION_ID = "session_id";
    public static final String COL_SEAT_ROW = "row_number";
    public static final String COL_SEAT_NUMBER = "seat_number";
    public static final String COL_SEAT_IS_BOOKED = "isBooked";

    public static final String TABLE_TICKETS = "Tickets";
    public static final String COL_TICKET_ID = "id";
    public static final String COL_TICKET_USER_ID = "user_id";
    public static final String COL_TICKET_SESSION_ID = "session_id";
    public static final String COL_TICKET_SEAT_ID = "seat_id";
    public static final String COL_TICKET_TOTAL_PRICE = "total_price";

    public static final String TABLE_REVIEWS = "Reviews";
    public static final String COL_REVIEW_ID = "id";
    public static final String COL_REVIEW_USER_ID = "user_id";
    public static final String COL_REVIEW_MOVIE_ID = "movie_id";
    public static final String COL_REVIEW_RATING = "rating";
    public static final String COL_REVIEW_COMMENT = "comment";
    public static final String COL_REVIEW_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL, role TEXT NOT NULL DEFAULT 'user');");
        db.execSQL("CREATE TABLE " + TABLE_MOVIES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, description TEXT, duration INTEGER, poster TEXT);");
        db.execSQL("CREATE TABLE " + TABLE_SESSIONS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER, date TEXT, time TEXT, price REAL);");
        db.execSQL("CREATE TABLE " + TABLE_SEATS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, row_number INTEGER, seat_number INTEGER, isBooked INTEGER DEFAULT 0);");
        db.execSQL("CREATE TABLE " + TABLE_TICKETS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, session_id INTEGER, seat_id INTEGER, total_price REAL);");
        db.execSQL("CREATE TABLE " + TABLE_REVIEWS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, movie_id INTEGER, rating REAL, comment TEXT, date TEXT);");
        seedDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void seedDatabase(SQLiteDatabase db) {
        ContentValues admin = new ContentValues();
        admin.put(COL_USER_USERNAME, "admin");
        admin.put(COL_USER_PASSWORD, "admin123");
        admin.put(COL_USER_ROLE, "admin");
        db.insert(TABLE_USERS, null, admin);

        ContentValues userValues = new ContentValues();
        userValues.put(COL_USER_USERNAME, "user");
        userValues.put(COL_USER_PASSWORD, "user123");
        userValues.put(COL_USER_ROLE, "user");
        db.insert(TABLE_USERS, null, userValues);

        String[][] movies = {
                {
                        "Интерстеллар",
                        "Когда засуха и вымирание урожая угрожают выживанию человечества на Земле, бывший пилот NASA Купер вместе с командой учёных отправляется сквозь червоточину в другую галактику. Там они ищут новый дом для людей, но цена этого путешествия — годы разлуки с семьёй и столкновение с законами пространства и времени.",
                        "169", "interstellar"
                },
                {
                        "Тёмный рыцарь",
                        "Бэтмен, лейтенант Гордон и прокурор Харви Дент начинают масштабную борьбу с преступностью в Готэме. Но появление безумного гения хаоса — Джокера — ставит всё под угрозу. Джокер не просто преступник, он философ разрушения.",
                        "152", "dark_knight"
                },
                {
                        "Начало",
                        "Дом Кобб — вор особого рода: он проникает в подсознание людей во время сна и похищает ценную информацию. Ему предлагают сделку: внедрить идею в разум влиятельного бизнесмена. Но граница между сном и реальностью начинает стираться.",
                        "148", "inception"
                },
                {
                        "Матрица",
                        "Хакер Нео живёт обычной жизнью, пока незнакомец по имени Морфеус не открывает ему шокирующую правду: реальность — это симуляция, созданная машинами для порабощения человечества. Культовый киберпанк-триллер о свободе и природе реальности.",
                        "136", "matrix"
                },
                {
                        "Дюна",
                        "Молодой Пол Атрейдес прибывает на опасную пустынную планету Арракис — единственный источник самого ценного вещества во вселенной. Предательство, война племён и древнее пророчество втягивают его в эпическую борьбу за власть над галактикой.",
                        "155", "dune"
                },
                {
                        "Аватар",
                        "Парализованный морской пехотинец Джейк Салли отправляется на далёкую планету Пандора, где управляет телом аватара. Постепенно сживаясь с культурой народа на'ви, он оказывается перед тяжелейшим выбором: выполнить приказ или защитить новый дом.",
                        "162", "avatar"
                },
                {
                        "Человек-паук: Нет пути домой",
                        "Когда личность Питера Паркера раскрыта на весь мир, его жизнь рушится. Заклинание выходит из-под контроля и открывает двери в мультивселенную, откуда врываются самые опасные злодеи.",
                        "148", "spider_man"
                },
                {
                        "Доктор Стрэндж",
                        "Блестящий нейрохирург Стивен Стрэндж попадает в страшную аварию, которая разрушает его карьеру. В поисках исцеления он отправляется на Восток и открывает в себе дар владения магическими искусствами.",
                        "115", "doctor_strange"
                },
                {
                        "Титаник",
                        "На борту роскошного лайнера встречаются двое: бедный художник Джек и богатая аристократка Роуз. Их любовь вспыхивает вопреки разнице в положении, но судьба уготовила им страшное испытание.",
                        "194", "titanic"
                },
                {
                        "Бойцовский клуб",
                        "Офисный работник знакомится с харизматичным торговцем мылом Тайлером Дёрденом. Вместе они основывают подпольный бойцовский клуб, который быстро перерастает в нечто гораздо более опасное.",
                        "139", "fight_club"
                },
                {
                        "Властелин колец: Братство кольца",
                        "Молодой хоббит Фродо Бэггинс получает в наследство Кольцо Всевластья — артефакт невероятной силы, способный уничтожить весь Средиземье. Вместе с отважными товарищами он отправляется в опасное путешествие, чтобы уничтожить кольцо в огне Роковой горы.",
                        "178", "lotr"
                },
                {
                        "Гладиатор",
                        "Прославленный римский генерал Максимус предан и лишён всего. Став гладиатором, он жаждет мести против императора, убившего его семью. Эпическая история о чести, мести и свободе в Древнем Риме.",
                        "155", "gladiator"
                },
                {
                        "Форрест Гамп",
                        "История простого человека с добрым сердцем, который случайно оказывается в эпицентре ключевых событий американской истории. Форрест Гамп доказывает, что судьба — это не случайность, а то, что ты делаешь с тем, что тебе дано.",
                        "142", "forrest_gump"
                },
                {
                        "Криминальное чтиво",
                        "Несколько криминальных историй переплетаются в жизни гангстеров, боксёра и парочки грабителей. Культовый фильм Квентина Тарантино с нелинейным повествованием, яркими персонажами и незабываемыми диалогами.",
                        "154", "pulp_fiction"
                },
                {
                        "Зелёная книга",
                        "1962 год. Белый водитель Тони Валлелонга нанимается сопровождать чернокожего пианиста Дона Ширли в гастрольном туре по расистскому Югу США. Путешествие меняет обоих — история о дружбе, достоинстве и преодолении предрассудков.",
                        "130", "green_book"
                }
        };

        for (String[] movie : movies) {
            ContentValues mv = new ContentValues();
            mv.put(COL_MOVIE_TITLE, movie[0]);
            mv.put(COL_MOVIE_DESCRIPTION, movie[1]);
            mv.put(COL_MOVIE_DURATION, Integer.parseInt(movie[2]));
            mv.put(COL_MOVIE_POSTER, movie[3]);
            long movieId = db.insert(TABLE_MOVIES, null, mv);

            ContentValues s1 = new ContentValues();
            s1.put(COL_SESSION_MOVIE_ID, movieId);
            s1.put(COL_SESSION_DATE, "2025-06-15");
            s1.put(COL_SESSION_TIME, "18:00");
            s1.put(COL_SESSION_PRICE, 1200.0);
            long sid1 = db.insert(TABLE_SESSIONS, null, s1);

            ContentValues s2 = new ContentValues();
            s2.put(COL_SESSION_MOVIE_ID, movieId);
            s2.put(COL_SESSION_DATE, "2025-06-15");
            s2.put(COL_SESSION_TIME, "21:00");
            s2.put(COL_SESSION_PRICE, 1500.0);
            long sid2 = db.insert(TABLE_SESSIONS, null, s2);

            createSeatsForSession(db, sid1);
            createSeatsForSession(db, sid2);
        }
    }

    private void createSeatsForSession(SQLiteDatabase db, long sessionId) {
        for (int row = 1; row <= 5; row++) {
            for (int seat = 1; seat <= 8; seat++) {
                ContentValues v = new ContentValues();
                v.put(COL_SEAT_SESSION_ID, sessionId);
                v.put(COL_SEAT_ROW, row);
                v.put(COL_SEAT_NUMBER, seat);
                v.put(COL_SEAT_IS_BOOKED, 0);
                db.insert(TABLE_SEATS, null, v);
            }
        }
    }

    public void createSeatsForSession(long sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        createSeatsForSession(db, sessionId);
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_USERNAME + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE))
            );
            cursor.close();
        }
        return user;
    }

    public long registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_USERNAME, username);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_ROLE, "user");
        return db.insert(TABLE_USERS, null, values);
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MOVIES, null, null, null, null, null, COL_MOVIE_TITLE + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                movies.add(new Movie(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MOVIE_DURATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_POSTER))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return movies;
    }

    public Movie getMovieById(int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Movie movie = null;
        Cursor cursor = db.query(TABLE_MOVIES, null,
                COL_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            movie = new Movie(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_MOVIE_DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_POSTER))
            );
            cursor.close();
        }
        return movie;
    }

    public long addMovie(String title, String description, int duration, String poster) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MOVIE_TITLE, title);
        values.put(COL_MOVIE_DESCRIPTION, description);
        values.put(COL_MOVIE_DURATION, duration);
        values.put(COL_MOVIE_POSTER, poster);
        return db.insert(TABLE_MOVIES, null, values);
    }

    public boolean deleteMovie(int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Session> sessions = getSessionsByMovieId(movieId);
        for (Session session : sessions) {
            db.delete(TABLE_SEATS, COL_SEAT_SESSION_ID + "=?", new String[]{String.valueOf(session.getId())});
            db.delete(TABLE_TICKETS, COL_TICKET_SESSION_ID + "=?", new String[]{String.valueOf(session.getId())});
        }
        db.delete(TABLE_REVIEWS, COL_REVIEW_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)});
        db.delete(TABLE_SESSIONS, COL_SESSION_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)});
        int rows = db.delete(TABLE_MOVIES, COL_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)});
        return rows > 0;
    }

    public List<Session> getSessionsByMovieId(int movieId) {
        List<Session> sessions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SESSIONS, null,
                COL_SESSION_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)},
                null, null, COL_SESSION_DATE + ", " + COL_SESSION_TIME);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                sessions.add(new Session(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SESSION_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SESSION_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SESSION_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SESSION_TIME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SESSION_PRICE))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return sessions;
    }

    public long addSession(int movieId, String date, String time, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SESSION_MOVIE_ID, movieId);
        values.put(COL_SESSION_DATE, date);
        values.put(COL_SESSION_TIME, time);
        values.put(COL_SESSION_PRICE, price);
        long sessionId = db.insert(TABLE_SESSIONS, null, values);
        if (sessionId != -1) createSeatsForSession(db, sessionId);
        return sessionId;
    }

    public List<Seat> getSeatsBySessionId(int sessionId) {
        List<Seat> seats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SEATS, null,
                COL_SEAT_SESSION_ID + "=?", new String[]{String.valueOf(sessionId)},
                null, null, COL_SEAT_ROW + ", " + COL_SEAT_NUMBER);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                seats.add(new Seat(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_SESSION_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_ROW)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_NUMBER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_IS_BOOKED)) == 1
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return seats;
    }

    public long createTicket(int userId, int sessionId, int seatId, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues seatValues = new ContentValues();
        seatValues.put(COL_SEAT_IS_BOOKED, 1);
        db.update(TABLE_SEATS, seatValues, COL_SEAT_ID + "=?", new String[]{String.valueOf(seatId)});
        ContentValues ticketValues = new ContentValues();
        ticketValues.put(COL_TICKET_USER_ID, userId);
        ticketValues.put(COL_TICKET_SESSION_ID, sessionId);
        ticketValues.put(COL_TICKET_SEAT_ID, seatId);
        ticketValues.put(COL_TICKET_TOTAL_PRICE, totalPrice);
        return db.insert(TABLE_TICKETS, null, ticketValues);
    }

    public int getTotalTicketsSold() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TICKETS, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) { count = cursor.getInt(0); cursor.close(); }
        return count;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_TICKET_TOTAL_PRICE + ") FROM " + TABLE_TICKETS, null);
        double total = 0;
        if (cursor != null && cursor.moveToFirst()) { total = cursor.getDouble(0); cursor.close(); }
        return total;
    }

    public List<Ticket> getTicketsByUserId(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TICKETS, null,
                COL_TICKET_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                tickets.add(new Ticket(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TICKET_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TICKET_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TICKET_SESSION_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TICKET_SEAT_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TICKET_TOTAL_PRICE))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return tickets;
    }

    public Session getSessionById(int sessionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Session session = null;
        Cursor cursor = db.query(TABLE_SESSIONS, null,
                COL_SESSION_ID + "=?", new String[]{String.valueOf(sessionId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            session = new Session(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SESSION_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SESSION_MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SESSION_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SESSION_TIME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SESSION_PRICE))
            );
            cursor.close();
        }
        return session;
    }

    public Seat getSeatById(int seatId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Seat seat = null;
        Cursor cursor = db.query(TABLE_SEATS, null,
                COL_SEAT_ID + "=?", new String[]{String.valueOf(seatId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            seat = new Seat(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_SESSION_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_ROW)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_NUMBER)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_IS_BOOKED)) == 1
            );
            cursor.close();
        }
        return seat;
    }

    public long addReview(int userId, int movieId, float rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REVIEW_USER_ID, userId);
        values.put(COL_REVIEW_MOVIE_ID, movieId);
        values.put(COL_REVIEW_RATING, rating);
        values.put(COL_REVIEW_COMMENT, comment);
        values.put(COL_REVIEW_DATE, new java.text.SimpleDateFormat("dd.MM.yyyy",
                java.util.Locale.getDefault()).format(new java.util.Date()));
        return db.insert(TABLE_REVIEWS, null, values);
    }

    public List<Review> getReviewsByMovieId(int movieId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REVIEWS, null,
                COL_REVIEW_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)},
                null, null, COL_REVIEW_ID + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                reviews.add(new Review(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_REVIEW_MOVIE_ID)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COL_REVIEW_RATING)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_COMMENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW_DATE))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return reviews;
    }

    public float getAverageRating(int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT AVG(" + COL_REVIEW_RATING + ") FROM " + TABLE_REVIEWS +
                        " WHERE " + COL_REVIEW_MOVIE_ID + "=?",
                new String[]{String.valueOf(movieId)}
        );
        float avg = 0f;
        if (cursor != null && cursor.moveToFirst()) {
            avg = cursor.getFloat(0);
            cursor.close();
        }
        return avg;
    }

    public boolean hasUserReviewed(int userId, int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REVIEWS, null,
                COL_REVIEW_USER_ID + "=? AND " + COL_REVIEW_MOVIE_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(movieId)},
                null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    public List<Movie> getTrendingMovies(int limit) {
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT m.*, COALESCE(AVG(r.rating), 0) as avg_rating " +
                        "FROM " + TABLE_MOVIES + " m " +
                        "LEFT JOIN " + TABLE_REVIEWS + " r ON m.id = r.movie_id " +
                        "GROUP BY m.id " +
                        "ORDER BY avg_rating DESC " +
                        "LIMIT ?",
                new String[]{String.valueOf(limit)}
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                movies.add(new Movie(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MOVIE_DURATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MOVIE_POSTER))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return movies;
    }

    public String getUsernameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_USERNAME},
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);
        String username = "Пользователь";
        if (cursor != null && cursor.moveToFirst()) {
            username = cursor.getString(0);
            cursor.close();
        }
        return username;
    }
}