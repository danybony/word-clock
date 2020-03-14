#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager
#include <Adafruit_NeoPixel.h>    //https://github.com/adafruit/Adafruit_NeoPixel
#include <RTClib.h>  

const int LEDS_COUNT = 144;
const int LEDS_SIDE = 12;

int ch,cm,cs,cdy,cmo,cyr,cdw;          // current time & date variables
int om = -1;
int nh,nm,ns,ndy,nmo,nyr,ndw;          // NTP-based time & date variables
int timezoneOffset = 0;

boolean updateDisplay = false;
float ambientBrightness = 0.2;

void setup() {
  Serial.begin(115200);
  
  setupTimezoneButton();
  setupLEDs();
  setupWiFi();
  setupNTP();
}

void loop() {
//  loopLightSensor();
  loopTimezoneButton();
  loopNTP();

  if (outsideWorkingTime()) {
    displayOff();
  } else if (updateDisplay) {
    displayTime((ch + timezoneOffset) % 24, cm, cs);
    updateDisplay = false;
  }

  delay(1000);
}

boolean outsideWorkingTime() {
  return (ch > 1 && ch < 6)
      || (ch == 6 && cm < 45);
}
