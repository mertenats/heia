#pragma once
#ifndef CALIBRATION_CALIBRATION_H_
#define CALIBRATION_CALIBRATION_H_
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
#include "tsc2101.h"
#include "../scribble.h"

// Structure to store the components of the calibration matrix
struct calibration_matrix {
	double A;
	double B;
	double C;
	double D;
	double E;
	double F;
};

// Method to initialize the calibration
extern struct calibration_matrix calibration_set_matrix(struct pencil *p_pencil);

// Method to adjust the coordinate given by the touchscreen
extern uint16_t calibration_adjust_x(uint16_t x, uint16_t y, struct calibration_matrix *p_matrix);
extern uint16_t calibration_adjust_y(uint16_t x, uint16_t y, struct calibration_matrix *p_matrix);
#endif
