package test.realplayers.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.List;

import test.realplayers.BuildConfig;
import test.realplayers.models.Player;

/**
 * Created by slon on 22.03.2017.
 */

public class DbContentProvider extends ContentProvider {
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".DbContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final int PLAYERS = 1;
    private final UriMatcher uriMatcher;

    private static DbOpenHelper openHelper;

    public DbContentProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, DbOpenHelper.PLAYERS_TABLE, PLAYERS);
    }

    @Override
    public boolean onCreate() {
        getDbHelper(getContext());
        return true;
    }

    public static DbOpenHelper getDbHelper(Context ctx) {
        if (openHelper == null) {
            openHelper = new DbOpenHelper(ctx.getApplicationContext());
        }
        return openHelper;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = openHelper.getReadableDatabase();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case PLAYERS:
                queryBuilder.setTables(DbOpenHelper.PLAYERS_TABLE);
                break;
            default:
                return null;
        }
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = -1;
        switch (uriMatcher.match(uri)) {
            case PLAYERS:
                id = openHelper.getWritableDatabase().insertWithOnConflict(DbOpenHelper.PLAYERS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;
        }
        return id >= 0 ? ContentUris.withAppendedId(uri, id) : null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case PLAYERS:
                count = openHelper.getWritableDatabase().delete(DbOpenHelper.PLAYERS_TABLE, selection, selectionArgs);
                break;
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case PLAYERS:
                count = openHelper.getWritableDatabase().update(DbOpenHelper.PLAYERS_TABLE, values, selection, selectionArgs);
                break;
        }
        return count;
    }
}
