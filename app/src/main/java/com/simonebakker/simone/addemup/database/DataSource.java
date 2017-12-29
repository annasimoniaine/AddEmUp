package com.simonebakker.simone.addemup.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.simonebakker.simone.addemup.models.Game;

import java.util.ArrayList;
import java.util.List;

public class DataSource {
    private final DBHelper dbHelper;
    public DataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Create
    public int saveGame(Game game) {
        // Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GameContract.GameEntry.COLUMN_NAME_POINTS, game.getmPoints());
        values.put(GameContract.GameEntry.COLUMN_NAME_NAME, "");
        values.put(GameContract.GameEntry.COLUMN_NAME_PROGRESS, game.getmProgress());
        values.put(GameContract.GameEntry.COLUMN_NAME_DATE, game.getmDate());
        int gameID = (int) db.insert(GameContract.GameEntry.TABLE_NAME, null, values);
        db.close(); // Closing database connection

        return gameID;
    }

    // Select (gets the 8 finished games with the highest scores and returns them as a list of games)
    public List<Game> getHighScores() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                GameContract.GameEntry.COLUMN_NAME_ID + ',' +
                GameContract.GameEntry.COLUMN_NAME_NAME + ',' +
                GameContract.GameEntry.COLUMN_NAME_POINTS + ',' +
                GameContract.GameEntry.COLUMN_NAME_PROGRESS + ',' +
                GameContract.GameEntry.COLUMN_NAME_DATE +
                " FROM " + GameContract.GameEntry.TABLE_NAME +
                " WHERE " + GameContract.GameEntry.COLUMN_NAME_PROGRESS + " = -1" +
                " ORDER BY " + GameContract.GameEntry.COLUMN_NAME_POINTS + " DESC LIMIT 10";

        List<Game> gameList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Game game = new Game();
                game.setmID(cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_ID)));
                game.setmName(cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_NAME)));
                game.setmPoints(cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_POINTS)));
                game.setmProgress(cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_PROGRESS)));
                game.setmDate(cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_DATE)));
                gameList.add(game);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return gameList;
    }

    // Select (for resume, gets the non-finished game and returns it as a game object)
    public Game getCurrentGame() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                GameContract.GameEntry.COLUMN_NAME_ID + ',' +
                GameContract.GameEntry.COLUMN_NAME_POINTS + ',' +
                GameContract.GameEntry.COLUMN_NAME_PROGRESS + ',' +
                GameContract.GameEntry.COLUMN_NAME_DATE  +
                " FROM " + GameContract.GameEntry.TABLE_NAME +
                " WHERE " + GameContract.GameEntry.COLUMN_NAME_PROGRESS + " != -1";

        Game game = new Game(-1, 0, -1);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            game = new Game();
            game.setmID(cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_ID)));
            game.setmPoints(cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_POINTS)));
            game.setmProgress(cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_PROGRESS)));
            game.setmDate(cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_DATE)));
        }
        cursor.close();
        db.close();

        return game;
    }

    // Delete (removes all unfinished games, used before new currently saved game is added)
    public void removePrevious() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(GameContract.GameEntry.TABLE_NAME, GameContract.GameEntry.COLUMN_NAME_PROGRESS + "!=?", new String[] {"-1"});
        db.close();
    }

    // Delete (removes specific record from db, used from high score screen on swipe)
    public void removeGame(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(GameContract.GameEntry.TABLE_NAME, GameContract.GameEntry.COLUMN_NAME_ID + "=?", new String[] {String.valueOf(id)});
        db.close();
    }
}
