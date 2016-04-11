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

#include <stdbool.h>

#include "display.h"


/**
 * 7-segment display and LED interface
 */
struct fpga_regs {
	uint16_t reserved1[4];
	uint16_t seg7_rw;
	uint16_t seg7_ctrl;
	uint16_t seg7_id;
	uint16_t reserved2[1];
	uint16_t leds_rw;
	uint16_t leds_ctrl;
	uint16_t leds_id;
};
static volatile struct fpga_regs* fpga = (struct fpga_regs*)0xd6000000;
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

#define SEG_A 		0x008
#define SEG_B 		0x010
#define SEG_C 		0x020
#define SEG_D 		0x040
#define SEG_E 		0x080
#define SEG_F 		0x100
#define SEG_G 		0x200
#define SEG_DOT 	0x004
#define SEG_MINUS	SEG_G
static const uint16_t seg_7[] =
	{
	   SEG_A + SEG_B + SEG_C + SEG_D + SEG_E + SEG_F	,	// 0
		   SEG_B + SEG_C        			,	// 1
  	   SEG_A + SEG_B                 + SEG_E + SEG_D + SEG_G,	// 2
    	   SEG_A + SEG_B + SEG_C + SEG_D                 + SEG_G,	// 3
    	           SEG_B + SEG_C                 + SEG_F + SEG_G,	// 4
    	   SEG_A         + SEG_C + SEG_D         + SEG_F + SEG_G,	// 5
    	   SEG_A         + SEG_C + SEG_D + SEG_E + SEG_F + SEG_G,	// 6
    	   SEG_A + SEG_B + SEG_C				,	// 7
    	   SEG_A + SEG_B + SEG_C + SEG_D + SEG_E + SEG_F + SEG_G,	// 8
    	   SEG_A + SEG_B + SEG_C + SEG_D + SEG_F + SEG_G	,	// 9
    	   SEG_A + SEG_B + SEG_C + SEG_E + SEG_F + SEG_G	,	// A
     	                   SEG_C + SEG_D + SEG_E + SEG_F + SEG_G,	// b
     	   SEG_A                 + SEG_D + SEG_E + SEG_F        ,	// C
     	           SEG_B + SEG_C + SEG_D + SEG_E +         SEG_G,	// d
     	   SEG_A                 + SEG_D + SEG_E + SEG_F + SEG_G,	// E
     	   SEG_A                         + SEG_E + SEG_F + SEG_G,	// F
	};

/**
 * Method to initialize the 7-segment display
 */
void display_init()
{
	static bool is_initialized=false;
	if (!is_initialized) {
		fpga->leds_rw = 0;
		fpga->leds_ctrl = 0xff;
		fpga->seg7_rw = 0;
		fpga->seg7_ctrl = 0x3ff;
		is_initialized = true;
	}
}

/**
 * Method to display a decimal number on the 7-segment
 */
void display_value (int32_t value)
{
	display_init();
	uint16_t dot = 0;

	if (value < 0) {
		value = -value;
		dot = SEG_DOT;
	}

	uint16_t units = seg_7[value % 10];
	uint16_t tens  = seg_7[value / 10];
	if (value > 99) {
		units = SEG_MINUS;
		tens  = SEG_MINUS;
	}

	for (int i=0; i<10; i++) fpga->seg7_rw = units + 0x1;
	fpga->seg7_rw = 0x1;

	for (int i=0; i<10; i++) fpga->seg7_rw = tens + 0x2 + dot;
	fpga->seg7_rw = 0x2;
}

