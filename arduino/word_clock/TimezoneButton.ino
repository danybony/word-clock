const int buttonPin = D3;

int buttonState = LOW;

void setupTimezoneButton() {
  pinMode(buttonPin, INPUT);  
}

void loopTimezoneButton() {
  buttonState = digitalRead(buttonPin);
  
  if (buttonState == LOW) {
    timezoneOffset++;
    if (timezoneOffset == 24) {
      timezoneOffset = 0;
    }
    updateDisplay = true;
  }
}
