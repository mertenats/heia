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
 * Project:     HEIA-FR / Internet of Things Laboratory
 *
 * Abstract:    Project - Connected Weather Station
 *
 * Purpose:     Main module to demonstrate and to test the Connected Weather Station
 *
 * Author:      Samuel Mertenat - T2f
 * Date:        25.05.2015
 *
 * Components:	1x DHT11:        https://github.com/adafruit/DHT-sensor-library
 *		1x nRF8001:      https://github.com/NordicSemiconductor/ble-sdk-arduino
 * 		1x BMP085:       https://github.com/adafruit/Adafruit-BMP085-Library
 *		1x push-button
 *		1x RGB Led
 *		1x buzzer
 *
 * Others:      Timer:		https://github.com/JChristensen/Timer
 * 
 * Connections:	https://www.dropbox.com/s/riqyxkyqw80j53w/Sch%C3%A9ma_des_connexions.png?dl=0
 *
 * Use:		One-click:      enables or disables the buzzer
 *		Double-click:	switches between the min or the max temperature (thermostat), which
 *				can be set with the Smartphone (TX characteristic)
 *	        Connection: 	permits to retrieve temperature, humidity or pressure measurement
 */
 
#include <SPI.h>
#include <aci_cmds.h>
#include <aci_evts.h>
#include <hal_aci_tl.h>
#include <lib_aci.h>
#include <EEPROM.h>
#include <stdbool.h>
#include <float.h>
#include <limits.h>
#include "Timer.h"
#include "nRF8001MeteoStation.h"
#include "Logging.h"
#include "Button.h"

// logging level
// 1: error, 2: error + info, 3: error + info + debug
int g_loggingLevel = 3;

// measuring purpose ... (1: enable)
#define MEASURING               1
#define BUTTON_PIN              4
#define DHTPIN                  8       
#define NOTIFICATION_INTERVAL   1000*60*5	// interval of 5 mins between every notification

#if (MEASURING)
  #include "DHT.h"
  #define DHTTYPE DHT11      	// defines the DHT sensor type (DHT11)
  DHT dht(DHTPIN, DHTTYPE);  	// initializes DHT sensor
  static float h = FLT_MAX;  	// variable for humidity
  static float t = FLT_MAX;  	// variable for temperature
  
  #include <Wire.h>
  #include <Adafruit_BMP085.h>
  Adafruit_BMP085 bmp;		// initializes BMP085 sensor
  static uint32_t p = INT_MAX;	// variable for pressure
#endif

nRF8001MeteoStation nrf;      // creates an instance of nRF8001MeteoStation  
button b(BUTTON_PIN);		// creates an instance of button
Timer timer;			// creates an instance of timer

void setup() {
  Serial.begin(9600);
  
  // sets the device name
  nrf.setDeviceName("SAMeteo");
  
  // starts the nRF8001
  if (!nrf.begin(1, 0x0100, 0)) {
    TraceError(F("nRF8001 device can't be started"));
  }
  
  #if (MEASURING)
    dht.begin();  // initialization of the DHT11
    if (!bmp.begin()) {
      TraceError(F("BMP085 sensor can't be started"));
    }
  #endif

  // attaches the functions called on one or double-click
  b.attach_fnct_on_click(one_click);
  b.attach_fnct_on_double_click(double_click);

  // attaches the function to the timer and sets the interval
  timer.every(NOTIFICATION_INTERVAL, notification);
}

void loop() {
  // updates the button & the timer
  b.tick();
  timer.update();

  // gets all ACI events at regular intervals
  nrf.pollACI();
  
  #if (MEASURING)
    if (nrf.getState() != ACI_EVT_DISCONNECTED) {
      // reads temperature, humidity & pressure from the sensors
      t = dht.readTemperature();
      h = dht.readHumidity();
      p = bmp.readPressure();
      
      // checks if any reads failed
      if (isnan(h) || isnan(t)) {
        TraceError(F("Failed to read from DHT sensor\n"));
      } else if (isnan(p)) {
        TraceError(F("Failed to read from BMP085 sensor\n"));
      } else {
        // if the values are valid, it tries to send these to the paired device
        nrf.send_temperature(t);
        nrf.send_humidity(h);
        nrf.send_pressure(p);
      }
    } 
  #endif
}

// function called on one-click
void one_click() {
  nrf.toggle_notification_buzzer_state();
  TraceInfo(F("Button: One-click\n"));
}

// function called on double-click
void double_click() {
  nrf.toggle_thermostat_temperature();
  TraceInfo(F("Button: Double-click\n"));
}

// function called by the timer, every 5 minutes
void notification() {
  nrf.push_notification(t);
}
