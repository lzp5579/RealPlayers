package test.realplayers.api;

import android.app.Service;
import android.content.ContentProvider;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.realplayers.db.DbContentProvider;
import test.realplayers.db.DbOpenHelper;
import test.realplayers.util.AppLog;

public class ApiService extends Service {
    public final static String ACTION_PLAYERS = "players";

    public static final String EXTRA_TEAM_ID = "team_id";
    public static final String EXTRA_IS_SUCCESSFUL = "success";

    public ApiService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_PLAYERS:
                    getPlayers(intent.getLongExtra(EXTRA_TEAM_ID, 0));
                    break;
            }
        } else {
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    private void sendResult(String action, boolean isSuccessful) {
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA_IS_SUCCESSFUL, isSuccessful);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        stopSelf();
    }

    private void getPlayers(long teamId) {
        Api.get().getPlayers(teamId).enqueue(new Callback<ApiRequests.PlayersResponse>() {
            @Override
            public void onResponse(Call<ApiRequests.PlayersResponse> call, final Response<ApiRequests.PlayersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new AsyncTask<Void,Void,Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            DbContentProvider.getDbHelper(ApiService.this).insertPlayers(response.body().players);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            sendResult(ACTION_PLAYERS, true);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                sendResult(ACTION_PLAYERS, false);
            }
            @Override
            public void onFailure(Call<ApiRequests.PlayersResponse> call, Throwable t) {
                AppLog.e("ApiService", t.getMessage(), t);
                sendResult(ACTION_PLAYERS, false);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
