#pragma once
#ifndef DISPLAY_H
#define DISPLAY_H
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
 * Project:	EIA-FR / Embedded Systems 1 Laboratory
 *
 * Abstract: 	APF27 FPGA 7-Segment Display Device Driver
 *
 * Purpose:	This module implements a method to display an hex-digit on
 *   		7-segments connected the FPGA ports of the APF27
 *
 * Autĥor:	Daniel Gachet
 * Date:	28.06.2014
 */

#include <stdint.h>

/** 
 * Method to initialize the resources of the display module.
 */
extern void display_init();

/**
 * Method to display a decimal value on the FPGA 7-segement. 
 * 
 * @param value: value to display
 *               for negative value, a dot will be diplayed on the left digit
 *               for abs(value) bigger than 99, two minus sign ('-') will be 
 *               displayed
 */
extern void display_value (int32_t value);


#endif

