package test.realplayers.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import test.realplayers.models.Player;

/**
 * Created by slon on 22.03.2017.
 */

public interface ApiRequests {
    @GET("teams/{team}/players")
    Call<PlayersResponse> getPlayers(@Path("team") long teamId);

    class PlayersResponse {
        List<Player> players;
    }
}
