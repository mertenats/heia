#ifndef SCRIBBLE_H_
#define SCRIBBLE_H_
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
 * Abstract:	Scribble generation
 *
 * Purpose:		Module to allow to scribble on the LCD touchscreen
 *
 * Author:		Fabio Valverde & Samuel Mertenat
 * Date: 		17.05.2015
 */

#include <stdint.h>

// Enumeration of possible pencil's colors
enum colors {
	RED = 		63488,		// RGB 255-0-0
	GREEN = 	34784,		// RGB 128-255-0
	BLUE = 		1055,		// RGB 0-128-255
	YELLOW = 	65504,		// RGB 255-255-0
	WHITE = 	65535		// RGB 255-255-255
};

// Enumeration of possible pencil's widths [pixels]
enum widths {
	SMALL = 4,
	MEDIUM = 8,
	LARGE = 16
};

// Structure to store the components of the xpm_image
struct xpm_image {
	uint8_t width;		// image width
	uint8_t height;	// image height
	uint16_t* img;		// image coded [RGB565]
};

// Enumeration of possible state
enum states {
	DRAWING,
	ERASING,
	CHANGING_COLOR,
	CHANGING_WIDTH
};

// Pencil's structure
struct pencil {
	uint16_t x;							// x position
	uint16_t y;							// y position
	enum colors color;					// color
	enum widths width;					// width
	struct xpm_image rubber;			// to erase the preview section
	struct xpm_image lead_of_pencil;	// xpm image of the pencil
	enum states state;					// the pencil's state
};

// Method to initialize the scribble
extern void scribble_init();

// Method to refresh the LCD
extern void scribble_display_pencil(struct xpm_image *p_img, uint16_t p_x, uint16_t p_y);

#endif
