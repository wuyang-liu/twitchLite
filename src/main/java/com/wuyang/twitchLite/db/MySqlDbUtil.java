package com.wuyang.twitchLite.db;

import java.io.IOException;

public class MySqlDbUtil {
  private static final String DB_INSTANCE = System.getenv("DB_INSTANCE");
  private static final String DB_PORT_NUM = "3306";
  private static final String DB_NAME = "twitch_lite";
  
  public static String getMySQLAddress() {
    String DB_USERNAME = System.getenv("DB_USERNAME");
    String DB_PASSWORD = System.getenv("DB_PASSWORD");
    return String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&autoReconnect=true&serverTimezone=UTC&createDatabaseIfNotExist=true",
            DB_INSTANCE, DB_PORT_NUM, DB_NAME, DB_USERNAME, DB_PASSWORD);
  }
  
}

