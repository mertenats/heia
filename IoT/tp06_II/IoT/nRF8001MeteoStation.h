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
* Project:     HEIA-FR / Internet of Things Laboratory
*
* Abstract:    Project - Connected Weather Station
*
* Purpose:     Derived class of nRF8001Device. This class offers the possibilities to send
*              temperature & humidity and to retrieve ACI events
*
* Author:      Samuel Mertenat - T2f
* Date:        25.05.2015
*/

#include "nRF8001Device.h"
#include "services.h"
#include "Measurements.h"

extern measurements g_measurement;

// Temperature measurement Flags
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

// Pressure measurement structure (4 bytes)
// Documentation: https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.pressure.xml
typedef struct pressure_measure {
  uint8_t measurement[4];
}pressure_measure;

// Enumeration for the two settings of the thermostat
enum thermostat {
  THERMO_TEMP_MIN,
  THERMO_TEMP_MAX,
  NB_OF_TEMP
};

// Derived class of nRF8001Device
// This class specifies the needed functions & attributes used for our Weather Station
class nRF8001MeteoStation:public nRF8001Device {
public:
  // Constructor
  nRF8001MeteoStation(void);

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

  /** Function to send a pressure measurement
   *  @param p_pressure Measured pressure to send
   *  @return bool Returns TRUE if the pressure has been sent correctly 
   */
  bool send_pressure(uint32_t p_pressure);

  /** Function is called on defined ACI events (cf. nRF8001Device.cpp)
   *  @param aci_evt_t* Pointer to the aci_data event structure
   */
  virtual void onACIEvent(aci_evt_t* p_event);

  /** Function is called on a single click (cf. IoT.ino) and 
   *  toggles the buzzer state (enable / disabled)
   */
  void toggle_notification_buzzer_state(void);

  /** Function is called on a double-click (cf. IoT.ino) and 
   *  toggles between the min & the max temperature (thermostat)
   */
  void toggle_thermostat_temperature(void);

  /** Function is called by the timer (cf. IoT.ino) every 5 minutes 
   *  and pushes a notification to the user (about the temperature; thermostat)
   */
  void push_notification(float p_temperature);
private:
  /**
   * @brief [brief description]
   * @details [long description]
   * @return [description]
   */
  bool UartTX(void);

  /**
   * @brief [brief description]
   * @details [long description]
   * @return [description]
   */
  bool SendHistoricalData(void);

  // variables used to store the last temperature, humidity & pressure measurements
  float m_last_temp;
  float m_last_hum;

  // structures used to send temperature, humidity & pressure measurements
  temperature_measure m_temp_measure;
  humidity_measure m_hum_measure;

  // variables used to store if an ACK is pending for temperature / humidity / pressure
  bool m_ack_temperature_measure_pending;
  bool m_ack_humidity_measure_pending;

  // variable used to store the received bytes (2)
  //uint8_t m_uart_buffer[2/*PIPE_METEO_STATION_THERMOSTAT_TEMPERATUR_RX_MAX_SIZE*/];
  uint8_t m_uart_buffer[PIPE_NORDIC_UART_OVER_BTLE_UART_RX_RX_MAX_SIZE];
  uint8_t m_uart_buffer_length;

  uint8_t m_nb_measurements_to_send;
  uint8_t m_last_measurement_index;

  // variables used for the thermostat (min, max, setting state)
  uint8_t m_thermo_temp_min;
  uint8_t m_thermo_temp_max;
  uint8_t m_thermo_temp;
};
#endif
