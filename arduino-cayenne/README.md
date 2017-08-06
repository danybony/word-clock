# Arduino Word Clock
The Arduino-powered version of Word Clock with support for Cayenne MQTT.

This clock is based on [SparkFun ESP8266 Thing Dev board](https://www.sparkfun.com/products/13711) and uses [Cayenne](https://cayenne.mydevices.com/) to configure some paramenters of the clock (color, brightness and time zone). 
The current time is automatically updated using NTP.

## Software dependencies
* ESP8266WiFi: https://github.com/esp8266/Arduino
* WiFiManager: https://github.com/tzapu/WiFiManager
* Adafruit NeoPixel: https://github.com/adafruit/Adafruit_NeoPixel
* Adafruit RTClib: https://github.com/adafruit/RTClib

## Schematics
![](https://raw.githubusercontent.com/danybony/word-clock/master/arduino-cayenne/schematics/schematics.png)

## Development
To build the firmware using Arduino IDE, please add a new file named `Credentials.h` to the sketch folder, including the WiFi parameters and login details for the Cayenne Dashboard. Please have a look at the provided `Credentials_example.h` file.

After the firmware is update and the clock connects to the Internet, it will be possible to update its configuration directly from the Cayenne dashboard.

![](https://raw.githubusercontent.com/danybony/word-clock/master/arduino-cayenne/images/cayenne_dashboard.png)
