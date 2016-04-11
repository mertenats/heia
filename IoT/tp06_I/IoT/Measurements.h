#pragma once
#ifndef _MEASUREMENTS_H_
#define _MEASUREMENTS_H_
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

// 2048 / 10 x 7 = 1433.6 (70%)
// 1149 - 24 = space used without the N structures
// 1433.6 - 1125 = 260 / 24 = 10 
#define N 10

#define MEASURE_INTERVAL  30*1000//60*60*1000

#include <stdint.h>

// Temperature & humidity measurements structure
// float: 4 bytes -> 8 bytes
typedef struct measure_value {
  float temperature;
  float humidity;
}measure_value;

class measurements {
public:
  /**
   * Constructor 
   */
  measurements(void);

  /**
   * Function to send a new measurement to the historical values
   * @param p_measure The measurement (reference)
   * @param long The timestamps of the measurement
   */
  void new_measurement(measure_value& p_measure, unsigned long p_timestamp);

  /**
   * Function to return the number of measurements
   * @return The number of measurements 
   */
  uint8_t get_nb_measurements(void);

  /**
   * Function to return the last measurement index
   * @return The last measurement index
   */
  uint8_t get_last_measurement_index(void);

  /**
   * Function to return a specific measurement
   * @param p_index The measurement index in the circular buffer
   * @param p_measure The reference to the measurement, where to save the values
   */
  void get_measurement(uint8_t p_index, measure_value& p_measure);

private:
  // structures used to store the historical measurements 
  measure_value m_mea_avg[N];
  measure_value m_mea_min[N];
  measure_value m_mea_max[N];

  // variables used to sum and compute the avg of temperature & humidity
  uint16_t m_nb_of_mea_stored;    // number of measurements used for the avg
  float m_temp_sum;
  float m_hum_sum;

  unsigned long m_end_measure;    // end of the measurement [ms]
  uint16_t m_current_measure;     // index in the circular buffer 
  uint8_t m_nb_of_measurements;   // total number of measurements
};
#endif