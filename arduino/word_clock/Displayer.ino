#define PIXELS_DATA_PIN D2

#define sp    Serial.print
#define spln  Serial.println

Adafruit_NeoPixel pixels = Adafruit_NeoPixel(LEDS_COUNT, PIXELS_DATA_PIN, NEO_GRB + NEO_KHZ800);
uint32_t colorOn = pixels.Color(0, 255,255);
uint32_t colorOff = pixels.Color(0,0,0);

void setupLEDs() {
    pixels.begin();
}

void displayWiFi() {
  boolean matrix[LEDS_COUNT];
  getWiFiMatrix(matrix);
  display(matrix);
}

void displayTime(int h, int m, int s) {
  boolean matrix[LEDS_COUNT];
  getTimeMatrix(matrix, h, m, s);
  display(matrix);
}

void display(boolean matrix[]) {
    logMatrix(matrix);
    
    pixels.setBrightness(ambientBrightness * 255);
    updateLEDs(matrix);
    pixels.show();
}

void logMatrix(boolean matrix[]) {
  for (int i=0; i<LEDS_COUNT; i++) {
    sp(matrix[i] ? "X" : "_"); sp(" ");
    if (i % LEDS_SIDE == LEDS_SIDE-1) {
      spln();
    }
  }
}

// Our LEDs matrix is built with an Z pattern, 
// and the LEDs index order follows the following scheme:
// 
// > 0 1 2 3
//   7 6 5 4
//   8 9 10 11
//
// So we need to invert the column order in alternate rows from our logic matrix
void updateLEDs(boolean matrix[]) {
  for (int i=0; i<LEDS_COUNT; i++) {
    int row = i / LEDS_SIDE;
    int ledId;
    if (row % 2 == 0) {
      // -->
      ledId = i;
    } else {
      // <--
      ledId = (row + 1) * LEDS_SIDE - (i - row * LEDS_SIDE) - 1;
    }
    updateLED(ledId, matrix[i]);  
  }
}

void updateLED(int index, boolean isOn) {
  if (isOn) {
    pixels.setPixelColor(index, colorOn); 
  } else {
    pixels.setPixelColor(index, colorOff); 
  }
}

