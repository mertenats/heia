#pragma once
#ifndef TOUCHSCREEN_Hs
#define TOUCHSCREEN_H
/**
 * Copyright 2014 University of Applied Sciences Western Switzerland / Fribourg
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
 * Project:     HEIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract:    Touch Screen Controller
 *
 * Purpose:     Module to deal with the TSC2101 Controller interfacing
 *              the touchscreen of the APF27 module.
 *
 * Author:      Daniel Gachet
 * Date:        28.06.2014
 */

#include <stdint.h>

/**
 * Finger position on the touch screen
 */
struct touchscreen_position {
        uint16_t x;     // x-axis
        uint16_t y;     // y-axis
        uint16_t z;     // pressure
};


/**
 * Method to initialize the resources of the TSC2101 controller 
 * This method should be called prior any other one.
 */
extern void touchscreen_init();


/**
 * Method to read the position of finger on the touch screen
 * @return finger position
 */
extern struct touchscreen_position touchscreen_read_position();

#endif

