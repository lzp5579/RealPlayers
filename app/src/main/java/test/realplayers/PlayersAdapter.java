package test.realplayers;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import test.realplayers.db.DbOpenHelper;

/**
 * Created by slon on 22.03.2017.
 */

public class PlayersAdapter extends CursorAdapter {
    public PlayersAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_player, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Object tag = view.getTag();
        final ViewHolder vh;
        if (tag == null) {
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder)tag;
        }
        vh.name.setText(cursor.getString(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.NAME)));
        vh.position.setText(cursor.getString(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.POSITION)));
        int jerseyNumber = cursor.getInt(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.JERSEY_NUMBER));
        vh.jerseyNumber.setText(jerseyNumber > 0 ? String.valueOf(jerseyNumber):"");
        vh.contractUntil.setText(cursor.getString(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.CONTRACT_UNTIL)));
        vh.marketValue.setText(cursor.getString(cursor.getColumnIndex(DbOpenHelper.PlayerColumns.MARKET_VALUE)));
    }

    private static class ViewHolder {
        public final TextView name;
        public final TextView position;
        public final TextView jerseyNumber;
        public final TextView contractUntil;
        public final TextView marketValue;

        ViewHolder(View v) {
            name = (TextView)v.findViewById(R.id.name);
            position = (TextView)v.findViewById(R.id.position);
            jerseyNumber = (TextView)v.findViewById(R.id.jerseyNumber);
            contractUntil = (TextView)v.findViewById(R.id.contractUntil);
            marketValue = (TextView)v.findViewById(R.id.marketValue);
        }
    }
}
