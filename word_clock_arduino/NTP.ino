unsigned int localPort = 2390;      // local port to listen for UDP packets

IPAddress timeServerIP; 
const char* ntpServerName = "time.nist.gov";
const int NTP_PACKET_SIZE = 48; // NTP time stamp is in the first 48 bytes of the message
byte packetBuffer[ NTP_PACKET_SIZE]; //buffer to hold incoming and outgoing packets
boolean doNTP=false;

// A UDP instance to let us send and receive packets over UDP
WiFiUDP udp;

RTC_Millis RTC;                           // RTC (soft)
DateTime now;                             // current time

const unsigned long NTP_DELAY = 3600000;  // Update time using NTP once every hour
unsigned long lastNTPUpdate = 0;

#define min(a,b) ((a)<(b)?(a):(b))        // recreate the min function
#define sp    Serial.print
#define spf   Serial.printf
#define spln  Serial.println

void setupNTP() {
    RTC.begin(DateTime(F(__DATE__), F(__TIME__)));    // initially set to compile date & time
    udp.begin(localPort);
}

void loopNTP() {
    getTime();
    if (millis() > lastNTPUpdate + NTP_DELAY) {
      doNTP = true;
    }

    if (cm != om) {                              
        updateDisplay = true;
        om = cm;
    }
    if (doNTP) {
        updateTimeFromNTP();
    }
}

void updateTimeFromNTP() {
  //get a random server from the pool
  WiFi.hostByName(ntpServerName, timeServerIP); 
  sendNTPpacket(timeServerIP);            // send an NTP packet to a time server
  delay(1000);                          // wait to see if a reply is available

  int cb = udp.parsePacket();           // get packet (if available)
  if (!cb) {
    spln(F("... no packet yet"));
  } else {
    sp(F("... NTP packet received with ")); sp(cb); spln(F(" bytes"));     // We've received a packet, read the data from it
    udp.read(packetBuffer, NTP_PACKET_SIZE);                            // read the packet into the buffer

    unsigned long highWord = word(packetBuffer[40], packetBuffer[41]);  // timestamp starts at byte 40 of packet. It is 2 words (4 bytes) long
    unsigned long lowWord = word(packetBuffer[42], packetBuffer[43]);   // Extract each word and...
    unsigned long secsSince1900 = highWord << 16 | lowWord;             // ... combine into long: NTP time (seconds since Jan 1 1900):

    const unsigned long seventyYears = 2208988800UL;                    // Unix time starts on Jan 1 1970. In seconds, that's 2208988800:
    unsigned long epoch = secsSince1900 - seventyYears;                 // subtract seventy years to get to 1 Jan. 1900:

    int tz = -1;                                            // adjust for EST time zone
    DateTime gt(epoch - (tz*60*60));                       // obtain date & time based on NTP-derived epoch...
    tz = IsDST(gt.month(), gt.day(), gt.dayOfTheWeek())?-2:-1;  // if in DST correct for GMT-4 hours else GMT-5
    DateTime ntime(epoch - (tz*60*60));                    // if in DST correct for GMT-4 hours else GMT-5
    RTC.adjust(ntime);    // and set RTC to correct local time   
    doNTP = false;
    lastNTPUpdate = millis();
    
    // ---------------- What follows is just for debug ----------------
    nyr = ntime.year()-2000;                       
    nmo = ntime.month();
    ndy = ntime.day();    
    nh  = ntime.hour(); if(nh==0) nh=24;                   // adjust to 1-24            
    nm  = ntime.minute();                     
    ns  = ntime.second();                     

    sp(F("... NTP packet local time: [GMT - ")); sp(tz); sp(F("]: "));       // Local time at Greenwich Meridian (GMT) - offset  
    if(nh < 10) sp(F(" ")); sp(nh);  sp(F(":"));          // print the hour 
    if(nm < 10) sp(F("0")); sp(nm);  sp(F(":"));          // print the minute
    if(ns < 10) sp(F("0")); sp(ns);                       // print the second

    sp(F(" on "));                                        // Local date
    if(nyr < 10) sp(F("0")); sp(nyr);  sp(F("/"));        // print the year 
    if(nmo < 10) sp(F("0")); sp(nmo);  sp(F("/"));        // print the month
    if(ndy < 10) sp(F("0")); spln(ndy);                   // print the day
    spln();
  }
}

// send an NTP request to the time server at the given address
unsigned long sendNTPpacket(IPAddress& address) {
  Serial.println("sending NTP packet...");
  // set all bytes in the buffer to 0
  memset(packetBuffer, 0, NTP_PACKET_SIZE);
  // Initialize values needed to form NTP request
  // (see URL above for details on the packets)
  packetBuffer[0] = 0b11100011;   // LI, Version, Mode
  packetBuffer[1] = 0;     // Stratum, or type of clock
  packetBuffer[2] = 6;     // Polling Interval
  packetBuffer[3] = 0xEC;  // Peer Clock Precision
  // 8 bytes of zero for Root Delay & Root Dispersion
  packetBuffer[12]  = 49;
  packetBuffer[13]  = 0x4E;
  packetBuffer[14]  = 49;
  packetBuffer[15]  = 52;

  // all NTP fields have been given values, now
  // you can send a packet requesting a timestamp:
  udp.beginPacket(address, 123); //NTP requests are to port 123
  udp.write(packetBuffer, NTP_PACKET_SIZE);
  udp.endPacket();
}

// getTime(): get current time from RTC (soft or hard)
void getTime() { 
  now = RTC.now();  
  ch = min(24,now.hour()); if(ch == 0) ch=24; // hours 1-24
  cm = min(59,now.minute()); 
  cs = min(59,now.second());
  cdy= min(31,now.day()); 
  cmo= min(now.month(),12); 
  cyr= min(99,now.year()-2000); 
  cdw =now.dayOfTheWeek();
}

// IsDST(): returns true if during DST, false otherwise
boolean IsDST(int mo, int dy, int dw) {
  if (mo < 3 || mo > 11) { return false; }                // January, February, and December are out.
  if (mo > 3 && mo < 11) { return true;  }                // April to October are in
  int previousSunday = dy - dw;                               
  if (mo == 3) { return previousSunday >= 8; }            // In March, we are DST if our previous Sunday was on or after the 8th.
  return previousSunday <= 0;                             // In November we must be before the first Sunday to be DST. That means the previous Sunday must be before the 1st.
}

