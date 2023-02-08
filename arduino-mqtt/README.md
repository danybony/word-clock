# Arduino Word Clock
The Arduino-powered version of Word Clock.

This clock uses WiFiManager to create a WiFi access point (whith the name `WordClock`) the first time that the device boots. 
The access point leads to a captive portal where the user can insert the WiFi credential that will be used later to connect to the Internet.
Such connection is used to connect to a local network MQTT broker connected with Home Assistant.

## Software dependencies
* ESP8266WiFi: https://github.com/esp8266/Arduino
* WiFiManager: https://github.com/tzapu/WiFiManager
* Adafruit NeoPixel: https://github.com/adafruit/Adafruit_NeoPixel
* Arduino Home Assistant: https://github.com/dawidchyrzynski/arduino-home-assistant
