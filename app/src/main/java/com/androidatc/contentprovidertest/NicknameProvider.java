package com.androidatc.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by dieterbiedermann on 08.06.17.
 */

public class NicknameProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.androidatc.ContentProviderTest.NicknameProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/nickname";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String ID = "id";
    static final String NAME = "name";
    static final String NICK_NAME = "nickname";

    static final int NICKNAME = 1;
    static final int NICKNAME_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "nicknames", NICKNAME);
        uriMatcher.addURI(PROVIDER_NAME, "nicknames/#", NICKNAME_ID);
    }

    private static final String dbName = "data";
    private static final String tblName = "nickname";

    private Context context;
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private int dbVersion = 1;

    private static class DBHelper extends SQLiteOpenHelper {

        private static final String createQuery = "create table "
                + tblName
                + " (   "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "nickname TEXT"
                + ");";

        private static final String updateQuery = "DROP TABLE IF EXISTS "
                + tblName
                + ";";

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(updateQuery);
            onCreate(db);
        }

    }


    @Override
    public boolean onCreate() {
        context = getContext();
        dbHelper = new DBHelper(context, dbName, null, dbVersion);

        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        String[] columns = {"id","name","nickname"};

        switch (uriMatcher.match(uri)) {
            case NICKNAME:
                break;

            case NICKNAME_ID:
                selection = "id=" + uri.getPathSegments().get(1);
                break;

            default:
        }
        Cursor c = db.query(tblName, columns, selection, null, null, null, null);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long rowID = db.insert(	tblName, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int count = db.delete(tblName, null, null);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int count = db.update(tblName, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
