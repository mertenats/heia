#pragma once
#ifndef _NRF8001_METEO_STATION_H_
#define _NRF8001_METEO_STATION_H_
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
 * Purpose:		Derived class of nRF8001Device. This class offers the possiblities to send
 *                      temperature & humidity and to retrieve ACI events
 *
 * Author:		Samuel Mertenat - T2f
 * Date:		08.05.2015
 */
 
#include "nRF8001Device.h"
#include "services.h"

// Temperature mesurement Flags
// Documentation: https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.temperature_measurement.xml
// Flags: 0: Celsius, 0: Time Stamp not present, 0:Temperature Type not present, 3-7: reserved
#define TEMPERATURE_MEASUREMENT_FLAGS  0b00000000

// Temperature measurement structure (5 bytes)
typedef struct temperature_measure {
  uint8_t flags;
  uint8_t measurement[4];
}temperature_measure;

// Humidity measurement structure (2 bytes)
// Documentation: https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.humidity.xml
typedef struct humidity_measure {
  uint8_t measurement[2];
}humidity_measure;

// Derived class of nRF8001Device
// This class specifies the needed functions & attributs used for our Wheater Station
class nRF8001MeteoStation:public nRF8001Device {
  public:
    // Constructor
    nRF8001MeteoStation();
    
    /** Function to send a temperature measurement
     *  @param p_temperature Measured temperature to send
     *  @return bool Returns TRUE if the temperature has been sent correctly 
     */
    bool send_temperature(float p_temperature);
    
    /** Function to send a humidity measurement
     *  @param p_humidity Measured humidity to send
     *  @return bool Returns TRUE if the humidity has been sent correctly 
     */
    bool send_humidity(float p_humidity);
    
    /** Function is called on defined ACI events (cf. nRF8001Device.cpp)
     *  @param aci_evt_t* Pointer to the aci_data event structure
     */
    virtual void onACIEvent(aci_evt_t* p_event);
    
  private:
    // variables used to store the last temperature & humidity measurements
    float m_last_temp;
    float m_last_hum;
    
    // structures used to send temperature & humidity measurements
    temperature_measure m_temp_measure;
    humidity_measure m_hum_measure;
    
    // variables used to store if an ACK is pending for temperature / humidity
    bool m_ack_temperature_measure_pending;
    bool m_ack_humidity_measure_pending;
};
#endif
