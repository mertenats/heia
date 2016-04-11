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

#include <imx27_gpio.h>
#include "gpio.h"
#include "chrono.h"

// The LEDs
#define GD0		0x10000000 // 0b10000000000000000000000000000
#define GD1		0x20000000 // 0b100000000000000000000000000000
#define GD2		0x40000 // 0b1000000000000000000
#define GD3		0x80000 // 0b10000000000000000000
#define GD4		0x100000 // 0b100000000000000000000
#define GD5		0x200000 // 0b1000000000000000000000
#define GD6		0x8000 // 0b1000000000000000
#define GD7		0x10000 // 0b10000000000000000

// LEDs animation structure for the GPIO ports PCxx (GD0-1)
static const uint32_t leds_animation_C[] = {
		GD0,
		GD0 + GD1,
		GD0 + GD1,
		GD0 + GD1,
		GD0 + GD1,
		GD0 + GD1,
		GD0 + GD1,
		GD0 + GD1,	// all the leds are turn on
			  GD1,
			    	 0,
						 0,
							 0,
								 0,
									 0,
										 0,
											 0,
};

// LEDs animation structure for the GPIO ports PBxx (GD2-7)
static const uint32_t leds_animation_B[] = {
		  0,
		        0,
		            GD2,
					GD2 + GD3,
					GD2 + GD3 + GD4,
					GD2 + GD3 + GD4 + GD5,
					GD2 + GD3 + GD4 + GD5 + GD6,
					GD2 + GD3 + GD4 + GD5 + GD6 + GD7,	// all the leds are turn on
					GD2 + GD3 + GD4 + GD5 + GD6 + GD7,
			  	    GD2 + GD3 + GD4 + GD5 + GD6 + GD7,
			  	          GD3 + GD4 + GD5 + GD6 + GD7,
						  	  	GD4 + GD5 + GD6 + GD7,
			  	          	    	  GD5 + GD6 + GD7,
					  	          	        GD6 + GD7,
					  	          	        	  GD7,
												    0,
};

// Method to initialize the GPIO 7-segment display
void gpio_display_init() {
	// Initialization of segments C - G
	imx27_gpio_configure(IMX27_GPIO_PORT_B, 0x27C00 /*0b100111110000000000*/, IMX27_GPIO_OUTPUT);
	imx27_gpio_setbits (IMX27_GPIO_PORT_B, 0);

	// Initialization of segments A - B
	imx27_gpio_configure(IMX27_GPIO_PORT_C, 0xC0000000 /*0b11000000000000000000000000000000*/, IMX27_GPIO_OUTPUT);
	imx27_gpio_setbits (IMX27_GPIO_PORT_C, 0);

	// Initialization of the 'digit selector'
	imx27_gpio_configure(IMX27_GPIO_PORT_E, 0xC00 /*0b110000000000*/, IMX27_GPIO_OUTPUT);
	imx27_gpio_setbits (IMX27_GPIO_PORT_E, 0);
}

// Method to initialize the GPIO LEDs
void gpio_leds_init() {
	// Initialization of the LEDs as OUTPUT
	imx27_gpio_configure(IMX27_GPIO_PORT_B, 0x3D8000 /*0b1111011000000000000000*/, IMX27_GPIO_OUTPUT);
	imx27_gpio_setbits (IMX27_GPIO_PORT_B, 0);
	imx27_gpio_configure(IMX27_GPIO_PORT_C, 0x30000000 /*0b110000000000000000000000000000*/, IMX27_GPIO_OUTPUT);
	imx27_gpio_setbits (IMX27_GPIO_PORT_C, 0);
}

// Method to display a number on the GPIO 7-segment
void gpio_display_value(struct chrono_value *chrono_val) {
	// will contain 1 or 0, depending on the digit to display
	static uint8_t index = 0;
	gpio_display_init();

	if (index == 0) {
		imx27_gpio_setbits (IMX27_GPIO_PORT_B, chrono_val->dg1b);
		imx27_gpio_setbits (IMX27_GPIO_PORT_C, chrono_val->dg1c);
		imx27_gpio_setbits (IMX27_GPIO_PORT_E, DIG1_GPIO);
		index = (index + 1) % 2;
	} else {
		imx27_gpio_setbits (IMX27_GPIO_PORT_B, chrono_val->dg2b);
		imx27_gpio_setbits (IMX27_GPIO_PORT_C, chrono_val->dg2c);
		imx27_gpio_setbits (IMX27_GPIO_PORT_E, DIG2_GPIO);
		index = (index + 1) % 2;
	}
}

// Method is called by the timer to animate the LEDs connected to the GüIO
void gpio_animate_leds(struct chrono_value *chrono_val) {
	if (chrono_val->running || chrono_val->leds == 15) {
		gpio_leds_init();
		imx27_gpio_setbits (IMX27_GPIO_PORT_B, leds_animation_B[chrono_val->leds]);
		imx27_gpio_setbits (IMX27_GPIO_PORT_C, leds_animation_C[chrono_val->leds]);
		chrono_val->leds = (chrono_val->leds + 1)%16;
	}
}
