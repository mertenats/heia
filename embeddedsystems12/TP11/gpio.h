#pragma once
#ifndef GPIO_H
#define GPIO_H
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
 * Project:		EIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract: 	TP11 - GPIO extension (imx27_gpio.c/h)
 *
 * Purpose:		Module to demonstrate and to test the GPIO 7segments
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		23.04.2015
 */

#include "fpga.h"

/* Segment definition

           +-- seg A --+
           |           |
         seg F       seg B
           |           |
           +-- seg G --+
           |           |
         seg E       seg C
           |           |
           +-- seg D --+
 */

// The segments
#define SEG_A_GPIO 		0x40000000 // 0b1000000000000000000000000000000
#define SEG_B_GPIO 		0x80000000 // 0b10000000000000000000000000000000
#define SEG_C_GPIO 		0x400 // 0b10000000000
#define SEG_D_GPIO 		0x800 // 0b100000000000
#define SEG_E_GPIO 		0x1000 // 0b1000000000000
#define SEG_F_GPIO 		0x2000 // 0b10000000000000
#define SEG_G_GPIO 		0x4000 // 0b100000000000000
#define SEG_MINUS_GPIO	SEG_G_GPIO
#define DIG1_GPIO		0x400 // 0b10000000000
#define DIG2_GPIO		0x800 // 0b100000000000

// 7-segments structure for the GPIO ports PCxx (segments A-B)
static const uint32_t seg_7_C[] = {
		SEG_A_GPIO + SEG_B_GPIO,	// 0
		SEG_B_GPIO,					// 1
		SEG_A_GPIO + SEG_B_GPIO,	// 2
		SEG_A_GPIO + SEG_B_GPIO,	// 3
		SEG_B_GPIO,					// 4
		SEG_A_GPIO,					// 5
		SEG_A_GPIO,					// 6
		SEG_A_GPIO + SEG_B_GPIO,	// 7
		SEG_A_GPIO + SEG_B_GPIO,	// 8
		SEG_A_GPIO + SEG_B_GPIO,	// 9
};

// 7-segments structure for the GPIO ports PBxx (segments C-G)
static const uint32_t seg_7_B[] = {
		SEG_C_GPIO + SEG_D_GPIO + SEG_E_GPIO + SEG_F_GPIO,				// 0
		SEG_C_GPIO,														// 1
		SEG_E_GPIO + SEG_D_GPIO + SEG_G_GPIO,							// 2
		SEG_C_GPIO + SEG_D_GPIO + SEG_G_GPIO,							// 3
		SEG_C_GPIO + SEG_F_GPIO + SEG_G_GPIO,							// 4
		SEG_C_GPIO + SEG_D_GPIO + SEG_F_GPIO + SEG_G_GPIO,				// 5
		SEG_C_GPIO + SEG_D_GPIO + SEG_E_GPIO + SEG_F_GPIO + SEG_G_GPIO,	// 6
		SEG_C_GPIO,														// 7
		SEG_C_GPIO + SEG_D_GPIO + SEG_E_GPIO + SEG_F_GPIO + SEG_G_GPIO,	// 8
		SEG_C_GPIO + SEG_D_GPIO + SEG_F_GPIO + SEG_G_GPIO,				// 9
};

// Method to display a number on the GPIO 7-segment
extern void gpio_display_value(struct chrono_value *chrono_val);

// Method to animate the LEDs
void gpio_animate_leds(struct chrono_value *chrono_val);

#endif
