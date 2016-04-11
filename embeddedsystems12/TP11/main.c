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
 * Abstract: 	TP11 - Stop watch
 *
 * Purpose:		Main module to demonstrate and to test the stop watch
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		23.04.2015
 */

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include "interrupt.h"
#include "exception.h"
#include "aitc.h"
#include "chrono.h"
#include "gpt.h"
#include <imx27_lcdc.h>
#include "xpm.h"
#include <string.h>
#include "Logo_stop_watch.xpm"

// Method to display an image on the LCD (source code from my TP5)
void display_image(struct xpm_image* img, uint16_t x, uint16_t y) {
	/* Documentation: imx27_lcdc.h */
	// returns the base address of the LCD
	uint16_t *bitmap = imx27_lcdc_get_bitmap();

	// goes to the coordinate x, y
	bitmap += IMX27_LCD_WIDTH * y;
	bitmap += x;

	uint16_t *from = img->image;
	uint16_t *to = bitmap;
	// 2 bytes per pixel; line width x 2
	uint16_t line_length_bitmap = img->width * 2;

	// loops through each line of the picture
	for(uint16_t i = 0; i < img->height; i++) {
		memcpy(to, from, line_length_bitmap);
		to += IMX27_LCD_WIDTH;		// goes to next line
		from += img->width;
	}
}

int main() {
	printf ("\n");
	printf ("EIA-FR - Embedded Systems 2 Laboratory\n");
	printf ("TP11:  - Stop watch Program\n");
	printf ("         Fabio Valverde & Samuel Mertenat\n");
	printf ("----------------------------------------------------\n");
	printf ("You can use the GPIO buttons to control the stop watch\n");
	printf ("         Left: Start - Stop\n");
	printf ("         Middle: New Lap\n");
	printf ("         Right: Initialization\n");
	printf ("----------------------------------------------------\n");

	// initializes the LCD and displays an image
	imx27_lcdc_init();
	imx27_lcdc_enable();
	struct xpm_image img = convert_xpm_image(Logo_stop_watch);
	display_image(&img, 0, 0);

	// initialization of the different modules...
	interrupt_init();
	exception_init();
	aitc_init();
	interrupt_enable();
	gpt_init();
	chrono_init();

	return 0;
}
