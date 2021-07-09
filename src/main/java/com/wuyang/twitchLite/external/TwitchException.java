package com.wuyang.twitchLite.external;

public class TwitchException extends RuntimeException{
  public TwitchException(String errorMessage) {
    super(errorMessage);
  }
}
