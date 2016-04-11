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

#include <imx27_lcdc.h>
#include <imx27_gpio.h>
#include "interrupts/aitc.h"
#include "scribble.h"
#include "calibration.h"

#define PREVIEW_SIZE 30		// pencil's preview section (24 x 24)

// Method is called by the PE3 GPIO button to clear the LCD
void scribble_clear_LCD(struct pencil *p_pencil) {
	p_pencil->state = ERASING;
}

// Method is called by the PE4 GPIO button to change the color of the pencil
void scribble_change_color(struct pencil *p_pencil) {
	switch (p_pencil->color) {
	case RED:
		p_pencil->color = GREEN;
		printf("Color : GREEN\n");
		break;
	case GREEN:
		p_pencil->color = BLUE;
		printf("Color : BLUE\n");
		break;
	case BLUE:
		p_pencil->color = YELLOW;
		printf("Color : YELLOW\n");
		break;
	case YELLOW:
		p_pencil->color = WHITE;
		printf("Color : WHITE\n");
		break;
	case WHITE:
		p_pencil->color = RED;
		printf("Color : RED\n");
		break;
	}
	p_pencil->state = CHANGING_COLOR;
}

// Method is called by the PE6 GPIO button to change the width of the pencil
void scribble_change_width(struct pencil *p_pencil) {
	switch (p_pencil->width) {
	case SMALL:
		p_pencil->width = MEDIUM;
		printf("Size : MEDIUM\n");
		break;
	case MEDIUM:
		p_pencil->width = LARGE;
		printf("Size : LARGE\n");
		break;
	case LARGE:
		p_pencil->width = SMALL;
		printf("Size : SMALL\n");
		break;
	}
	p_pencil->state = CHANGING_WIDTH;
}

// Method is called when a pressure occurs on the touchscreen
// This method prints to the coordinates x-y the pencil's representation
void scribble_display_pencil(struct xpm_image *p_img, uint16_t p_x, uint16_t p_y) {
	// returns the base address of the LCD
	uint16_t *bitmap = imx27_lcdc_get_bitmap();

	// goes to the coordinate x, y
	bitmap += IMX27_LCD_WIDTH * p_y;
	bitmap += p_x;

	uint16_t *from = p_img->img;
	uint16_t *to = bitmap;
	// 2 bytes per pixel; line width x 2
	uint16_t line_length_bitmap = p_img->width * 2;

	// loops through each line of the picture
	for (uint16_t i = 0; i < p_img->height; i++) {
		memcpy(to, from, line_length_bitmap);
		to += IMX27_LCD_WIDTH; // goes to next line
		from += p_img->width;
	}
}

// Method is called when the user change the colors or the size of the pencil
// This method creates a new representation of the pencil (xpm_image)
void scribble_update_pencil(struct pencil *p_pencil) {
	// creates and initializes a xpm image
	struct xpm_image xpm = {
			.width = p_pencil->width,
			.height = p_pencil->width,
			.img = 0
	};

	// allocates the needed memory (size x size x 2 bytes)
	xpm.img = malloc(xpm.height * xpm.width * sizeof(*xpm.img));
	// codes the new representation [rgb565]
	uint16_t* p = xpm.img;
	for (uint8_t y = 0; y < xpm.height; y++) {
		for (uint8_t x = 0; x < xpm.width; x++) {
			*p++ = p_pencil->color;
		}
	}

	// liberates the memory used by the oldest representation and assigns the new representation to the pencil
	free(p_pencil->lead_of_pencil.img);
	p_pencil->lead_of_pencil = xpm;

	// if the rubber (for the preview section) isn't created yet, creates the representation for its
	if (p_pencil->rubber.img == 0) {
		xpm.height = PREVIEW_SIZE;
		xpm.width = PREVIEW_SIZE;
		xpm.img = 0;
		xpm.img = malloc(xpm.height * xpm.width * sizeof(*xpm.img));
		p = xpm.img;
		for (uint8_t y = 0; y < xpm.height; y++) {
			for (uint8_t x = 0; x < xpm.width; x++) {
				*p++ = WHITE;
			}
		}
		p_pencil->rubber = xpm;
	}
}

// Method is called when the user change the colors or the size of the pencil
// This method erases the preview section and displays the pencil's representation
void scribble_update_pencil_preview(struct pencil *p_pencil) {
	scribble_display_pencil(&p_pencil->rubber, 0, 0);
	p_pencil->x = PREVIEW_SIZE / 2 - p_pencil->lead_of_pencil.width / 2;
	p_pencil->y = PREVIEW_SIZE / 2 - p_pencil->lead_of_pencil.height / 2;
	scribble_display_pencil(&p_pencil->lead_of_pencil, p_pencil->x, p_pencil->y);
}

// Method to initialize the scribble
void scribble_init() {
	// creates a red pencil with a medium size
	struct pencil pencil = {
			.color = RED,
			.width = LARGE,
			.lead_of_pencil.img = 0,
			.rubber.img = 0,
			.state = DRAWING
	};
	scribble_update_pencil(&pencil);

	// creates & initializes the matrix used for the calibration
	struct calibration_matrix matrix = calibration_set_matrix(&pencil);

	aitc_attach(AITC_GPIO, AITC_IRQ, imx27_gpio_isr, 0);
	// attaches  the buttons to the different functions
	imx27_gpio_attach(IMX27_GPIO_PORT_E, 3, IMX27_GPIO_IRQ_FALLING, scribble_clear_LCD, &pencil);
	imx27_gpio_attach(IMX27_GPIO_PORT_E, 4, IMX27_GPIO_IRQ_FALLING, scribble_change_color, &pencil);
	imx27_gpio_attach(IMX27_GPIO_PORT_E, 6, IMX27_GPIO_IRQ_FALLING, scribble_change_width, &pencil);

	// enables the buttons
	imx27_gpio_enable(IMX27_GPIO_PORT_E, 3);
	imx27_gpio_enable(IMX27_GPIO_PORT_E, 4);
	imx27_gpio_enable(IMX27_GPIO_PORT_E, 6);

	struct tsc2101_position current_p;
	struct tsc2101_position last_p = tsc2101_read_position();
	uint8_t same_pressure = 0;
	scribble_update_pencil_preview(&pencil);	// displays the preview section

	while (1) {
		if (pencil.state == DRAWING) {
			current_p = tsc2101_read_position();
			if (current_p.z == last_p.z) {
				same_pressure++;
				if (same_pressure >= 9) {
					same_pressure = 0;

					// adjusts the coordinates x-y
					pencil.x = calibration_adjust_x(current_p.x, current_p.y, &matrix);
					pencil.y = calibration_adjust_y(current_p.x, current_p.y, &matrix);

					if (pencil.x <= PREVIEW_SIZE && pencil.y <= PREVIEW_SIZE) {
						continue;
						// displays nothing on the preview section ...
					} else if (pencil.x < pencil.lead_of_pencil.width) {
						if (pencil.y < pencil.lead_of_pencil.height)
							scribble_display_pencil(&pencil.lead_of_pencil, 0, 0);
						else if (pencil.y > IMX27_LCD_HEIGHT - pencil.lead_of_pencil.height)
							scribble_display_pencil(&pencil.lead_of_pencil, 0, IMX27_LCD_HEIGHT - pencil.lead_of_pencil.height);
						else
							scribble_display_pencil(&pencil.lead_of_pencil, 0, pencil.y - pencil.lead_of_pencil.height / 2);
					} else if (pencil.x > IMX27_LCD_WIDTH - pencil.lead_of_pencil.width) {
						if (pencil.y < pencil.lead_of_pencil.height)
							scribble_display_pencil(&pencil.lead_of_pencil, IMX27_LCD_WIDTH - pencil.lead_of_pencil.width, 0);
						else if (pencil.y > IMX27_LCD_HEIGHT - pencil.lead_of_pencil.height)
							scribble_display_pencil(&pencil.lead_of_pencil, IMX27_LCD_WIDTH - pencil.lead_of_pencil.width, IMX27_LCD_HEIGHT - pencil.lead_of_pencil.height);
						else
							scribble_display_pencil(&pencil.lead_of_pencil, IMX27_LCD_WIDTH - pencil.lead_of_pencil.width, pencil.y - pencil.lead_of_pencil.height / 2);
					} else {
						if (pencil.y < pencil.lead_of_pencil.height)
							scribble_display_pencil(&pencil.lead_of_pencil, pencil.x - pencil.lead_of_pencil.width / 2, 0);
						else if (pencil.y > IMX27_LCD_HEIGHT - pencil.lead_of_pencil.height)
							scribble_display_pencil(&pencil.lead_of_pencil, pencil.x - pencil.lead_of_pencil.width / 2, IMX27_LCD_HEIGHT - pencil.lead_of_pencil.height);
						else
							scribble_display_pencil(&pencil.lead_of_pencil, pencil.x - pencil.lead_of_pencil.width / 2, pencil.y - pencil.lead_of_pencil.height / 2);
					}
				}
			}
			last_p.x = current_p.x;
			last_p.x = current_p.y;
			last_p.z = current_p.z;
		} else if (pencil.state == ERASING) {
			imx27_lcdc_clear_screen();
			scribble_update_pencil_preview(&pencil);
			printf("LCD cleared\n");
			pencil.state = DRAWING;
		} else if (pencil.state == CHANGING_COLOR || pencil.state == CHANGING_WIDTH) {
			scribble_update_pencil(&pencil);
			scribble_update_pencil_preview(&pencil);
			pencil.state = DRAWING;
		}
	}
}
