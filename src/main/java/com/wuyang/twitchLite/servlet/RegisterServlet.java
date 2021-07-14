package com.wuyang.twitchLite.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuyang.twitchLite.db.MySQLConnection;
import com.wuyang.twitchLite.db.MySQLException;
import com.wuyang.twitchLite.entity.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
  
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      User user = mapper.readValue(request.getReader(), User.class);
      if (user == null) {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          return;
      }
      boolean isUserAdded = false;
      MySQLConnection connection = null;
      try {
          connection = new MySQLConnection();
          user.setPassword(ServletUtil.encryptPassword(user.getUserId(), user.getPassword()));
          isUserAdded = connection.addUser(user);
      } catch (MySQLException e) {
          throw new ServletException(e);
      } finally {
          connection.close();
      }
      if (!isUserAdded) {
          response.setStatus(HttpServletResponse.SC_CONFLICT);
      }
  }
}
