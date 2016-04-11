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
* Project:	HEIA-FR / Internet of Things Laboratory
*
* Abstract:     Project - Connected Weather Station
*
* Purpose:      Module to create a button, which can detect a single-click or a double-click
*
* Source:	This library is a lightweight & simplest version of the OneButton library
*		https://github.com/mathertel/OneButton              
*
* Author:	Samuel Mertenat - T2f
* Date:	        25.05.2015
*/

#include "Button.h"

button::button(uint8_t p_button_pin):
  m_button_pin(p_button_pin),
  m_click_ticks(800),
  m_state(0),
  m_button_level(LOW) {
    // sets the pin as INPUT
    pinMode(m_button_pin, INPUT);
}

void button::attach_fnct_on_click(callbackFunction p_function) {
  m_click_fnct = p_function;
}

void button::attach_fnct_on_double_click(callbackFunction p_function) {
  m_double_click_fnct = p_function;
}

void button::tick() {
  // detects the input level & gets the current time
  m_button_level = digitalRead(m_button_pin);
  m_now = millis();
  
  if (m_state == 0) {
    if (m_button_level == HIGH) {
      m_state = 1; 
      m_start_time = m_now;  // remembers starting time
    }
  } else if (m_state == 1) {
    if (m_button_level == LOW) {
      m_state = 2;
    } 
  } else if (m_state == 2) {
    if (m_now > m_start_time + m_click_ticks) {
      // this was only a single-click
      if (m_click_fnct) m_click_fnct();
      m_state = 0;  // resets state
    } else if (m_button_level == HIGH) {
      m_state = 3;
    } 
  } else if (m_state == 3) {
    if (m_button_level == LOW) {
      // this was a 2 click sequence.
      if (m_double_click_fnct) m_double_click_fnct();
      m_state = 0;  // resets state
    } 
  } 
}
