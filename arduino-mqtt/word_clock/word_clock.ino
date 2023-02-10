#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager
#include <Adafruit_NeoPixel.h>    //https://github.com/adafruit/Adafruit_NeoPixel
//#include <Ethernet.h>
#include <ArduinoHA.h>            //https://github.com/dawidchyrzynski/arduino-home-assistant
#include "SafeString.h"           //https://www.forward.com.au/pfod/ArduinoProgramming/SafeString/index.html

const int LEDS_COUNT = 144;
const int LEDS_SIDE = 12;

int ch,cm;          // current time variables

boolean updateDisplay = false;
boolean turnedOn = true;
float lettersBrightness = 50; // 0-255

void setup() {
  Serial.begin(115200);
  
  setupLEDs();
  setupWiFi();
  setupMqtt();
}

void loop() {
  loopMqtt();

  if (!turnedOn) {
    displayOff();
  } else if (updateDisplay) {
    displayTime(ch, cm);
    updateDisplay = false;
  }
}
