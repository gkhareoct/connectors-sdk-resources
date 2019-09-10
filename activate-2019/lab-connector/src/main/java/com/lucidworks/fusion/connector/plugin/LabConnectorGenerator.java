package com.lucidworks.fusion.connector.plugin;

public interface LabConnectorGenerator {

  String makeHeadline();

  String makeSentence(boolean isHeadline);

  String makeText(int numSentences);
}
