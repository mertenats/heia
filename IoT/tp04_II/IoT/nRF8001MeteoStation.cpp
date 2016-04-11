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

#if ARDUINO >= 100
  #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "nRF8001MeteoStation.h"
#include <stdbool.h>
#include <float.h>
#include "logging.h"

#define REQ 10
#define RDY 2  
#define RST 9

static const hal_aci_data_t setup_msgs[NB_SETUP_MESSAGES] PROGMEM = SETUP_MESSAGES_CONTENT;
// for storing the service pipe data created in nRFgo Studio
#ifdef SERVICES_PIPE_TYPE_MAPPING_CONTENT
  static services_pipe_type_mapping_t services_pipe_type_mapping[NUMBER_OF_PIPES] = SERVICES_PIPE_TYPE_MAPPING_CONTENT;
#else
  #define NUMBER_OF_PIPES 0
  static services_pipe_type_mapping_t* services_pipe_type_mapping = NULL;
#endif

nRF8001MeteoStation::nRF8001MeteoStation():
  nRF8001Device (setup_msgs, NB_SETUP_MESSAGES, services_pipe_type_mapping, NUMBER_OF_PIPES, nRF8001Device::CONNECT, REQ, RDY, RST) {
    // initializes the last temperature & humidity to the biggest FLOAT value
    m_last_temp = FLT_MAX;
    m_last_hum = FLT_MAX;
    
    // initializes the memory space taken by the measurement's structures
    memset(&m_temp_measure, 0, sizeof(m_temp_measure));
    memset(&m_hum_measure, 0, sizeof(m_hum_measure));
    
    // sets the ACKs to FALSE; no ACKs are pending
    m_ack_temperature_measure_pending = false;
    m_ack_humidity_measure_pending = false;
}

bool nRF8001MeteoStation::send_temperature(float p_temperature) {
  // if the last sending was acquitted and the temperature to send is different
  // from the earlier temperature, it checks if the pipe is available
  if ((m_ack_temperature_measure_pending == false) && (p_temperature != m_last_temp)) {
    if (lib_aci_is_pipe_available(&aci_state, PIPE_METEO_STATION_TEMPERATURE_MEASUREMENT_TX_ACK)) {
      // sets the structure with the corresponding flags (cf. .h) and the temperature with the given parameter
      m_temp_measure.flags = TEMPERATURE_MEASUREMENT_FLAGS;
      m_temp_measure.measurement[0] = p_temperature;
      m_temp_measure.measurement[1] = 0;
      m_temp_measure.measurement[2] = 0;
      m_temp_measure.measurement[3] = 0;
      
      // sends the temperature structure (Pipe, structure address, size)
      m_ack_temperature_measure_pending = lib_aci_send_data(PIPE_METEO_STATION_TEMPERATURE_MEASUREMENT_TX_ACK, (uint8_t *)&m_temp_measure, 5);
      // if the value was sent correctly, it stores the temperature
      if (m_ack_temperature_measure_pending)
        m_last_temp = p_temperature;
      return m_ack_temperature_measure_pending;
    }
  }
  return false;
}

bool nRF8001MeteoStation::send_humidity(float p_humidity) {
  if ((m_ack_humidity_measure_pending == false) && (p_humidity != m_last_hum)) {
    if (lib_aci_is_pipe_available(&aci_state, PIPE_METEO_STATION_HUMIDITY_MEASUREMENT_TX_ACK)) {
      m_hum_measure.measurement[0] = 0;
      m_hum_measure.measurement[1] = p_humidity;
      
      m_ack_humidity_measure_pending = lib_aci_send_data(PIPE_METEO_STATION_HUMIDITY_MEASUREMENT_TX_ACK, (uint8_t *)&m_hum_measure, PIPE_METEO_STATION_HUMIDITY_MEASUREMENT_TX_ACK_MAX_SIZE);
      if (m_ack_humidity_measure_pending)
        m_last_hum = p_humidity;
      return m_ack_humidity_measure_pending;
    }
  }
  return false;
}

void nRF8001MeteoStation::onACIEvent(aci_evt_t* p_event) {
  switch(p_event->evt_opcode) {
    // if an ACI_EVT_DATA_ACK occurs, it searches the pipe, which has return a ACK
    case ACI_EVT_DATA_ACK:
      if (p_event->params.data_ack.pipe_number == PIPE_METEO_STATION_TEMPERATURE_MEASUREMENT_TX_ACK) {
        // sets to False; a new sending could take place in case of a new temperature (!= m_last_temp)
        m_ack_temperature_measure_pending = false;  
        TraceInfo(F("ACK for temperature measurement received\n"));
      } else if (p_event->params.data_ack.pipe_number == PIPE_METEO_STATION_HUMIDITY_MEASUREMENT_TX_ACK) {
        m_ack_humidity_measure_pending = false;
        TraceInfo(F("ACK for humidity measurement received\n"));
      }
    break;
    // if a paired device is disconnected, it sets the stored values to the biggest FLOAT value
    // and the ACKs to false. It avoids problems if the device want to connect again
    case ACI_EVT_DISCONNECTED:
      m_last_temp = FLT_MAX;
      m_last_hum = FLT_MAX;
      m_ack_temperature_measure_pending = false;
      m_ack_humidity_measure_pending = false;
    break;
  }
}
