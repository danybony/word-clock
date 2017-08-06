void setupCayenne() {
  displayWiFi();
  Cayenne.begin(username, password, clientID, ssid, wifiPassword);
}

void loopCayenne() {
  Cayenne.loop();
}

