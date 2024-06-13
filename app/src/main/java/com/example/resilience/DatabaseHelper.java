package com.example.resilience;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "resilience.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1; // Retorna true si el usuario se añadió correctamente
    }
    public boolean addMeditationSession(int userId, String selectedTime, String duration, int completed, String date, String reflections) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("selected_time", selectedTime); // Guardar tiempo seleccionado
        values.put("duration", duration);
        values.put("completed", completed);
        values.put("date", date);
        values.put("reflections", reflections);

        long result = db.insert("sessions", null, values);
        return result != -1;
    }




    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "username=? AND password=?", new String[]{username, password}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean checkUserrepetido(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "username=? ", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, password TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_SESSIONS_TABLE = "CREATE TABLE sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "selected_time TEXT, " + // Nueva columna para tiempo seleccionado
                "duration INTEGER, " +
                "completed INTEGER, " +
                "date TEXT, " +
                "reflections TEXT)";
        db.execSQL(CREATE_SESSIONS_TABLE);


        // Crear la tabla de sesiones de respiración
        String CREATE_BREATHING_SESSIONS_TABLE = "CREATE TABLE breathing_sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "selected_time TEXT, " + // Tiempo seleccionado
                "completed_time TEXT, " + // Tiempo completado
                "exhale_count INTEGER, " + // Contador de exhalaciones
                "inhale_count INTEGER, " + // Contador de inhalaciones
                "completed INTEGER, " + // Si se completó la sesión
                "date TEXT)"; // Fecha de la sesión
        db.execSQL(CREATE_BREATHING_SESSIONS_TABLE);
    }


    // Método para obtener el ID del usuario
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "username=?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        return -1; // Usuario no encontrado
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ...
        if (oldVersion < 4) {
            // Añadir la nueva columna 'selected_time' a la tabla 'sessions'
            db.execSQL("ALTER TABLE sessions ADD COLUMN selected_time TEXT DEFAULT ''");
        }
    }




    public List<MeditationSession> getSessionsForDate(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<MeditationSession> sessions = new ArrayList<>();

        // Incluye la nueva columna 'selected_time' en tu consulta
        String[] columns = new String[] {"duration", "completed", "date", "reflections", "selected_time"};
        String selection = "user_id=? AND date=?";
        String[] selectionArgs = new String[] {String.valueOf(userId), date};

        Cursor cursor = db.query("sessions", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                int completed = cursor.getInt(cursor.getColumnIndex("completed"));
                String sessionDate = cursor.getString(cursor.getColumnIndex("date"));
                String reflections = cursor.getString(cursor.getColumnIndex("reflections"));
                String selectedTime = cursor.getString(cursor.getColumnIndex("selected_time")); // Recupera el tiempo seleccionado

                // Agrega el tiempo seleccionado al constructor de MeditationSession
                sessions.add(new MeditationSession(duration, completed, sessionDate, reflections, selectedTime));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return sessions;
    }



    public boolean addBreathingSession(int userId, String selectedTime, String completedTime, int exhaleCount, int inhaleCount, int completed, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("selected_time", selectedTime);
        values.put("completed_time", completedTime);
        values.put("exhale_count", exhaleCount);
        values.put("inhale_count", inhaleCount);
        values.put("completed", completed);
        values.put("date", date);

        long result = db.insert("breathing_sessions", null, values);
        return result != -1;
    }

    // Método para obtener sesiones de respiración para un usuario en una fecha específica
    public List<BreathingSession> getBreathingSessionsForDate(int userId, String date) {
        List<BreathingSession> sessions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = new String[]{"selected_time", "completed_time", "exhale_count", "inhale_count", "completed", "date"};
        String selection = "user_id=? AND date=?";
        String[] selectionArgs = new String[]{String.valueOf(userId), date};

        Cursor cursor = db.query("breathing_sessions", columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            String selectedTime = cursor.getString(cursor.getColumnIndex("selected_time"));
            String completedTime = cursor.getString(cursor.getColumnIndex("completed_time"));
            int exhaleCount = cursor.getInt(cursor.getColumnIndex("exhale_count"));
            int inhaleCount = cursor.getInt(cursor.getColumnIndex("inhale_count"));
            int completed = cursor.getInt(cursor.getColumnIndex("completed"));
            String sessionDate = cursor.getString(cursor.getColumnIndex("date")); // Aquí agregamos el campo faltante

            sessions.add(new BreathingSession(selectedTime, completedTime, exhaleCount, inhaleCount, completed, sessionDate));
        }
        cursor.close();
        return sessions;
    }

    public void deleteAllBreathingSessions() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Borrar todos los registros de la tabla 'breathing_sessions'
        db.delete("breathing_sessions", null, null);
        db.delete("sessions", null, null);
        db.delete("users", null, null);
    }



    public Map<String, Integer> getSessionDaysLast30Days(int userId, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT date FROM " + tableName +
                " WHERE user_id=? AND date >= date('now', '-30 days')";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        Map<String, Integer> dayCounts = new HashMap<>();

        while (cursor.moveToNext()) {
            String sessionDate = cursor.getString(cursor.getColumnIndex("date"));
            String dayOfWeek = getDayOfWeek(sessionDate);

            // Increment the count for the corresponding day of the week
            dayCounts.put(dayOfWeek, dayCounts.getOrDefault(dayOfWeek, 0) + 1);
        }
        cursor.close();

        return dayCounts;
    }
    // Método para obtener sesiones del mes anterior
    public Map<String, Integer> getSessionDaysLastMonth(int userId, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT date FROM " + tableName +
                " WHERE user_id=? AND date >= date('now', 'start of month', '-1 month') AND date < date('now', 'start of month')";
        return getSessionData(db, sql, userId);
    }

    // Método para obtener sesiones de los últimos 90 días
    public Map<String, Integer> getSessionDaysLast90Days(int userId, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT date FROM " + tableName +
                " WHERE user_id=? AND date >= date('now', '-90 days')";
        return getSessionData(db, sql, userId);
    }

    // Método para obtener sesiones del año actual
    public Map<String, Integer> getSessionDaysThisYear(int userId, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT strftime('%m', date) as month, COUNT(*) as session_count FROM " + tableName +
                " WHERE user_id=? AND date >= date('now', 'start of year') GROUP BY month";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        Map<String, Integer> monthCounts = new HashMap<>();
        while (cursor.moveToNext()) {
            String month = cursor.getString(cursor.getColumnIndex("month"));
            int count = cursor.getInt(cursor.getColumnIndex("session_count"));
            monthCounts.put(month, count);
        }
        cursor.close();
        return monthCounts;
    }


    // Método auxiliar para evitar la duplicación de código
    private Map<String, Integer> getSessionData(SQLiteDatabase db, String sql, int userId) {
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
        Map<String, Integer> dayCounts = new HashMap<>();

        while (cursor.moveToNext()) {
            String sessionDate = cursor.getString(cursor.getColumnIndex("date"));
            String dayOfWeek = getDayOfWeek(sessionDate);
            dayCounts.put(dayOfWeek, dayCounts.getOrDefault(dayOfWeek, 0) + 1);
        }
        cursor.close();

        return dayCounts;
    }


    private String getDayOfWeek(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = sdf.parse(date);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public int getTotalCompletedSessions(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM sessions WHERE user_id=? ";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        int totalSessions = 0;
        if (cursor.moveToFirst()) {
            totalSessions = cursor.getInt(0);
        }
        cursor.close();

        return totalSessions;
    }





    // Ejemplos de métodos específicos para meditación y respiración









}


