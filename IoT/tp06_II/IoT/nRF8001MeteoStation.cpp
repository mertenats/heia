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

#include "Arduino.h"
#include "nRF8001MeteoStation.h"
#include <stdbool.h>
#include <float.h>
#include <limits.h>
#include "logging.h"
#include "Notification.h"

#define REQ_PIN             10
#define RDY_PIN             2  
#define RST_PIN             9

#define RED_LED_PIN         3
#define GREEN_LED_PIN       5
#define BLUE_LED_PIN        6
#define BUZZER_PIN          7

#define THERMOSTAT_TEMP_MIN 18
#define THERMOSTAT_TEMP_MAX 23

notification notif(RED_LED_PIN, GREEN_LED_PIN, BLUE_LED_PIN, BUZZER_PIN);

static const hal_aci_data_t setup_msgs[NB_SETUP_MESSAGES] PROGMEM = SETUP_MESSAGES_CONTENT;
// for storing the service pipe data created in nRFgo Studio
#ifdef SERVICES_PIPE_TYPE_MAPPING_CONTENT
  static services_pipe_type_mapping_t services_pipe_type_mapping[NUMBER_OF_PIPES] = SERVICES_PIPE_TYPE_MAPPING_CONTENT;
#else
  #define NUMBER_OF_PIPES 0
  static services_pipe_type_mapping_t* services_pipe_type_mapping = NULL;
#endif

/** Function to convert a hex value into a uint value
 *  @param p_hex HEX value
 *  @return uint8_t	The unsigned value
 *  http://stackoverflow.com/questions/10156409/convert-hex-string-char-to-int 
 */
uint8_t convert_hex_to_uint(char p_hex) {
  return (uint8_t)strtol(&p_hex, NULL, 16);
}

nRF8001MeteoStation::nRF8001MeteoStation():
  nRF8001Device (setup_msgs, NB_SETUP_MESSAGES, services_pipe_type_mapping, NUMBER_OF_PIPES, nRF8001Device::CONNECT, REQ_PIN, RDY_PIN, RST_PIN),
  m_last_temp(FLT_MAX),
  m_last_hum(FLT_MAX),
  //m_last_pres(INT_MAX),
  m_ack_temperature_measure_pending(false),
  m_ack_humidity_measure_pending(false),
  //m_ack_pressure_measure_pending(false),
  m_thermo_temp_min(THERMOSTAT_TEMP_MIN),
  m_thermo_temp_max(THERMOSTAT_TEMP_MAX),
  m_thermo_temp(THERMO_TEMP_MIN) {
    // initializes the last temperature, humidity & pressure to the biggest FLOAT / INT values
    // sets the ACKs to FALSE; no ACKs are pending
    // initializes the min & max temperatures for the thermostat

    // initializes the memory space taken by the measurement's structures & the RX buffer
    memset(&m_temp_measure, 0, sizeof(m_temp_measure));
    memset(&m_hum_measure, 0, sizeof(m_hum_measure));
    //memset(&m_pres_measure, 0, sizeof(m_pres_measure));
    memset(&m_uart_buffer, 0, sizeof(m_uart_buffer));
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

/*
bool nRF8001MeteoStation::send_pressure(uint32_t p_pressure) {
  if ((m_ack_pressure_measure_pending == false) && (p_pressure != m_last_pres)) {
    if (lib_aci_is_pipe_available(&aci_state, PIPE_METEO_STATION_PRESSURE_MEASUREMENT_TX_ACK)) {
      uint8_t *p_in_val = (uint8_t *)&p_pressure;
      m_pres_measure.measurement[3] = *p_in_val++;
      m_pres_measure.measurement[2] = *p_in_val++;
      m_pres_measure.measurement[1] = *p_in_val++;
      m_pres_measure.measurement[0] = *p_in_val++;
      
      m_ack_pressure_measure_pending = lib_aci_send_data(PIPE_METEO_STATION_PRESSURE_MEASUREMENT_TX_ACK , (uint8_t *)&m_pres_measure, PIPE_METEO_STATION_PRESSURE_MEASUREMENT_TX_ACK_MAX_SIZE);
      if (m_ack_pressure_measure_pending)
        m_last_pres = p_pressure;
      return m_ack_pressure_measure_pending;
    }
  }
  return false;
}
*/

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
      } /*else if (p_event->params.data_ack.pipe_number == PIPE_METEO_STATION_PRESSURE_MEASUREMENT_TX_ACK) {
        m_ack_pressure_measure_pending = false;
        TraceInfo(F("ACK for pressure measurement received\n"));
      }*/
      notif.notify(YELLOW, 1, false);
      break;
    // if a paired device is disconnected, it sets the stored values to the biggest FLOAT value
    // and the ACKs to false. It avoids problems if the device want to connect again
    case ACI_EVT_DISCONNECTED:
      m_last_temp = FLT_MAX;
      m_last_hum = FLT_MAX;
      //m_last_pres = INT_MAX;
      m_ack_temperature_measure_pending = false;
      m_ack_humidity_measure_pending = false;
      //m_ack_pressure_measure_pending = false;
      break;
    // if a device asks for a connection
    case ACI_EVT_CONNECTED:
      notif.notify(WHITE, 1, false);
      break;
    // if the paired device sends data, it retrieves the data into the min or the max temperature (thermostat)
    /*
    case ACI_EVT_DATA_RECEIVED:
      if (p_event->params.data_received.rx_data.pipe_number == PIPE_METEO_STATION_UART_RX_RX) {
        for(uint8_t i = 0; i < p_event->len - 2; i++) {
          m_uart_buffer[i] = convert_hex_to_uint(p_event->params.data_received.rx_data.aci_data[i]);
        }
        if (m_thermo_temp == THERMO_TEMP_MIN) {
          m_thermo_temp_min = m_uart_buffer[0] * 10 + m_uart_buffer[1];
          TraceInfoFormat(F("Thermostat min temperature set to : %u\n"), m_thermo_temp_min);
        } else if (m_thermo_temp == THERMO_TEMP_MAX) {
          m_thermo_temp_max = m_uart_buffer[0] * 10 + m_uart_buffer[1];
          TraceInfoFormat(F("Thermostat max temperature set to : %u\n"), m_thermo_temp_max);
        }
      }
      notif.notify(WHITE, 1, false);
      break;
    */
    case ACI_EVT_DATA_RECEIVED:
      if (PIPE_NORDIC_UART_OVER_BTLE_UART_RX_RX == p_event->params.data_received.rx_data.pipe_number) {
        for(int i = 0; i < p_event->len - 2; i++) {
          m_uart_buffer[i] = p_event->params.data_received.rx_data.aci_data[i];
        }
        TraceInfoFormat(F(" Data(Hex) : %s\n"), m_uart_buffer);
        m_uart_buffer_length = p_event->len - 2;

        if (strcmp((const char*) m_uart_buffer, "send") == 0) {
          if (lib_aci_is_pipe_available(&aci_state, PIPE_NORDIC_UART_OVER_BTLE_UART_TX_TX)) {
            // store the number of measurements to be sent
            m_nb_measurements_to_send = g_measurement.get_nb_measurements();
            m_last_measurement_index = g_measurement.get_last_measurement_index();
            if (! SendHistoricalData()) {
              TraceInfo(F("SendHistoricalData failed\n"));
            }
          }     
        } else {
          if (m_thermo_temp == THERMO_TEMP_MIN) {
            m_thermo_temp_min = convert_hex_to_uint(m_uart_buffer[0]) * 10 + convert_hex_to_uint(m_uart_buffer[1]);
            TraceInfoFormat(F("Thermostat min temperature set to : %u\n"), m_thermo_temp_min);
          } else if (m_thermo_temp == THERMO_TEMP_MAX) {
            m_thermo_temp_max =  convert_hex_to_uint(m_uart_buffer[0]) * 10 +  convert_hex_to_uint(m_uart_buffer[1]);
            TraceInfoFormat(F("Thermostat max temperature set to : %u\n"), m_thermo_temp_max);
          }
        }
      }
    case ACI_EVT_DATA_CREDIT:
      if (m_nb_measurements_to_send > 0)
      //if (m_nb_measurements_to_send <= aci_state.data_credit_available)
        SendHistoricalData();
      //else 
        //TraceInfoFormat(F("Not enough credits to send %u measurements (%u credits)\n"), m_nb_measurements_to_send, aci_state.data_credit_available);
      break;
    default:
    break;
  }
}

void nRF8001MeteoStation::toggle_notification_buzzer_state() {
  notif.notify(WHITE, 1, false);
  notif.toggle_buzzer_state();
}

void nRF8001MeteoStation::toggle_thermostat_temperature() {
  notif.notify(WHITE, 2, true);
  // toggles between the min & the max temperature
  m_thermo_temp = (m_thermo_temp + 1) % NB_OF_TEMP;
}

void nRF8001MeteoStation::push_notification(float p_temperature) {
  if (p_temperature < m_thermo_temp_min) {
    notif.notify(BLUE, 1, true);  // buzzer forced
  } else if (p_temperature > m_thermo_temp_max) {
    notif.notify(RED, 1, true);   // buzzer forced
  } else {
    notif.notify(GREEN, 1, false);
	}
}

bool nRF8001MeteoStation::UartTX(void) {
  if (m_uart_buffer_length == 0) {
    return true;
  }
  bool status = false;
  if (lib_aci_is_pipe_available(&aci_state, PIPE_NORDIC_UART_OVER_BTLE_UART_TX_TX) && (aci_state.data_credit_available >= 1)) {
    status = lib_aci_send_data(PIPE_NORDIC_UART_OVER_BTLE_UART_TX_TX, m_uart_buffer, m_uart_buffer_length);
    if (status) {
      aci_state.data_credit_available--;
      m_uart_buffer_length = 0;
    }
  }
  return status;
}

bool nRF8001MeteoStation::SendHistoricalData(void) {
  if ((m_nb_measurements_to_send > 0) && (m_nb_measurements_to_send == g_measurement.get_nb_measurements())) {
    // no measurement sent yet
    // first send the number of existing measures
    TraceInfoFormat(F("Sending %d measurements\n"), m_nb_measurements_to_send);
    memset(m_uart_buffer, 0, sizeof(m_uart_buffer));
    sprintf((char*) m_uart_buffer, "%d", m_nb_measurements_to_send);
    m_uart_buffer_length = strlen((const char*) m_uart_buffer);
    if (! UartTX()) {
      TraceError(F("Failed to send number of measurements\n"));
      return false;
    }
  }

  uint8_t measurementIndex = 0;
  measure_value measure = {0}; 

  if (m_nb_measurements_to_send < N) {
    while ((m_nb_measurements_to_send > 0) && (aci_state.data_credit_available >= 1)) {
      // send the measurement from the oldest to the newest
      //unsigned int measurementIndex = m_last_measurement_index - m_nb_measurements_to_send;
      measurementIndex = m_last_measurement_index - m_nb_measurements_to_send + 1;

      // get the measure
      //measure_value measure = {0};
      //measure.temperature = 25;
      //measure.humidity = 45;
      g_measurement.get_measurement(measurementIndex, measure);
      //g_measurement.get_measurement(measurementIndex, measure);
      TraceErrorFormat(F("Measure to sent %u, %u\n"), (uint8_t)measure.temperature, (uint8_t)measure.humidity);

      // format the measure
      memset(m_uart_buffer, 0, sizeof(m_uart_buffer));
      sprintf((char*) m_uart_buffer, "#%d: t: %d, h:%d", measurementIndex, int(measure.temperature), int(measure.humidity));
      //sprintf((char*) m_uart_buffer, "#%u: t: %u, h:%u", measurementIndex, (uint8_t)measure.temperature, (uint8_t)measure.humidity);

      m_uart_buffer_length = strlen((const char*) m_uart_buffer);

      if (! UartTX()) {
        TraceErrorFormat(F("Failed to send measurement #%d\n"), measurementIndex);
        return false;
      }
    m_nb_measurements_to_send--;
    }
  } else {
    measurementIndex = m_last_measurement_index;
    while ((m_nb_measurements_to_send > 0) && (aci_state.data_credit_available >= 1)) {
      // send the measurement from the oldest to the newest
      //unsigned int measurementIndex = m_last_measurement_index - m_nb_measurements_to_send;
      measurementIndex = (measurementIndex + 1) % N;

      // get the measure
      //measure_value measure = {0};
      //measure.temperature = 25;
      //measure.humidity = 45;
      g_measurement.get_measurement(measurementIndex, measure);
      //g_measurement.get_measurement(measurementIndex, measure);
      TraceErrorFormat(F("Measure to sent %u, %u\n"), (uint8_t)measure.temperature, (uint8_t)measure.humidity);

      // format the measure
      memset(m_uart_buffer, 0, sizeof(m_uart_buffer));
      sprintf((char*) m_uart_buffer, "#%d: t: %d, h:%d", measurementIndex, int(measure.temperature), int(measure.humidity));
      //sprintf((char*) m_uart_buffer, "#%u: t: %u, h:%u", measurementIndex, (uint8_t)measure.temperature, (uint8_t)measure.humidity);

      m_uart_buffer_length = strlen((const char*) m_uart_buffer);

      if (! UartTX()) {
        TraceErrorFormat(F("Failed to send measurement #%d\n"), measurementIndex);
        return false;
      }
    m_nb_measurements_to_send--;
    }

  }

  /*while ((m_nb_measurements_to_send > 0) && (aci_state.data_credit_available >= 1)) {
    // send the measurement from the oldest to the newest
    //unsigned int measurementIndex = m_last_measurement_index - m_nb_measurements_to_send;
    uint8_t measurementIndex = 0;
    //if (m_nb_measurements_to_send == N) {
    if (m_last_measurement_index - m_nb_measurements_to_send < -1) {
      measurementIndex = (m_last_measurement_index + 1) % N;
    } else {
      measurementIndex = m_last_measurement_index + 1 - m_nb_measurements_to_send;
    }

    // get the measure
    //measure_value measure = {0};
    measure_value measure = {0}; 
    //measure.temperature = 25;
    //measure.humidity = 45;
    g_measurement.get_measurement(measurementIndex, measure);
    //g_measurement.get_measurement(measurementIndex, measure);
    TraceErrorFormat(F("Measure to sent %u, %u\n"), (uint8_t)measure.temperature, (uint8_t)measure.humidity);

    // format the measure
    memset(m_uart_buffer, 0, sizeof(m_uart_buffer));
    sprintf((char*) m_uart_buffer, "#%d: t: %d, h:%d", measurementIndex, int(measure.temperature), int(measure.humidity));
    //sprintf((char*) m_uart_buffer, "#%u: t: %u, h:%u", measurementIndex, (uint8_t)measure.temperature, (uint8_t)measure.humidity);

    m_uart_buffer_length = strlen((const char*) m_uart_buffer);

    if (! UartTX()) {
      TraceErrorFormat(F("Failed to send measurement #%d\n"), measurementIndex);
      return false;
    }
    m_nb_measurements_to_send--;
  }
  */

  if (m_nb_measurements_to_send == 0) {
    TraceInfo(F("SendHistoricalData done\n"));
  }
  return true;
}

