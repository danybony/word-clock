#define LIGHT_SENSOR_DATA_PIN A0

void loopLightSensor() {
    // The light sensor retunts values in the range 0 - 1023
    float rawValue = analogRead(LIGHT_SENSOR_DATA_PIN) / 1023.0;
    ambientBrightness = rawValue / 1023.0;
}

