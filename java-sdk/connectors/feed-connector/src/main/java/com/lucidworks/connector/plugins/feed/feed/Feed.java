package com.lucidworks.connector.plugins.feed.feed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Feed {

  private final Map<String, FeedEntry> entries;

  @JsonCreator
  public Feed(
      @JsonProperty("entries") Map<String, FeedEntry> entries
  ) {
    this.entries = entries;
  }

  public Map<String, FeedEntry> getEntries() {
    return entries;
  }

}