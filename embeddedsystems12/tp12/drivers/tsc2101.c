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
 * Project:	EIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract:	TSC2101 - Touch Screen Controller
 *
 * Author: 	Daniel Gachet / Florian Nicoulaud
 * Date: 	28.06.2014
 */

#include "tsc2101.h"
#include <imx27_cspi.h>

/* ADC */
#define TSC2101_P1_ADC_PSTCM		(1 << 15)
#define TSC2101_P1_ADC_ADST		(1 << 14)
#define TSC2101_P1_ADC_ADCSM(x)		(((x) & 0xf) << 10)
#define TSC2101_P1_ADC_RESOL(x)		(((x) & 0x3) << 8)
#define TSC2101_P1_ADC_ADAVG(x)		(((x) & 0x3) << 6)
#define TSC2101_P1_ADC_ADCR(x)		(((x) & 0x3) << 4)
#define TSC2101_P1_ADC_PVSTC(x)		(((x) & 0x7) << 1)
#define TSC2101_P1_ADC_AVGFS		(1 << 0)

/* RESET CONTROL */
#define TSC2101_P1_RESET_RSALL		(0xbb00)

/* REFERENCE CONTROL */
#define TSC2101_P1_REF_VREFM	 	(1 << 4)
#define TSC2101_P1_REF_RPWUDL(x) 	(((x) & 0x3) << 2)
#define TSC2101_P1_REF_RPWDN	 	(1 << 1)
#define TSC2101_P1_REF_IREFV	 	(1 << 0)

/* CONFIGURATION CONTROL */
#define TSC2101_P1_CONF_SWPDTD	  	(1 << 6)
#define TSC2101_P1_CONF_PRECTM(x) 	(((x) & 0x7) << 3)
#define TSC2101_P1_CONF_RPWUDL(x) 	(((x) & 0x7) << 0)

/* MEASUREMENT CONFIGURATION */
#define TSC2101_P1_MEASU_TSCAN		(1 << 15)
#define TSC2101_P1_MEASU_A1CONF		(1 << 14)
#define TSC2101_P1_MEASU_A2CONF		(1 << 13)
#define TSC2101_P1_MEASU_ATEMES		(1 << 12)
#define TSC2101_P1_MEASU_AA1MES		(1 << 11)
#define TSC2101_P1_MEASU_AA2MES		(1 << 10)
#define TSC2101_P1_MEASU_IGPIO1		(1 << 9)
#define TSC2101_P1_MEASU_THMFXL		(1 << 8)
#define TSC2101_P1_MEASU_THMNFL		(1 << 7)
#define TSC2101_P1_MEASU_EXTRES		(1 << 2)

/* PROGRAMMABLE DELAY */
#define TSC2101_P1_DELAY_NTSPDELE	(1 << 15)
#define TSC2101_P1_DELAY_NTSPDINTV(x)	(((x) & 0x7) << 12)
#define TSC2101_P1_DELAY_TSPDELEN	(1 << 11)
#define TSC2101_P1_DELAY_TSPDINTV(x)	(((x) & 0x7) << 8)
#define TSC2101_P1_DELAY_CLKSEL		(1 << 7)
#define TSC2101_P1_DELAY_CLKDIV(x)	(((x) & 0x7f) << 0)

static struct imx27_cspi_ctrl spi2;


void tsc2101_init()
{
	/* initialize the spi interface */
	spi2 = imx27_cspi_init(IMX27_SPI2, IMX27_SS0, 8000, 16);

	/* Reset the TSC2101 */
	uint32_t data = TSC2101_P1_RESET_RSALL;
	uint32_t c = (1 << 11) + (4 << 5);
	spi2.write (&spi2, c, data);

	/* Configure touchscreen scan */
	data = TSC2101_P1_ADC_PSTCM    | TSC2101_P1_ADC_ADCSM(2)
	     | TSC2101_P1_ADC_ADAVG(1) | TSC2101_P1_ADC_RESOL(2)
	     | TSC2101_P1_ADC_ADCR(3)  | TSC2101_P1_ADC_PVSTC(3);

	c = (1 << 11) + (0 << 5);
	spi2.write (&spi2, c, data);

	/* Status Register */
	data = 0;
	c = (1 << 11) + (1 << 5);
	spi2.write (&spi2, c, data);

	/* Buffer Register */
	data = 0;
	c = (1 << 11) + (2 << 5);
	spi2.write (&spi2, c, data);

	/* Reference Control */
	data = TSC2101_P1_REF_RPWUDL(1) | TSC2101_P1_REF_RPWDN;
	c = (1 << 11) + (3 << 5);
	spi2.write (&spi2, c, data);

	/* Configuration Control */
	data = TSC2101_P1_CONF_PRECTM(0);
	c = (1 << 11) + (5 << 5);
	spi2.write (&spi2, c, data);

	/* Measurement Control */
	data = TSC2101_P1_MEASU_TSCAN | TSC2101_P1_MEASU_ATEMES;
	c = (1 << 11) + (12 << 5);
	spi2.write (&spi2, c, data);

	/* Programmable Delay */
	data = TSC2101_P1_DELAY_CLKDIV(0) | TSC2101_P1_DELAY_TSPDINTV(2)
	     | TSC2101_P1_DELAY_TSPDELEN;
	c = (1 << 11) + (13 << 5);
	spi2.write (&spi2, c, data);
}


struct tsc2101_position tsc2101_read_position() 
{
	uint32_t d[3];
	uint32_t c = 0x8000 + (0 << 11) + (0 << 5);
	spi2.read(&spi2, c, d, 3);

	struct tsc2101_position page = {
		.x = d[0],
		.y = d[1],
		.z = d[2],
	};

	return page;
}


