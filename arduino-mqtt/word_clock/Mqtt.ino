#define BROKER_ADDR IPAddress(192,168,1,20) // Update your broker IP address

#define BROKER_USERNAME     "" // replace with your credentials
#define BROKER_PASSWORD     ""

#define TIME_TOPIC   "kitchen/clock/time"

WiFiClient client;
HADevice device("WordClock");
HAMqtt mqtt(client, device);

void setupMqtt() {
  device.setName("WordClock");
  device.setSoftwareVersion("1.0.0");
  device.setManufacturer("Daniele Bonaldo");

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
    // For example: if (memcmp(topic, "myCustomTopic") == 0) { ... }

    Serial.print("New message on topic: ");
    Serial.println(topic);
    Serial.print("Data: ");
    Serial.println((const char*)payload);

    if (strcmp(topic, TIME_TOPIC) == 0) { 
      Serial.print("Time received: "); Serial.println((const char*)payload);
      
      char * hoursChar = strtok ((char*)payload, ":");
      Serial.println(hoursChar);
      ch = atoi(hoursChar);

      char * minChar = strtok (NULL, ":");
      Serial.println(minChar);
      cm = atoi(minChar);
      updateDisplay = true;
      
    }
//    mqtt.publish("myPublishTopic", "hello");
}

void onMqttConnected() {
    Serial.println("Connected to the broker!");

    // You can subscribe to custom topic if you need
    mqtt.subscribe(TIME_TOPIC);
}
