#define BROKER_ADDR IPAddress(192,168,1,20) // Update your broker IP address

#define BROKER_USERNAME     "" // replace with your credentials
#define BROKER_PASSWORD     ""

#define TIME_TOPIC   "kitchen/clock/time"

WiFiClient client;
HADevice device("WordClock");
HAMqtt mqtt(client, device);

HALight light("WordClock", HALight::BrightnessFeature | HALight::RGBFeature);

void setupMqtt() {
  device.setName("WordClock");
  device.setSoftwareVersion("1.0.0");
  device.setManufacturer("Daniele Bonaldo");

  light.setName("WordClock");
  light.setRetain(true);
  light.onStateCommand(onStateCommand);
  light.onBrightnessCommand(onBrightnessCommand);
  light.onRGBColorCommand(onRGBColorCommand);

  mqtt.onMessage(onMqttMessage);
  mqtt.onConnected(onMqttConnected);

  mqtt.begin(BROKER_ADDR, BROKER_USERNAME, BROKER_PASSWORD);
  Serial.println("MQTT started!");
}

void loopMqtt() {
  mqtt.loop();
}

void onMqttMessage(const char* topic, const uint8_t* payload, uint16_t length) {
    // This callback is called when message from MQTT broker is received.
    // Please note that you should always verify if the message's topic is the one you expect.

//    Serial.print("New message on topic: ");
//    Serial.println(topic);
//    Serial.print("Data: ");
//    Serial.println((const char*)payload);

    if (strcmp(topic, TIME_TOPIC) == 0) { 
      Serial.print("Time received: "); Serial.println((const char*)payload);
      
      char * hoursChar = strtok ((char*)payload, ":");
      ch = atoi(hoursChar);

      char * minChar = strtok (NULL, ":");
      cm = atoi(minChar);
      updateDisplay = true; 
    }
}

void onMqttConnected() {
    Serial.println("Connected to the broker!");

    mqtt.subscribe(TIME_TOPIC);
}

void onStateCommand(bool state, HALight* sender) {
    Serial.print("State: ");
    Serial.println(state);
    turnedOn = state;
    updateDisplay = true;

    sender->setState(state); // report state back to the Home Assistant
}

void onBrightnessCommand(uint8_t brightness, HALight* sender) {
    Serial.print("Brightness: ");
    Serial.println(brightness);
    lettersBrightness = brightness;
    updateDisplay = true;

    sender->setBrightness(brightness); // report brightness back to the Home Assistant
}

void onRGBColorCommand(HALight::RGBColor color, HALight* sender) {
    Serial.print("Red: ");
    Serial.println(color.red);
    Serial.print("Green: ");
    Serial.println(color.green);
    Serial.print("Blue: ");
    Serial.println(color.blue);
    colorOn = pixels.Color(color.red, color.green, color.blue);
    updateDisplay = true;

    sender->setRGBColor(color); // report color back to the Home Assistant
}
