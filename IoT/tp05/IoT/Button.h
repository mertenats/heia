#pragma once
#ifndef _BUTTON_H_
#define _BUTTON_H_
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

#include "Arduino.h"

extern "C" {
  typedef void (*callbackFunction)(void);
}

class button {
public:
  /** Constructor
   *  @param p_button_pin	Pin where the push-button is connected to
   */
  button(uint8_t p_button_pin);
   
   /** Function to attach a function that will be called on a single-click
    *  @param p_function Callback function
    */
   void attach_fnct_on_click(callbackFunction p_function);

  /** Function to attach a function that will be called on a double-click
   *  @param p_function Callback function
   */
  void attach_fnct_on_double_click(callbackFunction p_function);

  /** Function is called every some milliseconds for handling button events
   */
  void tick(void);
private:
  // variables used to store the pin of the button & the timeout
  uint8_t m_button_pin;
  uint16_t m_click_ticks;
  
  // callback functions (single-click / double-click)
  callbackFunction m_click_fnct;
  callbackFunction m_double_click_fnct;
  
  // variables used to hold information across the upcoming tick calls
  unsigned long m_start_time;
  unsigned long m_now;
  
  uint8_t m_state;
  uint8_t m_button_level;	// LOW / HIGH
};
#endif
