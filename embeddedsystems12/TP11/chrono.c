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
 * Project:		EIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract: 	TP11 - Stop watch controller
 *
 * Purpose:		Main module to demonstrate and to test the GPT controller
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		23.04.2015
 */

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <imx27_gpio.h>
#include "gpt.h"
#include "aitc.h"
#include "gpio.h"
#include "chrono.h"
#include "fpga.h"

// Method to start / stop the chronometer
void chrono_control(struct chrono_value *chrono_val) {
	if (!chrono_val->running) {
		chrono_val->running = true;
		printf("Chrono : running ...\n");
	} else {
		chrono_val->running = false;
		printf("Chrono : stopped ... at %u 1/100 [s]\n", chrono_val->timer);
	}
}

// Method to pause the chronometer
void chrono_lap(struct chrono_value *chrono_val) {
	if (chrono_val->running) {
		chrono_val->laps_buffer[chrono_val->laps_buffer_index] = chrono_val->timer;
		chrono_val->laps = chrono_val->laps + 1;
		chrono_val->laps_buffer_index = (chrono_val->laps_buffer_index + 1) % BUFFER_SIZE;
		printf("Chrono : lap...      at %u 1/100 [s]\n", chrono_val->timer);
		printf("Chrono : running ...\n");
	} else {
		printf("Chrono : stopped (lap not possible) ...\n");
	}
}

// Method to initialized the chronometer
void chrono_reset(struct chrono_value *chrono_val) {
	if (chrono_val->laps != 0) {
		uint32_t lastLap = 0;
		printf ("\nThe last stored lap(s)\n");
		printf ("----------------------------------------------------\n");
		printf ("##\tTime\tDifference\n");
		if (chrono_val->laps < BUFFER_SIZE) {
			for (uint8_t i = 0; i < chrono_val->laps; i++) {
				printf ("%u\t%u\t%u\n", i + 1, chrono_val->laps_buffer[i], chrono_val->laps_buffer[i] - lastLap);
				lastLap = chrono_val->laps_buffer[i];
			}
		} else {
			for (uint8_t i = 0; i < BUFFER_SIZE; i++) {
				printf ("%u\t%u\t%u\n", i + 1, chrono_val->laps_buffer[i], chrono_val->laps_buffer[i] - lastLap);
				lastLap = chrono_val->laps_buffer[i];
			}
		}
	}
	chrono_init_structure(chrono_val);
	printf("Chrono : initialized ...\n");
}

// Method is called by the timer to increment the chronometer
void chrono_increment_timer(struct chrono_value *chrono_val) {
	if (chrono_val->running) {
		chrono_val->timer = chrono_val->timer + 1;
		// if timer > 99 [s], we can't display the value on the 7segments
		if ((chrono_val->timer / 100) > 99) {
			chrono_val->dg3 = SEG_MINUS_FPGA + 0x1;
			chrono_val->dg4 = SEG_MINUS_FPGA + 0x2;
			chrono_val->dg1b = SEG_MINUS_GPIO;
			chrono_val->dg1c = 0;
			chrono_val->dg2b = SEG_MINUS_GPIO;
			chrono_val->dg2c = 0;
		} else {
			chrono_val->dg3 = seg_7[(chrono_val->timer % 100) % 10] + 0x1;
			chrono_val->dg4 = seg_7[(chrono_val->timer % 100) / 10] + 0x2;
			chrono_val->dg1b = seg_7_B[(chrono_val->timer / 100) / 10];
			chrono_val->dg1c = seg_7_C[(chrono_val->timer / 100) / 10];
			chrono_val->dg2b = seg_7_B[(chrono_val->timer / 100) % 10];
			chrono_val->dg2c = seg_7_C[(chrono_val->timer / 100) % 10];
		}
	}
}

// Method to initialize the chrono_value structure
void chrono_init_structure(struct chrono_value *chrono_val) {
	chrono_val->timer = 0;
	chrono_val->running = 0;
	chrono_val->dg1b = seg_7_B[0];
	chrono_val->dg1c = seg_7_C[0];
	chrono_val->dg2b = seg_7_B[0];
	chrono_val->dg2c = seg_7_C[0];
	chrono_val->dg3 = seg_7[0] + 0x1;
	chrono_val->dg4 = seg_7[0] + 0x2;
	chrono_val->leds = 15;	// all the leds are switched off
	chrono_val->laps = 0;
	chrono_val->laps_buffer_index = 0;

	for (uint8_t i = 0; i < BUFFER_SIZE; i++) {
		chrono_val->laps_buffer[i] = 0;
	}
}

// Method to initialize the chronometer
void chrono_init() {
	imx27_gpio_init();

	// declaration of a data structures + initialization
	struct chrono_value chrono_val;
	chrono_init_structure(&chrono_val);

	aitc_attach(AITC_GPIO, AITC_IRQ, imx27_gpio_isr, 0);
	// attaches the buttons to the different functions
	imx27_gpio_attach(IMX27_GPIO_PORT_E, 3, IMX27_GPIO_IRQ_FALLING, chrono_control, &chrono_val);
	imx27_gpio_attach(IMX27_GPIO_PORT_E, 4, IMX27_GPIO_IRQ_FALLING, chrono_lap, &chrono_val);
	imx27_gpio_attach(IMX27_GPIO_PORT_E, 6, IMX27_GPIO_IRQ_FALLING, chrono_reset, &chrono_val);

	// enables the buttons
	imx27_gpio_enable(IMX27_GPIO_PORT_E, 3);
	imx27_gpio_enable(IMX27_GPIO_PORT_E, 4);
	imx27_gpio_enable(IMX27_GPIO_PORT_E, 6);

	// enables the timers
	gpt_enable(GPT1, 10, chrono_increment_timer, &chrono_val);	// 1/100 [s] timer-> stop watch
	gpt_enable(GPT2, 1, fpga_display_value, &chrono_val);		// 1/1000 [s] timer-> to refresh screen
	gpt_enable(GPT3, 1, gpio_display_value, &chrono_val);		// 1/1000 [s] timer-> to refresh screen
	gpt_enable(GPT4, 100, gpio_animate_leds, &chrono_val);		// 1/10 [s] timer-> to animate the LEDs
}
