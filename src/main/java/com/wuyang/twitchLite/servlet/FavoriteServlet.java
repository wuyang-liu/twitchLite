package com.wuyang.twitchLite.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuyang.twitchLite.db.MySQLConnection;
import com.wuyang.twitchLite.db.MySQLException;
import com.wuyang.twitchLite.entity.FavoriteRequestBody;
import com.wuyang.twitchLite.entity.Item;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FavoriteServlet", value = "/favorite")
public class FavoriteServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String userId = request.getParameter("user_id");
    Map<String, List<Item>> itemMap;
    MySQLConnection connection = null;
    try {
      // Read the favorite items from the database
      connection = new MySQLConnection();
      itemMap = connection.getFavoriteItems(userId);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().print(new ObjectMapper().writeValueAsString(itemMap));
    } catch (MySQLException e) {
      throw new ServletException(e);
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
    
  }
  
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String userId = request.getParameter("user_id");
    ObjectMapper mapper = new ObjectMapper();
    FavoriteRequestBody body = mapper.readValue(request.getReader(), FavoriteRequestBody.class);
    if (body == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    
    MySQLConnection connection = null;
    try {
      connection = new MySQLConnection();
      connection.setFavoriteItem(userId, body.getFavoriteItem());
    } catch (MySQLException e) {
      throw new ServletException(e);
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }
  
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String userId = request.getParameter("user_id");
    ObjectMapper mapper = new ObjectMapper();
    FavoriteRequestBody body = mapper.readValue(request.getReader(), FavoriteRequestBody.class);
    if (body == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    
    MySQLConnection connection = null;
    try {
      // Remove the favorite item to the database
      connection = new MySQLConnection();
      connection.unsetFavoriteItem(userId, body.getFavoriteItem().getId());
    } catch (MySQLException e) {
      throw new ServletException(e);
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }
}
