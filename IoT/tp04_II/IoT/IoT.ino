/**
 * Copyright 2015 University of Applied Sciences Western Switzerland / Fribourg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project:		HEIA-FR / Internet of Things Laboratory
 *
 * Abstract:            Project - Connected Weather Station
 *
 * Purpose:		Main module to demonstrate and to test the Connected Weather Station
 *
 * Author:		Samuel Mertenat - T2f
 * Date:		08.05.2015
 */
 
#include <SPI.h>
#include <aci_cmds.h>
#include <aci_evts.h>
#include <hal_aci_tl.h>
#include <lib_aci.h>
#include <EEPROM.h>
#include <stdbool.h>
#include <float.h>

#include "nRF8001MeteoStation.h"
#include "Logging.h"

// logging level
// 1: error, 2: error + info, 3: error + info + debug
int g_loggingLevel = 3;

// measuring purpose ... (1: enable)
#ifndef MEASURING
#define MEASURING 1
#endif

#if (MEASURING)
  #include "DHT.h"
  #define DHTPIN 8           // defines the pin we're using
  #define DHTTYPE DHT11      // defines the DHT sensor type (DHT11)
  DHT dht(DHTPIN, DHTTYPE);  // initializes DHT sensor
  static float h = FLT_MAX;  // variable for humidity
  static float t = FLT_MAX;  // variable for temperature
#endif

// creates a instance of nRF8001MeteoStation  
nRF8001MeteoStation nrf;

void setup() {
  Serial.begin(9600);
  
  // sets the device name
  nrf.setDeviceName("SAMeteo");
  
  // starts the nRF8001
  bool nrfIsStarted = nrf.begin(1, 0x0100, 0);
  if (nrfIsStarted == false) {
    TraceError(F("nRF8001 device can't be started"));
  }
  
  #if (MEASURING)
    dht.begin();  // initialization of the DHT11
  #endif
}

void loop() {
  // gets all ACI events at regular intervals
  nrf.pollACI();
  
  #if (MEASURING)
    if (nrf.getState() != ACI_EVT_DISCONNECTED) {
      // reads temperature & humidity from the DHT11
      t = dht.readTemperature();
      h = dht.readHumidity();
      
      // checks if any reads failed
      if (isnan(h) || isnan(t)) {
        TraceError(F("Failed to read from DHT sensor\n"));
      } else {
        // if the values are valid, it tries to send these to the paired device
        nrf.send_temperature(t);
        delay(100);
        nrf.send_humidity(h);
      }
    } 
  #endif
}
