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
 * Abstract: 	ARM Exception Handling 
 *
 * Author: 	Fabio Valverde & Samuel Mertenat
 * Date: 	07.03.15
 */

#include <stdio.h>

#include "interrupt.h"

static void exception_handler(void* addr, enum interrupt_vectors vector, void* param) {
	printf("Exception handled : %s\n", (char*)param);
	while(vector == INT_PREFETCH_ABORT) {
	}
}

void exception_init() {
	interrupt_attach(INT_UNDEFINED, exception_handler, "Undefined instruction");
	interrupt_attach(INT_PREFETCH_ABORT, exception_handler, "Prefetch abort");
	interrupt_attach(INT_DATA_ABORT, exception_handler, "Data abort");
	interrupt_attach(INT_SWI, exception_handler, "Software interrupt");
}



