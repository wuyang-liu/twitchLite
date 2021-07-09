package com.wuyang.twitchLite.external;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TwitchClient {
  // TODO: make them envs
  private static final String TOKEN = "Bearer 0v6j8afz58uo5nmb8hbjvgi7ajnoet";
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
  
  private String searchTwitch(String url) throws TwitchException {
  
    CloseableHttpClient httpClient = HttpClients.createDefault();
    
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
      HttpGet request = new HttpGet(url);
      request.setHeader("Authorization", TOKEN);
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
  
}
