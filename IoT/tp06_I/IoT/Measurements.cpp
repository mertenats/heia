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
* Purpose:     Module to store the historical measurements
*              of the weather station
*
* Author:      Samuel Mertenat - T2f
* Date:        13.06.2015
*/

#include <float.h>
#include "Measurements.h"
#include "Arduino.h"
#include "Logging.h"

measurements::measurements():
  m_nb_of_mea_stored(0),
  m_current_measure(0),
  m_temp_sum(0),
  m_hum_sum(0),
  m_nb_of_measurements(0) {
    // initialization of the buffers
    for (uint16_t i = 0; i < N; i++) {
      m_mea_avg[i].temperature = 0;
      m_mea_avg[i].humidity = 0;

      m_mea_min[i].temperature = FLT_MAX;
      m_mea_min[i].humidity = FLT_MAX;

      m_mea_max[i].temperature = FLT_MIN;
      m_mea_max[i].humidity = FLT_MIN;

      // defines the measurement period
      m_end_measure = millis() + MEASURE_INTERVAL; 
    }
}

void measurements::new_measurement(measure_value& p_measure, unsigned long p_timestamp) {
  // if the new measurement is part of the current period, compute the min / max values
  if (p_timestamp <= m_end_measure) {
    if (p_measure.temperature < m_mea_min[m_current_measure].temperature)
      m_mea_min[m_current_measure].temperature = p_measure.temperature;
    if (p_measure.humidity < m_mea_min[m_current_measure].humidity)
      m_mea_min[m_current_measure].humidity = p_measure.humidity;

    if (p_measure.temperature > m_mea_max[m_current_measure].temperature)
      m_mea_max[m_current_measure].temperature = p_measure.temperature;
    if (p_measure.humidity > m_mea_max[m_current_measure].humidity)
      m_mea_max[m_current_measure].humidity = p_measure.humidity;

    m_nb_of_mea_stored++;
    m_temp_sum += p_measure.temperature;
    m_hum_sum += p_measure.humidity;
  } else {
    // computes the avg (measurements sum / number of measurements)
    m_mea_avg[m_current_measure].temperature = m_temp_sum / m_nb_of_mea_stored;
    m_mea_avg[m_current_measure].humidity = m_hum_sum / m_nb_of_mea_stored;

    TraceInfoFormat(F("Temperature:\tmin / max / avg : %u / %u / %u\n"), (uint8_t)m_mea_min[m_current_measure].temperature, (uint8_t)m_mea_max[m_current_measure].temperature, (uint8_t)m_mea_avg[m_current_measure].temperature);
    TraceInfoFormat(F("Humidity:\tmin / max / avg : %u / %u / %u\n"), (uint8_t)m_mea_min[m_current_measure].humidity, (uint8_t)m_mea_max[m_current_measure].humidity, (uint8_t)m_mea_avg[m_current_measure].humidity);

    m_current_measure = (m_current_measure + 1) % N;  // circular buffer, +1 % size
    m_nb_of_measurements++;

    // initializes the current structure (in case of override, m_nb_of_measurements > N)
    m_mea_max[m_current_measure].temperature = FLT_MIN;
    m_mea_max[m_current_measure].humidity = FLT_MIN;
    m_mea_min[m_current_measure].temperature = FLT_MAX;
    m_mea_min[m_current_measure].humidity = FLT_MAX;

    m_end_measure = millis() + MEASURE_INTERVAL;
    // initializes the number of measurements used for the avg
    m_nb_of_mea_stored = 0;   
    m_temp_sum = 0;
    m_hum_sum = 0;

    TraceInfoFormat(F("Number of measure: %u Last measurement index:  %u\n"), get_nb_measurements(), get_last_measurement_index()); 
  }
}

uint8_t measurements::get_nb_measurements(void) {
  if (m_nb_of_measurements > (N - 1))
    return N;
  else
    return m_nb_of_measurements;
}

uint8_t measurements::get_last_measurement_index(void) {
  if (m_nb_of_measurements < N && m_current_measure == 0)
    return 0;
  else if (m_current_measure == 0)
    return N-1;
  return m_current_measure - 1;
}

void measurements::get_measurement(uint8_t p_index, measure_value& p_measure) {
  p_measure.temperature = m_mea_avg[p_index].temperature;
  p_measure.humidity = m_mea_avg[p_index].humidity;
}