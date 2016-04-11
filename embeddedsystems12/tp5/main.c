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
 * Abstract: 	TP5 - Introduction to XPM File Format & LDC Display
 *
 * Purpose:	This module is simple program to convert xpm file into a 
 * 		16 bits bitmap to be printed out a LCD display
 *
 * Autĥor:	<Samuel Mertenat>
 * Date:	<20.01.15>
 */

#include <stdio.h>
#include <string.h>

#include <imx27_lcdc.h>
#include "xpm.h"

#include "logo_heia.xpm"
#include "logo_sis.xpm"
#include "logo_i2c.xpm"

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
	printf ("EIA-FR - Embedded Systems 1 Laboratory\n");
	printf ("TP5: Introduction to XPM File Format & LDC Display\n");
	printf ("     Convert xpm-files and display them on LCD\n");
	printf ("\n");

	imx27_lcdc_init();
	imx27_lcdc_enable();

	// creates & displays the logo for heia
	struct xpm_image img = convert_xpm_image (logo_heia);
	display_image(&img, 0, 0);

	// creates & displays the logo for sis
	img = convert_xpm_image (logo_sis);
	display_image(&img, IMX27_LCD_WIDTH/4 - img.width/2, IMX27_LCD_HEIGHT - img.height);

	// creates & displays the logo for i2ck
	img = convert_xpm_image (logo_i2c);
	display_image(&img,(IMX27_LCD_WIDTH/4)*3 - img.width/2, IMX27_LCD_HEIGHT - img.height);

	return 0;
}
