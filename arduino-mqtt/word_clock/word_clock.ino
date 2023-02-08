#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager
#include <Adafruit_NeoPixel.h>    //https://github.com/adafruit/Adafruit_NeoPixel
//#include <Ethernet.h>
#include <ArduinoHA.h>            //https://github.com/dawidchyrzynski/arduino-home-assistant

const int LEDS_COUNT = 144;
const int LEDS_SIDE = 12;

int ch,cm;          // current time variables

boolean updateDisplay = false;
float ambientBrightness = 0.2;

void setup() {
  Serial.begin(115200);
  
  setupLEDs();
  setupWiFi();
  setupMqtt();
}

void loop() {
  loopMqtt();

  if (outsideWorkingTime()) {
    displayOff();
  } else if (updateDisplay) {
    displayTime(ch, cm);
    updateDisplay = false;
  }

  delay(1000);
}

boolean outsideWorkingTime() {
  return (ch > 1 && ch < 6)
      || (ch == 6 && cm < 45);
}
