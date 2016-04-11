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
 * Project:		HEIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract:	Main module
 *
 * Purpose:		Main module to demonstrate and to test the scribble module
 *
 * Author:		Fabio Valverde & Samuel Mertenat
 * Date: 		17.05.2015
 */

#include <imx27_gpio.h>
#include "interrupt.h"
#include "interrupts/exception.h"
#include "interrupts/aitc.h"
#include <imx27_lcdc.h>
#include "drivers/tsc2101.h"
#include "scribble.h"

int main() {
	printf("\n");
	printf("HEIA-FR - Embedded Systems 2 Laboratory\n");
	printf("TP12: Gribouillage\n");
	printf("Fabio Valverde & Samuel Mertenat\n");
	printf("----------------------------------------------------\n");
	printf("You can use the GPIO buttons to set the pencil\n");
	printf("	Left:\tClear screen\n");
	printf("	Middle:\tChange the color [RED - GREEN - BLUE - YELLOW - WHITE]\n");
	printf("	Right:\tChange the size [SMALL - MEDIUM - LARGE]\n");
	printf("----------------------------------------------------\n");

	// Initialization of the different modules
	imx27_gpio_init();
	imx27_lcdc_init();
	imx27_lcdc_enable();
	interrupt_init();
	exception_init();
	aitc_init();
	interrupt_enable();
	tsc2101_init();
	scribble_init();
	return 0;
}
