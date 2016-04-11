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
 * Project: HEIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract: <TODO>     
 *
 * Purpose:  <TODO>
 *
 * Author:   <Samuel Mertenat>
 * Date:     <25.02.15>
 */

#include <stdio.h>
#include <stdint.h>
#include <string.h>

#include <imx27_lcdc.h>
#include "xpm.h"

#include "touchscreen.h"

#include "img/yoda.xpm"
#include "img/cross.xpm"

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

/* Documentation: 24_touchscreen_calibration.pdf.pdf - p7-8 */
void set_calibration_matrix(double* A, double* B, double* C, double* D, double* E, double* F) {
    printf ("\nTouchscreen - Three-Point Calibration\n");
    printf ("Please, press briefly on the red cross to calibrate the display!\n");

	// 3 different points to display the cross
	uint16_t dx1 = 350, dy1 = 65;
	uint16_t dx2 = 195, dy2 = 200;
	uint16_t dx3 = 550, dy3 = 350;

	// and the touchscreen's values for these points ...
	uint16_t tx1 = 0, ty1 = 0;
	uint16_t tx2 = 0, ty2 = 0;
	uint16_t tx3 = 0, ty3 = 0;

	struct xpm_image img = convert_xpm_image (cross);
	struct touchscreen_position p = touchscreen_read_position();
	uint32_t counter = 0;

	// displays the first image and gets the touchscreen position
	display_image(&img, dx1 - img.width / 2, dy1 - img.height / 2);
	do {
		p = touchscreen_read_position();
	    for (counter = 1000000; counter > 0; counter--);
	} while (p.x == 0 && p.y == 0);
	tx1 = p.x;
	ty1 = p.y;
	imx27_lcdc_clear_screen();
    for (counter = 2000000; counter > 0; counter--);

    // and the same for the others
	display_image(&img, dx2 - img.width / 2, dy2 - img.height / 2);
	do {
		p = touchscreen_read_position();
	    for (counter = 1000000; counter > 0; counter--);
	} while (p.x == tx1 || p.y == ty1);
	tx2 = p.x;
	ty2 = p.y;
	imx27_lcdc_clear_screen();
    for (counter = 2000000; counter > 0; counter--);

	display_image(&img, dx3 - img.width / 2, dy3 - img.height / 2);
	do {
		p = touchscreen_read_position();
	    for (counter = 1000000; counter > 0; counter--);
	} while (p.x == tx2 || p.y == ty2);
	tx3 = p.x;
	ty3 = p.y;
	imx27_lcdc_clear_screen();
	free(img.image);

	// calculates the matrix
	double K = 0;
	K = (tx1-tx3)*(ty2-ty3)-(tx2-tx3)*(ty1-ty3);
	*A = ((dx1-dx3)*(ty2-ty3)-(dx2-dx3)*(ty1-ty3))/K;
	*B = ((tx1-tx3)*(dx2-dx3)-(dx1-dx3)*(tx2-tx3))/K;
	*C = (ty1*(tx3*dx2-tx2*dx3)+ty2*(tx1*dx3-tx3*dx1)+ty3*(tx2*dx1-tx1*dx2))/K;
	*D = ((dy1-dy3)*(ty2-ty3)-(dy2-dy3)*(ty1-ty3))/K;
	*E = ((tx1-tx3)*(dy2-dy3)-(dy1-dy3)*(tx2-tx3))/K;
	*F = (ty1*(tx3*dy2-tx2*dy3)+ty2*(tx1*dy3-tx3*dy1)+ty3*(tx2*dy1-tx1*dy2))/K;
    printf ("A: %f, B: %f, C: %f, D: %f, E: %f, F: %f\n", *A, *B, *C, *D, *E, *F);
    printf ("Three-Point Calibration done!\n\n");
}

/* Documentation: 24_touchscreen_calibration.pdf.pdf - p7-8 */
uint16_t adjuste_position(uint16_t x, uint16_t y, double* X, double* Y, double* Z) {
	return (x * *X + y * *Y + *Z);
}

int main() {
    printf ("\n");
    printf ("EIA-FR - Embedded Systems 2 Laboratory\n");
    printf ("TP10: SPI/Touchscreen\n");

    // initialization of the display
	imx27_lcdc_init();
	imx27_lcdc_enable();
	touchscreen_init();

	double A = 0, B = 0, C = 0, D = 0, E = 0, F = 0; // matrix for the calibration
	set_calibration_matrix(&A, &B, &C, &D, &E, &F);

	struct touchscreen_position old_position = touchscreen_read_position();
	struct touchscreen_position current_position;
	struct xpm_image img = convert_xpm_image (yoda);
	display_image(&img, IMX27_LCD_WIDTH/2 - img.width/2, IMX27_LCD_HEIGHT/2 - img.height/2);
	uint16_t counter = 0, x = 0, y = 0;

	while(1) {
		current_position = touchscreen_read_position();
		if ((current_position.x != old_position.x) && (current_position.y != old_position.y)) {
			x = adjuste_position(current_position.x, current_position.y, &A, &B, &C);
			y = adjuste_position(current_position.x, current_position.y, &D, &E, &F);

			printf ("Touchscreen position : [%d;%d] LCD position : [%d;%d]\n", current_position.x, current_position.y, x, y);
			imx27_lcdc_clear_screen();
			if (x < img.width) {
				if (y < img.height)
					display_image(&img, 0, 0);
				else if (y > IMX27_LCD_HEIGHT - img.height)
					display_image(&img, 0, IMX27_LCD_HEIGHT - img.height);
				else
					display_image(&img, 0, y-img.height/2);
			} else if (x > IMX27_LCD_WIDTH - img.width) {
				if (y < img.height)
					display_image(&img, IMX27_LCD_WIDTH - img.width, 0);
				else if (y > IMX27_LCD_HEIGHT - img.height)
					display_image(&img, IMX27_LCD_WIDTH - img.width, IMX27_LCD_HEIGHT - img.height);
				else
					display_image(&img, IMX27_LCD_WIDTH - img.width, y-img.height/2);
			} else {
				if (y < img.height)
					display_image(&img, x-img.width/2, 0);
				else if (y > IMX27_LCD_HEIGHT - img.height)
					display_image(&img, x-img.width/2, IMX27_LCD_HEIGHT - img.height);
				else
					display_image(&img, x-img.width/2, y-img.height/2);
			}
		}
	    for (counter = 10000; counter > 0; counter--);
		old_position = current_position;
	}
}
