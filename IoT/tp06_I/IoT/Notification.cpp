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
* 
*/

#include "Arduino.h"
#include "Logging.h"
#include "Notification.h"

notification::notification(uint8_t p_red_led_pin, uint8_t p_green_led_pin, uint8_t p_blue_led_pin, uint8_t p_buzzer_pin):
  m_red_led_pin(p_red_led_pin), 
  m_green_led_pin(p_green_led_pin),
  m_blue_led_pin(p_blue_led_pin),
  m_buzzer_pin(p_buzzer_pin),
  m_duration(DURATION),
  m_interval(INTERVAL),
  m_buzzer_enabled(false) {
    // initializes the memory space taken by the colors' array
    memset(&m_colors, 0, sizeof(m_colors));

    // sets the pins as OUTPUT
    pinMode(m_red_led_pin, OUTPUT);
    pinMode(m_green_led_pin, OUTPUT);
    pinMode(m_blue_led_pin, OUTPUT);
    pinMode(m_buzzer_pin, OUTPUT);

    // defines the colors
    m_colors[RED].red = m_colors[GREEN].green = m_colors[BLUE].blue = m_colors[YELLOW].red = m_colors[YELLOW].green = m_colors[WHITE].red = m_colors[WHITE].green = m_colors[WHITE].blue = 255;
    m_colors[GREEN].red = m_colors[BLUE].green = 128;
    m_colors[RED].green = m_colors[RED].blue = m_colors[GREEN].blue = m_colors[BLUE].red = m_colors[YELLOW].blue = 0;
}

void notification::notify(Colors p_color, uint8_t p_occurences, bool p_buzzer_enabled) {
  for (uint8_t i = 0; i < p_occurences; i++) {
    // switches on the LED
    analogWrite(m_red_led_pin, m_colors[p_color].red);
    analogWrite(m_green_led_pin, m_colors[p_color].green);
    analogWrite(m_blue_led_pin, m_colors[p_color].blue);

    delay(m_interval);

    // switches off the LED
    digitalWrite(m_red_led_pin, LOW);
    digitalWrite(m_green_led_pin, LOW);
    digitalWrite(m_blue_led_pin, LOW);

    if (p_buzzer_enabled || m_buzzer_enabled)
      tone(m_buzzer_pin, 4978, m_duration);
  }
}

void notification::toggle_buzzer_state() {
  m_buzzer_enabled = !m_buzzer_enabled;	// toggles state
  if (m_buzzer_enabled)
    TraceInfo(F("Notification: buzzer enabled\n"));
}

