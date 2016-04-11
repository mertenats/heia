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
 * Project:	EIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract: 	TP7 - Hardware Interrupt Handling
 *
 * Purpose:		Main module to demonstrate and to test the i.MX27
 *				interrupt handling.
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		19.03.2015
 */

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>

#include <imx27_gpio.h>

#include "interrupt.h"
#include "exception.h"
#include "display.h"
#include "aitc.h"

/* ------------------------------------------------------------------------- */

void aitc_test(void* param) {
	printf ("AITC interrupt simulation on GPT6 occurred\n");
	aitc_force(AITC_GPT6, false);
	aitc_detach(AITC_GPT6);
	*(bool*)param = true;
}

void gpio_e4_test(void* param) {
	printf ("GPIO interrupt test on port_e pin_4 occurred\n");
	imx27_gpio_detach (IMX27_GPIO_PORT_E, 4);
	*(bool*)param = true;
}

void gpio_count(void* param) {
	(*(int*)param)++;
}

void gpio_reset(void* param) {
	*(int*)param = 0;
}

/* ------------------------------------------------------------------------- */

int main () {
	printf ("\n");
	printf ("EIA-FR - Embedded Systems 2 Laboratory\n");
	printf ("TP7: i.MX27 Hardware Interrupt Handling Test Program\n");
	printf ("----------------------------------------------------\n");

	/* modules initialization */
	interrupt_init();
	exception_init();
	aitc_init();
	imx27_gpio_init();
	aitc_attach(AITC_GPIO, AITC_IRQ, imx27_gpio_isr, 0);
	display_init();
	interrupt_enable();

	/* test aitc interrupt processing */
	bool aitc_simul_has_occured = false;
	aitc_attach(AITC_GPT6, AITC_IRQ, aitc_test, &aitc_simul_has_occured);
	aitc_force(AITC_GPT6, true);
	while (!aitc_simul_has_occured);

	/* test gpio interrupt processing */
	bool gpio_has_been_pressed = false;
	imx27_gpio_attach (IMX27_GPIO_PORT_E, 4, IMX27_GPIO_IRQ_FALLING, gpio_e4_test, &gpio_has_been_pressed);
	imx27_gpio_enable (IMX27_GPIO_PORT_E, 4);
	printf (" --> press gpio switch 1 to continue...\n");
	while (!gpio_has_been_pressed);

	/* application initialization */
	int counter = 0;
	imx27_gpio_attach(IMX27_GPIO_PORT_E, 3, IMX27_GPIO_IRQ_FALLING, gpio_count, &counter);
	imx27_gpio_enable(IMX27_GPIO_PORT_E, 3);
	imx27_gpio_attach(IMX27_GPIO_PORT_E, 6, IMX27_GPIO_IRQ_FALLING, gpio_reset, &counter);
	imx27_gpio_enable(IMX27_GPIO_PORT_E, 6);

	/* main loop */
	while(1) {
		display_value (counter > 99 ? -99 : counter);
	}
	return 0;
}
