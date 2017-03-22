package test.realplayers.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Size;

import java.util.List;

import test.realplayers.models.Player;

/**
 * Created by slon on 22.03.2017.
 */

public class DbOpenHelper extends SQLiteOpenHelper {
    public static final String PLAYERS_TABLE = "players";

    private static final int DATABASE_VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context.getApplicationContext(), "db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPlayersTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion + 1; i <= newVersion; i++) {
            upgradeDB(db, oldVersion, i);
        }
        db.setVersion(newVersion);
    }

    private void upgradeDB(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
        }
    }

    private void createPlayersTable(SQLiteDatabase db) {
        db.execSQL("create table " + PLAYERS_TABLE + "("+
                PlayerColumns._ID + " integer primary key on conflict replace," +
                PlayerColumns.NAME + " text," +
                PlayerColumns.POSITION + " text," +
                PlayerColumns.JERSEY_NUMBER + " integer," +
                PlayerColumns.CONTRACT_UNTIL + " text," +
                PlayerColumns.MARKET_VALUE + " text);");
    }

    public static class PlayerColumns implements BaseColumns {
        public static final String NAME = "name";
        public static final String POSITION = "position";
        public static final String JERSEY_NUMBER = "jersey_number";
        public static final String CONTRACT_UNTIL = "contract_until";
        public static final String MARKET_VALUE = "market_value";
    }

    public boolean insertPlayers(List<Player> models) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(PLAYERS_TABLE, null, null);

        if (models != null && models.size() > 0) {
            String sql = makeInsertQuery(PLAYERS_TABLE, new String[]{
                    PlayerColumns._ID,
                    PlayerColumns.NAME,
                    PlayerColumns.POSITION,
                    PlayerColumns.JERSEY_NUMBER,
                    PlayerColumns.CONTRACT_UNTIL,
                    PlayerColumns.MARKET_VALUE});
            SQLiteStatement statement = db.compileStatement(sql);
            if (statement != null) {
                db.beginTransaction();
                try {
                    for (Player model : models) {
                        statement.clearBindings();
                        statement.bindLong(1, model.getId());
                        bindString(statement, 2, model.getName());
                        bindString(statement, 3, model.getPosition());
                        statement.bindLong(4, model.getJerseyNumber());
                        bindString(statement, 5, model.getContractUntil());
                        bindString(statement, 6, model.getMarketValue());
                        statement.execute();
                    }
                    db.setTransactionSuccessful();

                    return true;
                } finally {
                    db.endTransaction();
                }
            }
        }
        return false;
    }

    private static String makeInsertQuery(String table, @Size(min = 1) String[] strings) {
        StringBuilder builder = new StringBuilder("INSERT INTO ");
        builder.append(table);
        builder.append(" (");

        int n = strings.length;
        builder.append(strings[0]);
        for (int i = 1; i < n; i++) {
            builder.append(',');
            builder.append(strings[i]);
        }
        builder.append(") VALUES (?");
        for (int i = 1; i < n; i++) {
            builder.append(",?");
        }
        builder.append(")");
        //Log.d("Provider", builder.length() + " " + builder.toString());
        return builder.toString();
    }

    private static void bindString(SQLiteStatement statement, int index, String insertedObject) {
        if (insertedObject == null) {
            statement.bindNull(index);
        } else {
            statement.bindString(index, insertedObject);
        }
    }
}
