#include "Logging.h"

#include <stdio.h>

void PrintHeader(const char* szHeaderType) {
  // returns the number of ms since the Arduino began running
  unsigned long time = millis();
  Serial.print(szHeaderType);
  Serial.print("\tat time : ");
  Serial.print(time / 3600000);  // hours
  time = time % 3600000;
  Serial.print("h ");
  Serial.print(time / 60000);  //  minutes
  time = time % 60000;
  Serial.print("m ");
  Serial.print((time / 1000) % 60);  // seconds
  Serial.print("s : \t");
}

void PrintSerial(const char* format, int arg1) {
  Serial.print(format);
  Serial.println(arg1);
}

