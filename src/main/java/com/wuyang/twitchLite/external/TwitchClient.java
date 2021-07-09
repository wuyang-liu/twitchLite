package com.wuyang.twitchLite.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuyang.twitchLite.entity.Game;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class TwitchClient {
  // TODO: make them envs
  private static final String TWITCH_TOKEN = "Bearer 7i3rlcpuojiqq8uyqab9eygsmmral4";
  private static final String CLIENT_ID = "fi2p2rh3o5zka17z4qaagunua94i4e";
  private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
  private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
  private static final int DEFAULT_GAME_LIMIT = 20;
  
  private String buildGameURL(String url, String gameName, int limit) {
    if (gameName.equals("")) {
      return String.format(url, limit);
    }
    try {
      gameName = URLEncoder.encode(gameName, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return String.format(url, gameName);
  }
  
  // Send HTTP request to Twitch Backend based on the given URL,
  // and returns the body of the HTTP response returned from Twitch backend.
  private String searchTwitch(String url) throws TwitchException {
    
    CloseableHttpClient httpClient = HttpClients.createDefault();
    // Define the response handler to parse and return HTTP response body returned from Twitch
    ResponseHandler<String> responseHandler = httpResponse -> {
      int responseCode = httpResponse.getStatusLine().getStatusCode();
      if (responseCode != 200) {
        System.out.println("Response status: " + httpResponse.getStatusLine().getReasonPhrase());
        throw new TwitchException("Failed to get result from Twitch API");
      }
      HttpEntity entity = httpResponse.getEntity();
      if (entity == null) {
        throw new TwitchException("Failed to get result from Twitch API");
      }
      JSONObject object = new JSONObject(EntityUtils.toString(entity));
      return object.getJSONArray("data").toString();
    };
    
    try {
      // Define the HTTP request, TOKEN and CLIENT_ID are used for user authentication on Twitch backend
      HttpGet request = new HttpGet(url);
      request.setHeader("Authorization", TWITCH_TOKEN);
      request.setHeader("Client-Id", CLIENT_ID);
      return httpClient.execute(request, responseHandler);
    } catch (IOException e) {
      e.printStackTrace();
      throw new TwitchException("Failed to get result from Twitch API");
    } finally {
      try {
        httpClient.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  // Convert JSON format data returned from Twitch to an Arraylist of Game objects
  private List<Game> getGameList(String data) throws TwitchException {
    
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<Game> result = Arrays.asList(mapper.readValue(data, Game[].class));
      return result;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new TwitchException("Failed to parse game data from Twitch API");
    }
  }
  
  // Integrate search() and getGameList() together, returns the top x popular games from Twitch.
  public List<Game> topGames(int limit) throws TwitchException {
    if (limit < 0) {
      limit = DEFAULT_GAME_LIMIT;
    }
    
    return getGameList(searchTwitch(buildGameURL(TOP_GAME_URL, "", limit)));
  }
  
  // Integrate search() and getGameList() together, returns the dedicated game based on the game name.
  public Game searchGame(String gameName) throws TwitchException {
    String url = buildGameURL(GAME_SEARCH_URL_TEMPLATE, gameName, 0);
    List<Game> gameList = getGameList(searchTwitch(url));
    if (gameList.size() != 0) {
      return gameList.get(0);
    }
    return null;
  }
}
