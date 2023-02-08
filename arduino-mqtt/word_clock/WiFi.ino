void setupWiFi() {
  setupWifiConnectionBlocking();
}

void setupWifiConnectionBlocking(){
  WiFiManager wifiManager;
  //reset settings - for testing
  //wifiManager.resetSettings();

  displayWiFi();

  //set callback that gets called when connecting to previous WiFi fails, and enters Access Point mode
  wifiManager.setAPCallback(configModeCallback);
  wifiManager.setTimeout(180);

  //fetches ssid and pass and tries to connect
  //if it does not connect it starts an access point with the specified name
  //here  "AutoConnectAP"
  //and goes into a blocking loop awaiting configuration
  if (!wifiManager.autoConnect("WordClock")) {
    Serial.println("failed to connect and hit timeout");
    //restart and try again, or maybe put it to deep sleep
    ESP.restart();
    delay(1000);
  }
}

//gets called when WiFiManager enters configuration mode
void configModeCallback (WiFiManager *myWiFiManager) {
  Serial.println("Entering config mode");
  Serial.println(WiFi.softAPIP());
  //if you used auto generated SSID, print it
  Serial.println(myWiFiManager->getConfigPortalSSID());
}

