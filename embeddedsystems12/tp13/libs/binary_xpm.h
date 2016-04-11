#pragma once
#ifndef XPM_H
#define XMP_H
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
 * Project:		EIA-FR / Embedded Systems 1 Laboratory
 *
 * Abstract: 	TP5 - Introduction to XPM File Format & LDC Display
 *
 * Purpose:		This module is simple program to convert xpm file into a
 * 				16 bits bitmap to be printed out a LCD display
 *
 * Autĥor:		Samuel Mertenat
 * Date:		20.01.15
 */

#include <stdint.h>

/* Documentation: ex 9.4 */
// xpm image
/*struct xpm_image {
	uint32_t width;		// image width [pixels]
	uint32_t height;	// image height [pixels]
	uint16_t* image;	// image coded [RGB565]
};*/

/**
 * Convert a XPM image into a 16-bit bitmap format ready to be displayed
 * on the LCD display.
 *
 * @param xpm_data xpm-image to be converted
 * @result converted xmp_image
 */
extern struct xpm_image binary_convert_xpm_image (char* xpm_data[]);

#endif
