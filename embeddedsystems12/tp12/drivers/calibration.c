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
 * Abstract:	TSC2101 - Touch Screen Calibration
 *
 * Purpose:		Module to calibrate the TSC2101 Controller
 *
 * Author:		Fabio Valverde & Samuel Mertenat
 * Date: 		17.05.2015
 */

#include <stdint.h>
#include <stdbool.h>
#include "calibration.h"
#include "tsc2101.h"
#include "../scribble.h"

// Method to calibrate the touchscreen with the LCD
/* Documentation: 24_touchscreen_calibration.pdf.pdf - p7-8 */
struct calibration_matrix calibration_set_matrix(struct pencil *p_pencil) {
	printf("Touchscreen - Three-Point Calibration\n");
	printf("Please, press briefly on the red square on the screen!\n");

	// defines 3 different points to display the squares
	uint16_t dx1 = 350, dy1 = 65;
	uint16_t dx2 = 195, dy2 = 200;
	uint16_t dx3 = 550, dy3 = 350;

	// and defines the touchscreen's variables to store the values returned
	// from the touchscreen for these points
	uint16_t tx1 = 0, ty1 = 0;
	uint16_t tx2 = 0, ty2 = 0;
	uint16_t tx3 = 0, ty3 = 0;

	// initialization of the matrix
	struct calibration_matrix matrix = {
			.A = 0,
			.B = 0,
			.C = 0,
			.D = 0,
			.E = 0,
			.F = 0
	};

	struct tsc2101_position p = tsc2101_read_position();
	uint32_t counter = 0;

	// displays the first square and gets the touchscreen position
	scribble_display_pencil(&p_pencil->lead_of_pencil, dx1 - p_pencil->lead_of_pencil.width / 2, dy1 - p_pencil->lead_of_pencil.height / 2);
	do {
		p = tsc2101_read_position();
		for (counter = 1000000; counter > 0; counter--);
	} while (p.x == 0 && p.y == 0);
	tx1 = p.x;
	ty1 = p.y;
	imx27_lcdc_clear_screen();
	for (counter = 2000000; counter > 0; counter--);

	// and the same for the others
	scribble_display_pencil(&p_pencil->lead_of_pencil, dx2 - p_pencil->lead_of_pencil.width / 2, dy2 - p_pencil->lead_of_pencil.height / 2);
	do {
		p = tsc2101_read_position();
		for (counter = 1000000; counter > 0; counter--);
	} while (p.x == tx1 || p.y == ty1);
	tx2 = p.x;
	ty2 = p.y;
	imx27_lcdc_clear_screen();
	for (counter = 2000000; counter > 0; counter--);

	scribble_display_pencil(&p_pencil->lead_of_pencil, dx3 - p_pencil->lead_of_pencil.width / 2, dy3 - p_pencil->lead_of_pencil.height / 2);
	do {
		p = tsc2101_read_position();
		for (counter = 1000000; counter > 0; counter--);
	} while (p.x == tx2 || p.y == ty2);
	tx3 = p.x;
	ty3 = p.y;
	imx27_lcdc_clear_screen();

	double K = 0;
	K = (tx1 - tx3) * (ty2 - ty3) - (tx2 - tx3) * (ty1 - ty3);
	matrix.A = ((dx1 - dx3) * (ty2 - ty3) - (dx2 - dx3) * (ty1 - ty3)) / K;
	matrix.B = ((tx1 - tx3) * (dx2 - dx3) - (dx1 - dx3) * (tx2 - tx3)) / K;
	matrix.C = (ty1 * (tx3 * dx2 - tx2 * dx3) + ty2 * (tx1 * dx3 - tx3 * dx1) + ty3 * (tx2 * dx1 - tx1 * dx2)) / K;
	matrix.D = ((dy1 - dy3) * (ty2 - ty3) - (dy2 - dy3) * (ty1 - ty3)) / K;
	matrix.E = ((tx1 - tx3) * (dy2 - dy3) - (dy1 - dy3) * (tx2 - tx3)) / K;
	matrix.F = (ty1 * (tx3 * dy2 - tx2 * dy3) + ty2 * (tx1 * dy3 - tx3 * dy1) + ty3 * (tx2 * dy1 - tx1 * dy2)) / K;
	printf("	A: %f, B: %f, C: %f\n", matrix.A, matrix.B, matrix.C);
	printf("	D: %f, E: %f, F: %f\n", matrix.D, matrix.E, matrix.F);
	printf("----------------------------------------------------\n");
	return matrix;
}

/* Documentation: 24_touchscreen_calibration.pdf.pdf - p7-8 */
uint16_t calibration_adjust_x(uint16_t x, uint16_t y, struct calibration_matrix *p_matrix) {
	return (x * p_matrix->A + y * p_matrix->B + p_matrix->C);
}

uint16_t calibration_adjust_y(uint16_t x, uint16_t y, struct calibration_matrix *p_matrix) {
	return (x * p_matrix->D + y * p_matrix->E + p_matrix->F);
}

