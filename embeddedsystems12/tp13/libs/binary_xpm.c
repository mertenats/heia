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

#include "binary_xpm.h"
#include "linear_xpm.h"

/* Documentation: ex 9.4 */
// color code structure used to build color map
struct binary_color_code {
	uint32_t code;		// color code
	uint32_t color;		// color value in RGB565
};

// Convert an ascii hex value into a interger value
static inline uint32_t binary_a2i(char c) {
	if ((c >= '0') && (c <= '9'))
		return c - '0';
	if ((c >= 'A') && (c <= 'F'))
		return c - 'A' + 10;
	if ((c >= 'a') && (c <= 'f'))
		return c - 'a' + 10;
	return 0;
}

// Convert RGB888 value into RGB565
static inline uint32_t binary_rgb565 (uint32_t r, uint32_t g, uint32_t b) {
	return (((r & 0xf8ul) >> 3) << 11) | (((g & 0xfcul) >> 2) << 5) | ((b & 0xf8ul) >> 3);
}

/**
 * Get associated color value to a color code contained within the color map
 *
 * @param map color map
 * @param colors numbers of colors contained into the color map
 * @param line one line of the image to convert
 * @param chars number of characters per color-code
 * @result associated color value
 */

// variables for cache purpose
uint16_t cache = 0;
uint32_t oldCode = 0;

static uint16_t binary_get_color (struct binary_color_code* map, int colors, const char* line, int chars) {
	uint32_t code = 0;
	for (int i = chars; i > 0; i--)
		code = (code << 8) + *line++;

	// if cached, returns the color directly
	if (code == oldCode) {
		return cache;
	}

	// searches for the color; binary search
	uint32_t l = 0;				// left
	uint32_t r = colors - 1;	// right
	uint32_t m = 0;				// middle
	while (r != l + 1) {
		m = (r + l) / 2;		// cuts the interval by 2
		if (map[m].code >= code) {
			r = m;
		} else {
			l = m;
		}
	}

	// if found, updates the cache and returns the color
	if (r < (colors) && map[r].code == code) {
		cache = map[r].color;
		oldCode = code;
		return cache;
	}
}

/**
 * Parse a color line to extract color code and color value
 *
 * @param c_str color line to be parsed
 * @param chars number of characters per color code
 * @result converted color code & value
 */
static struct binary_color_code binary_parse_color(const char* c_str, int chars) {
	struct binary_color_code map = {.code=0, .color=0,};
	for (int j = chars; j > 0; j--) map.code = (map.code << 8) + *c_str++;

	while (*c_str != 0)
		if (*c_str++ == '#') {
			uint32_t r = (binary_a2i(c_str[0]) << 4) + binary_a2i(c_str[1]);
			uint32_t g = (binary_a2i(c_str[2]) << 4) + binary_a2i(c_str[3]);
			uint32_t b = (binary_a2i(c_str[4]) << 4) + binary_a2i(c_str[5]);
			map.color = binary_rgb565(r,g,b);
			break;
		}
	return map;
}

/**
 * Compare 2 structures of color_code
 *
 * @param 2 structures color_code
 * @result value of the comparison
 */
int binary_compareColorCode(const void *c1, const void *c2) {
	struct binary_color_code* pC1 = (struct binary_color_code*) c1;
	struct binary_color_code* pC2 = (struct binary_color_code*) c2;
	return pC1->code - pC2->code;
}

/**
 * Convert a XPM image into a 16-bit bitmap format ready to be displayed
 * on the LCD display.
 *
 * @param xpm_data xpm-image to be converted
 * @result converted xpm-image
 */
struct xpm_image binary_convert_xpm_image (char* xpm_data[]) {
	struct xpm_image xpm = {.width=0, .height=0, .image=0,};
	uint32_t colors = 0;
	uint32_t chars = 0;

	sscanf(xpm_data[0], "%u %u %u %u",
		&xpm.width, &xpm.height, &colors, &chars);

	xpm.image = malloc(xpm.height * xpm.width * sizeof(*xpm.image));
	struct binary_color_code* map = calloc (colors, sizeof(*map));
	if ((xpm.image != 0) && (map != 0)) {
		for (uint32_t i = 1; i <= colors; i++) {
			struct binary_color_code ele = binary_parse_color (xpm_data[i], chars);
			map[i-1] = ele;
		}

		// QuickSort for the binary search
		qsort(map, colors, sizeof(struct binary_color_code), binary_compareColorCode);

		uint16_t* p = xpm.image;
		for (uint32_t y = 0; y < xpm.height; y++) {
			const char* l = xpm_data[1 + colors + y];
			for (uint32_t x = 0; x < xpm.width; x++) {
				*p++ = binary_get_color (map, colors, l, chars);
				l+=chars;
			}
		}

		free (map);
	}
	return xpm;
}
