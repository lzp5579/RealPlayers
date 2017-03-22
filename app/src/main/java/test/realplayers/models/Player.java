package test.realplayers.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import test.realplayers.db.DbOpenHelper;

/**
 * Created by slon on 22.03.2017.
 */

public class Player implements Parcelable {
     private final long id;
     private final String name;
     private String position;
     private int jerseyNumber;
     private String contractUntil;
     private String marketValue;

    public Player(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(DbOpenHelper.PlayerColumns._ID));
        name = cursor.getString(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.NAME));
        position = cursor.getString(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.POSITION));
        jerseyNumber = cursor.getInt(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.JERSEY_NUMBER));
        contractUntil = cursor.getString(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.CONTRACT_UNTIL));
        marketValue = cursor.getString(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.MARKET_VALUE));
    }

    protected Player(Parcel in) {
        id = in.readLong();
        name = in.readString();
        position = in.readString();
        jerseyNumber = in.readInt();
        contractUntil = in.readString();
        marketValue = in.readString();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public String getJerseyNumberString() {
        return jerseyNumber > 0 ? String.valueOf(jerseyNumber):"";
    }

    public String getContractUntil() {
        return contractUntil;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public void setContractUntil(String contractUntil) {
        this.contractUntil = contractUntil;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(position);
        dest.writeInt(jerseyNumber);
        dest.writeString(contractUntil);
        dest.writeString(marketValue);
    }
}
