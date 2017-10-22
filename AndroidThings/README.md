# Android Things Word Clock
The Android Things-powered version of Word Clock.

This clock uses Android Things to control all the logic related to which LEDs need to be on to display the current time.
At the moment the only supported language is English.

The clock can be made with both WS2801 or WS2812B LEDs strips. 
The only difference is that WS2801 LEDs will work directly with a Raspberry Pi (or other Android Things compatible board), while WS2812B LEDs will require a separate unit to control them (I used an Arduino Nano connected to the Android Things board via USB serial connection).

## Schematics
#### With WS2801 LEDs
![](https://raw.githubusercontent.com/danybony/word-clock/master/AndroidThings/schematics/WS2801.png)


#### With WS2812B LEDs
![](https://raw.githubusercontent.com/danybony/word-clock/master/AndroidThings/schematics/WS2812B.png)


## Smart Home setup
Using [Smart Home](https://developers.google.com/actions/smarthome/) it is possible to change the clock LEDs color through the Google Assistant.

The following steps are required to add the clock to Home Graph, a database that stores and provides contextual data about the home and its devices.

### Firebase

* Create a new project on the [Firebase console](https://console.firebase.google.com) and note the Project ID somewhere (you'll need it soon)
* Add an Android app to this project, with the following package name: `net.bonysoft.wordclock`
* Download the `google-services.json` file and save it to the `things` module
* Create a new Firebase Realtime Database, importing the following json file
```json
{
  "clock" : {
    "on" : true,
    "spectrumRGB" : 16510692
  }
}
```
* Configure database rules to public (**don't** do that on production)
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```
* Go to Firebase's Project Settings > Service Accounts, and click on the "Generate New Private Key" button. Save the generated file in `servers/smart-home-provider-functions/serviceAccountKey.json`
* Open the `servers/smart-home-provider-functions/index.js` file use your Firebase Project ID to modify the `databaseURL` variable
* Deploy the Android app to your Android Things device. At that time, modifying the database values from the Firebase console should have an effect on the Android Things device.

### Google Cloud Functions

* Enable the Cloud Functions API and install the Google Cloud SDK ([quickstart guide](https://cloud.google.com/functions/docs/quickstart))
* Deploy the `ha` (home automation) function with the following command
```bash
cd servers/smart-home-provider-functions
gcloud beta functions deploy ha --stage-bucket staging.<PROJECT ID>.appspot.com --trigger-http
```
* This script will deploy the function to Google Cloud and print to the console the endpoint address.
This has the format similar to `https://us-central1-<PROJECT ID>.cloudfunctions.net/ha`. Write it down as you'll need it later

Note: if you want to test the functions locally, use the Cloud Functions Emulator
```bash
functions-emulator start
cd servers/smart-home-provider-functions
npm install
functions-emulator deploy ha --trigger-http
functions-emulator stop
```

### OAuth2 server

* A fake OAuth2 mock server is provided so you can test Actions on Google easily. **Do not use this one on production**.  
To build and start it, run the following commands:
```bash
cd servers/fake-oauth-server-nodejs
npm install
npm start
```

* At that time, the server will run on `localhost:3000`. Download [ngrok](http://ngrok.com/) to expose it to the Internet with the command
```bash
ngrok http 3000
```

* The fake OAuth2 server is now exposed to the URL printed on the console. It has a format similar to `https://<NGROK_ID>.ngrok.io`. Copy it as you'll need it later

### Actions on Google
* Update the `servers/action.json` file and specify the Google Cloud Functions endpoint to the `url` variable
* Create an Actions on Google project on the [Actions Console](https://console.actions.google.com/) using the Actions SDK
* When told to update the app via the `gactions` command, run the following to use our `action.json` file
```bash
cd servers
gactions update --action_package action.json --project <PROJECT_ID>
```
* Set up account linking  
  * Grant time: Autorization code  
  * Client ID: `RKkWfsi0Z9`  
  * Client secret: `eToBzeBT7OwrPQO8mZHsZtLp1qhQbe`  
  * Authorization URL: `https://<NGROK_ID>.ngrok.io/oauth`  
  * Token URL: `https://<NGROK_ID>.ngrok.io/token`  
* Save all changes and click on the Test button

## Test

* Start the Google Home app on your phone
* Go to the devices settings > Home control > Add device and select the [test] device
* Login (user: `clock`, password: `word`)
* You will now be able to see the fan and the lights devices
* You can now control those devices from the Google assistant

---

The Smart Home section is based on [https://github.com/Nilhcem/smarthome-androidthings](https://github.com/Nilhcem/smarthome-androidthings)
