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
 * Abstract: 	7-segments Device Driver
 *
 * Purpose:	This module implements the services for the I2C module
 *          	of the i.MX27 processor.
 *
 * Autĥor:	<Samuel Mertenat>
 * Date:	<22.12.2014>
 */
#include <stdint.h>

/**
 * Method to initialize the 7-segment display
 */
extern void display_fpga_init();

/**
 * Method to display a value [-99 - 99] in decimal on the the 7-segment
 * @param value to display in decimal
 */
extern void display_val(int8_t value);
