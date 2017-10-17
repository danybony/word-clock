# Android Things Word Clock
The Android Things-powered version of Word Clock.

This clock uses Android Things to control all the logic related to which LEDs need to be on to display the current time.
At the moment the only supported language is English.

The clock can be made with both WS2801 or WS2812B LEDs strips. 
The only difference is that WS2801 LEDs will work directly with a Raspberry Pi (or other Android Things compatible board), while WS2812B LEDs will require a separate unit to control them (I used an Arduino Nano connected to the Android Things board via USB serial connection).

## Schematics
### With WS2801 LEDs
![](https://raw.githubusercontent.com/danybony/word-clock/master/AndroidThings/schematics/WS2801.png)


### With WS2812B LEDs
![](https://raw.githubusercontent.com/danybony/word-clock/master/AndroidThings/schematics/WS2812B.png)
