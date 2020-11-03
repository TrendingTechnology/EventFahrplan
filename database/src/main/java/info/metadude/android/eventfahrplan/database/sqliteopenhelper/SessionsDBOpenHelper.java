package info.metadude.android.eventfahrplan.database.sqliteopenhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import info.metadude.android.eventfahrplan.database.contract.FahrplanContract.SessionsTable;
import info.metadude.android.eventfahrplan.database.contract.FahrplanContract.SessionsTable.Columns;
import info.metadude.android.eventfahrplan.database.contract.FahrplanContract.SessionsTable.Indices;

public class SessionsDBOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;

    private static final String DATABASE_NAME = "sessions";

    private static final String SESSIONS_TABLE_CREATE =
            "CREATE TABLE " + SessionsTable.NAME + " (" +
                    Columns.SESSION_ID + " TEXT, " +
                    Columns.TITLE + " TEXT, " +
                    Columns.SUBTITLE + " TEXT, " +
                    Columns.DAY + " INTEGER, " +
                    Columns.ROOM + " TEXT, " +
                    Columns.SLUG + " TEXT, " +
                    Columns.START + " INTEGER, " +
                    Columns.DURATION + " INTEGER, " +
                    Columns.SPEAKERS + " TEXT, " +
                    Columns.TRACK + " TEXT, " +
                    Columns.TYPE + " TEXT, " +
                    Columns.LANG + " TEXT, " +
                    Columns.ABSTRACT + " TEXT, " +
                    Columns.DESCR + " TEXT, " +
                    Columns.REL_START + " INTEGER, " +
                    Columns.DATE + " TEXT, " +
                    Columns.LINKS + " TEXT, " +
                    Columns.DATE_UTC + " INTEGER, " +
                    Columns.ROOM_IDX + " INTEGER, " +
                    Columns.REC_LICENSE + " TEXT, " +
                    Columns.REC_OPTOUT + " INTEGER," +
                    Columns.URL + " TEXT," +
                    Columns.CHANGED_TITLE + " INTEGER," +
                    Columns.CHANGED_SUBTITLE + " INTEGER," +
                    Columns.CHANGED_ROOM + " INTEGER," +
                    Columns.CHANGED_DAY + " INTEGER," +
                    Columns.CHANGED_SPEAKERS + " INTEGER," +
                    Columns.CHANGED_RECORDING_OPTOUT + " INTEGER," +
                    Columns.CHANGED_LANGUAGE + " INTEGER," +
                    Columns.CHANGED_TRACK + " INTEGER," +
                    Columns.CHANGED_IS_NEW + " INTEGER," +
                    Columns.CHANGED_TIME + " INTEGER," +
                    Columns.CHANGED_DURATION + " INTEGER," +
                    Columns.CHANGED_IS_CANCELED + " INTEGER)";

    private static final String SESSIONS_INDEX_CREATE =
            "CREATE UNIQUE INDEX " + Indices.SESSION_ID + " ON " + SessionsTable.NAME + "(" + Columns.SESSION_ID + ")";

    public SessionsDBOpenHelper(@NonNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(SESSIONS_TABLE_CREATE);
        db.execSQL(SESSIONS_INDEX_CREATE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SessionsTable.NAME);
        onCreate(db);
    }
}
