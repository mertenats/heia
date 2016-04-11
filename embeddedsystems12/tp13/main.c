/**
 * File: 		main.c
 *
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
 * Abstract: 	TP13 - Optimizsations
 *
 * Purpose:		Main module to demonstrate and to test the
 * 				optimizations made on the code
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		14.06.2015
 */

#define BINARY_SEARCH

#include "interrupt.h"
#include "exception.h"
#include "aitc.h"
#include "gpt.h"
#include <imx27_lcdc.h>

// the xpm images
#include "images/xpm/tour_S.xpm"
#include "images/xpm/tour_M.xpm"
#include "images/xpm/tour_L.xpm"
#include "images/xpm/tour_XL.xpm"

#include "libs/binary_xpm.h"
#include "libs/linear_xpm.h"

struct chronometer {
	uint32_t timer;
};

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

// Method is called by the timer to increment the chronometer
void increment_timer(struct chronometer *p_chrono) {
	p_chrono->timer = p_chrono->timer + 1;
}

int main() {
	printf ("\n");
	printf ("HEIA-FR - Embedded Systems 2 Laboratory\n");
	printf ("TP13:   - TP5 Optimizations\n");
	printf ("          Fabio Valverde & Samuel Mertenat\n");
	printf ("----------------------------------------------------\n");

	// initializes the LCD and displays an image
	imx27_lcdc_init();
	imx27_lcdc_enable();

	// initialization of the different modules...
	interrupt_init();
	exception_init();
	aitc_init();
	interrupt_enable();
	gpt_init();
	imx27_gpio_init();

	imx27_mmu_init();

	// variables to store the beginning / end of the procedure
	uint32_t start = 0;
	uint32_t stop = 0;
	uint32_t result = 0;

	// struct used for the chronometer (incremented by the gpt)
	struct chronometer chrono;
	chrono.timer = 0;

	// enables the timer
	gpt_enable(GPT1, 1, increment_timer, &chrono);

	//---------- NO CACHE ----------
	// gets the initial time
	start = chrono.timer;
	// converts & displays the small image
	struct xpm_image img = linear_convert_xpm_image(tour_S);
	display_image(&img, 0, 0);
	// gets the final time
	stop = chrono.timer;

	result = stop - start;
	printf("Linear search - No cache\n");
	printf("Small image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_M);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Medium image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_L);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Large image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_XL);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Extra-large image: %u [ms]\n\n", result);


	start = chrono.timer;
	// converts & displays the small image
	img = binary_convert_xpm_image(tour_S);
	display_image(&img, 0, 0);
	// gets the final time
	stop = chrono.timer;

	result = stop - start;
	printf("Binary search - No cache\n");
	printf("Small image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_M);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Medium image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_L);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Large image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_XL);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Extra-large image: %u [ms]\n", result);

	//---------- DATA CACHE ----------
	printf ("----------------------------------------------------\n");
	imx27_mmu_enable_dcache();
	start = chrono.timer;
	img = linear_convert_xpm_image(tour_S);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Linear search - Data cache\n");
	printf("Small image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_M);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Medium image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_L);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Large image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_XL);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Extra-large image: %u [ms]\n\n", result);


	printf("Binary search - Data cache\n");
	start = chrono.timer;
	img = binary_convert_xpm_image(tour_S);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Small image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_M);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Medium image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_L);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Large image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_XL);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Extra-large image: %u [ms]\n", result);

	//---------- DATA + INSTRUCTIONS CACHEs ----------
	printf ("----------------------------------------------------\n");
	imx27_mmu_enable_icache();
	start = chrono.timer;
	img = linear_convert_xpm_image(tour_S);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Linear search - Data + instructions caches\n");
	printf("Small image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_M);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Medium image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_L);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Large image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_XL);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Extra-large image: %u [ms]\n\n", result);

	printf("Binary search - Data + instructions caches\n");
	start = chrono.timer;
	img = binary_convert_xpm_image(tour_S);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Small image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_M);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Medium image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_L);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Large image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_XL);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Extra-large image: %u [ms]\n", result);

	/*
	//---------- Instructions CACHE ----------
	imx27_mmu_enable_icache();
	// gets the initial time
	start = chrono.timer;
	// converts & displays the small image
	struct xpm_image img = linear_convert_xpm_image(tour_S);
	display_image(&img, 0, 0);
	// gets the final time
	stop = chrono.timer;

	result = stop - start;
	printf("Linear search - Instructions cache\n");
	printf("Small image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_M);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Medium image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_L);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Large image: %u [ms]\n", result);

	start = chrono.timer;
	img = linear_convert_xpm_image(tour_XL);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Extra-large image: %u [ms]\n\n", result);

	start = chrono.timer;
	// converts & displays the small image
	img = binary_convert_xpm_image(tour_S);
	display_image(&img, 0, 0);
	// gets the final time
	stop = chrono.timer;

	result = stop - start;
	printf("Binary search - Instructions cache\n");
	printf("Small image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_M);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Medium image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_L);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Large image: %u [ms]\n", result);

	start = chrono.timer;
	img = binary_convert_xpm_image(tour_XL);
	display_image(&img, 0, 0);
	stop = chrono.timer;

	result = stop - start;
	printf("Extra-large image: %u [ms]\n", result);
	*/
	return 0;
}
