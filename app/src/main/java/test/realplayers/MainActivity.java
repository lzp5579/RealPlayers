package test.realplayers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import test.realplayers.api.ApiService;
import test.realplayers.db.DbContentProvider;
import test.realplayers.db.DbOpenHelper;
import test.realplayers.models.Player;
import test.realplayers.util.AppLog;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivity";

    private static final String ARG_CHECK_REMOTE = "check_remote";
    private static final int EDIT_PLAYER_REQUEST_CODE = 1;
    private static final String STATE_SCROLL_POSITION = "scroll_position";

    private PlayersAdapter adapter;
    private View progress;
    private boolean supressOnResumeLoaderRestart;
    private ListView playersList;
    private int scrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = findViewById(R.id.progress);

        playersList = (ListView) findViewById(R.id.playersList);
        adapter = new PlayersAdapter(this, null);
        playersList.setAdapter(adapter);
        playersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (progress.getVisibility() == View.VISIBLE) return;

                Cursor cursor = adapter.getCursor();
                if (cursor.moveToPosition(position)) {
                    Player player = new Player(cursor);
                    startActivityForResult(new Intent(MainActivity.this, EditPlayerActivity.class)
                            .putExtra(EditPlayerActivity.EXTRA_PLAYER, player), EDIT_PLAYER_REQUEST_CODE);
                }
            }
        });
        scrollPosition = savedInstanceState != null ? savedInstanceState.getInt(STATE_SCROLL_POSITION):0;

        supressOnResumeLoaderRestart = true;

        AppLog.d(TAG, "initLoader from " + (savedInstanceState != null ? " restart" : "create"));
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SCROLL_POSITION, playersList.getFirstVisiblePosition());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                progress.setVisibility(View.VISIBLE);
                loadRemoteData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_PLAYER_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    AppLog.d(TAG, "restarting loader from onActivityResult");

                    //onresume called after onactivityresult
                    supressOnResumeLoaderRestart = true;

                    Bundle args = new Bundle();
                    args.putBoolean(ARG_CHECK_REMOTE, false);
                    getSupportLoaderManager().restartLoader(0, args, this);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ApiService.ACTION_PLAYERS));

        if (supressOnResumeLoaderRestart) {
            supressOnResumeLoaderRestart = false;
        } else {
            AppLog.d(TAG, "restarting loader from onResume");
            getSupportLoaderManager().restartLoader(0, null, this);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ApiService.ACTION_PLAYERS:
                        if (intent.getBooleanExtra(ApiService.EXTRA_IS_SUCCESSFUL, false)) {
                            AppLog.d(TAG, "restarting loader from receiver");

                            Bundle args = new Bundle();
                            args.putBoolean(ARG_CHECK_REMOTE, false);
                            getSupportLoaderManager().restartLoader(0, args, MainActivity.this);
                        } else {
                            progress.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        boolean checkRemote = args != null ? args.getBoolean(ARG_CHECK_REMOTE, false) : true;
        return new MyLoader(this, checkRemote);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        AppLog.d(TAG, "onLoadFinished");

        adapter.swapCursor(data);

        if (data == null || data.getCount() == 0 && ((MyLoader) loader).isCheckRemote()) {
            loadRemoteData();
        } else {
            progress.setVisibility(View.GONE);
            if (scrollPosition > 0) {
                playersList.smoothScrollToPosition(scrollPosition);
                scrollPosition = 0;
            }
        }
    }

    private void loadRemoteData() {
        AppLog.d(TAG, "loadRemoteData");

        Intent intent = new Intent(ApiService.ACTION_PLAYERS, null, this, ApiService.class)
                .putExtra(ApiService.EXTRA_TEAM_ID, 66l);
        startService(intent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private static class MyLoader extends CursorLoader {
        private final boolean checkRemote;

        public MyLoader(Context context, boolean checkRemote) {
            super(context.getApplicationContext(), Uri.withAppendedPath(DbContentProvider.CONTENT_URI, DbOpenHelper.PLAYERS_TABLE), null, null, null, null);
            this.checkRemote = checkRemote;
        }

        @Override
        public Cursor loadInBackground() {
            AppLog.d("MyLoader", "loadInBackground");
            return super.loadInBackground();
        }

        public boolean isCheckRemote() {
            return checkRemote;
        }
    }
}
