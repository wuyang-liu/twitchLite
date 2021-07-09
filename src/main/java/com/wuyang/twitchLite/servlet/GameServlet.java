package com.wuyang.twitchLite.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuyang.twitchLite.external.TwitchClient;
import com.wuyang.twitchLite.external.TwitchException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "game", value = "/game")
public class GameServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String gameName = request.getParameter("game_name");
    TwitchClient twitchClient = new TwitchClient();
    response.setContentType("application/json;charset=UTF-8");
    try {
      if (gameName != null) {
        response.getWriter().print(new ObjectMapper().writeValueAsString(twitchClient.searchGame(gameName)));
      } else {
        response.getWriter().print(new ObjectMapper().writeValueAsString(twitchClient.topGames(10)));
      }
    } catch (TwitchException e) {
      throw new ServletException(e);
    }
  }
  
}
