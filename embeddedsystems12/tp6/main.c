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
 * Abstract: 	TP6 - Interrupt handling demo and test program
 *
 * Purpose:	Main module to demonstrate and to test the i.MX27 
 *              interrupt handling.
 *
 * Author: 	Fabio Valverde & Samuel Mertenat
 * Date: 	09.03.15
 */

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>

#include "interrupt.h"
#include "exception.h"

/* ------------------------------------------------------------------------- */

int main () {
	printf ("\n");
	printf ("EIA-FR - Embedded Systems 2 Laboratory\n");
	printf ("TP6: Low Level Interrupt Handling on i.MX27\n");
	printf ("-------------------------------------------\n");

	interrupt_init();
	exception_init();

	printf ("Test data abort with a miss aligned access\n");
	uint32_t l = 0;
	uint32_t * pl = (uint32_t*)(char*) &l + 1;
	printf("%d", *pl);

	printf ("\nTest software interrupt\n");
	__asm__("SWI #1;");	//	generate SWI

	printf ("Test a invalid instruction\n");
	__asm__(".word 0xffffffff;");	// undefined interrupt

	printf ("\nTest a prefetch abort\nThis method will never return...\n");
	__asm__("mov pc, #0x0f000000;");	//	prefetch abort interrupt

	while(1);
	return 0;
}
