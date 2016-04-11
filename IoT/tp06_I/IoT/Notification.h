#pragma once
#ifndef _NOTIFICATION_H_
#define _NOTIFICATION_H_
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
* Purpose:     Module to notify the user with a RGB led and a buzzer
*
* Author:      Samuel Mertenat - T2f
* Date:        25.05.2015
*/

// Enumeration of the different available colors
enum Colors {
  RED,
  GREEN,
  BLUE,
  YELLOW,
  WHITE,
  NB_OF_COLOURS
};

// Structure to store the RGB components of a color
typedef struct Color {
  uint8_t red;
  uint8_t green;
  uint8_t blue;
}Color;

#define DURATION  100 // duration of the notification (led switched on / buzzer enabled)
#define INTERVAL  200

class notification {
public:
  /** Constructor
   *  @param p_red_led_pin RGB pins
   *  @param p_green_led_pin
   *  @param p_blue_led_pin
   *  @param p_buzzer_pin	Pin where the buzzer is connected to
   */
  notification(uint8_t p_red_led_pin, uint8_t p_green_led_pin, uint8_t p_blue_led_pin, uint8_t p_buzzer_pin);

  /** Function to push a notification
   *  @param p_color Color (RGB led)
   *  @param p_occurences Number of occurrences
   *  @param p_buzzer_enabled	To force the state of the buzzer (vs m_buzzer_state)
   */
  void notify(Colors p_color, uint8_t p_occurences, bool p_buzzer_enabled);

  /** Function is called on a single click (cf. IoT.ino) and 
   *  toggles the buzzer state (enable / disabled)
   */
  void toggle_buzzer_state();
private:
  // the pins where the LED & the buzzer are connected to
  uint8_t m_red_led_pin;
  uint8_t m_green_led_pin;
  uint8_t m_blue_led_pin;
  uint8_t m_buzzer_pin;

  // variables used to store the duration & interval of a notification
  uint16_t m_duration;
  uint16_t m_interval;

  // array of available colors
  Color m_colors[NB_OF_COLOURS];

  // variable used to store the buzzer state (enabled / disabled)
  bool m_buzzer_enabled;
};
#endif
