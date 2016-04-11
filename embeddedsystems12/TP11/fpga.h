#pragma once
#ifndef FPGA_H
#define FPGA_H
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
 * Abstract: 	TP11 - FPGA 7-Segment Display Device Driver
 *
 * Purpose:		Module to demonstrate and to test the FPGA 7segments
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		23.04.2015
 */

#include <stdint.h>
#include <stdbool.h>

/* 7-segment: segment definition

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

// The segments
#define SEG_A_FPGA 		0x008
#define SEG_B_FPGA 		0x010
#define SEG_C_FPGA 		0x020
#define SEG_D_FPGA 		0x040
#define SEG_E_FPGA 		0x080
#define SEG_F_FPGA 		0x100
#define SEG_G_FPGA 		0x200
#define SEG_DOT_FPGA 	0x004
#define SEG_MINUS_FPGA	SEG_G_FPGA

// 7-segments structure for the FPGA
static const uint16_t seg_7[] = {
		SEG_A_FPGA + SEG_B_FPGA + SEG_C_FPGA + SEG_D_FPGA + SEG_E_FPGA + SEG_F_FPGA,				// 0
		SEG_B_FPGA + SEG_C_FPGA,																	// 1
		SEG_A_FPGA + SEG_B_FPGA + SEG_E_FPGA + SEG_D_FPGA + SEG_G_FPGA,								// 2
		SEG_A_FPGA + SEG_B_FPGA + SEG_C_FPGA + SEG_D_FPGA + SEG_G_FPGA,								// 3
		SEG_B_FPGA + SEG_C_FPGA + SEG_F_FPGA + SEG_G_FPGA,											// 4
		SEG_A_FPGA + SEG_C_FPGA + SEG_D_FPGA + SEG_F_FPGA + SEG_G_FPGA,								// 5
		SEG_A_FPGA + SEG_C_FPGA + SEG_D_FPGA + SEG_E_FPGA + SEG_F_FPGA + SEG_G_FPGA,				// 6
		SEG_A_FPGA + SEG_B_FPGA + SEG_C_FPGA,														// 7
		SEG_A_FPGA + SEG_B_FPGA + SEG_C_FPGA + SEG_D_FPGA + SEG_E_FPGA + SEG_F_FPGA + SEG_G_FPGA,	// 8
		SEG_A_FPGA + SEG_B_FPGA + SEG_C_FPGA + SEG_D_FPGA + SEG_F_FPGA + SEG_G_FPGA,				// 9
};

#define BUFFER_SIZE 10 // number of laps

// Structure to store the chronometer values
// FPGA: 16 bits register - GPIO: 32 bits masks
struct chrono_value {
	uint32_t timer;						// 1/100 [s]
	bool	 running;					// running / stopped (chrono)
	uint32_t dg1b;
	uint32_t dg1c;
	uint32_t dg2b;
	uint32_t dg2c;
	uint16_t dg3;
	uint16_t dg4;
	uint8_t leds;						// LEDs animation purpose
	uint8_t laps;						// number of laps
	uint8_t laps_buffer_index;			// must be smaller than Buffer Size
	uint32_t laps_buffer[BUFFER_SIZE];	// space to store the laps
};

// Method to initialize the resources of the display module.
extern void fpga_display_init();

// Method to display a decimal value on the FPGA 7segements
extern void fpga_display_value (struct chrono_value *chrono_val);

#endif
