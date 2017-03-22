package test.realplayers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import test.realplayers.db.DbContentProvider;
import test.realplayers.db.DbOpenHelper;
import test.realplayers.models.Player;

public class EditPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_PLAYER = "player";
    private EditText position;
    private EditText jerseyNumber;
    private EditText contractUntil;
    private EditText marketValue;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_player);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        player = getIntent().getParcelableExtra(EXTRA_PLAYER);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(player.getName());

        position = (EditText)findViewById(R.id.position);
        jerseyNumber = (EditText)findViewById(R.id.jerseyNumber);
        contractUntil = (EditText)findViewById(R.id.contractUntil);
        marketValue = (EditText)findViewById(R.id.marketValue);

        findViewById(R.id.reset).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        if (savedInstanceState == null) {
            initFields();
        }
    }

    private void initFields() {
        position.setText(player.getPosition());
        jerseyNumber.setText(player.getJerseyNumberString());
        contractUntil.setText(player.getContractUntil());
        marketValue.setText(player.getMarketValue());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                save();
                break;

            case R.id.reset:
                initFields();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        int number = -1;
        try {
            number = Integer.parseInt(jerseyNumber.getText().toString());
            jerseyNumber.setError(null);
        } catch (NumberFormatException e) {
        }
        if (number <= 0) {
            jerseyNumber.setError(getString(R.string.invalid_jersey_number));
            return;
        }
        String contractDate = contractUntil.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sdf.parse(contractDate);
            contractUntil.setError(null);
        } catch (ParseException e) {
            contractUntil.setError(getString(R.string.invalid_date_number));
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbOpenHelper.PlayerColumns.POSITION, position.getText().toString());
        contentValues.put(DbOpenHelper.PlayerColumns.JERSEY_NUMBER, number);
        contentValues.put(DbOpenHelper.PlayerColumns.CONTRACT_UNTIL, contractDate);
        contentValues.put(DbOpenHelper.PlayerColumns.MARKET_VALUE, marketValue.getText().toString());

        Uri uri = Uri.withAppendedPath(DbContentProvider.CONTENT_URI, DbOpenHelper.PLAYERS_TABLE);

        getContentResolver().update(uri, contentValues, DbOpenHelper.PlayerColumns._ID + "=?", new String[]{String.valueOf(player.getId())});

        setResult(Activity.RESULT_OK);
        finish();
    }
}
