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
 * Project:	EIA-FR / Embedded Systems 1 Laboratory
 *
 * Abstract: 	TP2 - An introduction the ARM's assembler language
 *
 * Purpose:	Program to convert an ascii string in uppercase.
 *
 * Author: 	Samuel Mertenat
 * Date: 	11/10/2014
 */

/* Export public symbols */
	.global main, source, dest, size

/* Constants declaration */
MAX	= 50
OFFSET = -32 // 'a' to 'A' = -32 (table ascii)


/* Initialized variables declation */
	.data
	.align 8
source:	.asciz "Halli haolo><)(7]%&  HjugftRt%z   `ab yz{\n"
@@@             123456789012345678901234567890123456789012


/* Uninitialized variables declation */
	.bss
	.align 8
size:	.space 	2
dest:	.space	MAX


/* Implementation of assembler functions and methods */
	.text
main:	nop
		ldr	r1, =source 		// charge l'adresse de source dans r1
		ldr	r0, =dest 			// charge l'adresse de dest dans r0

		ldr r2, =MAX 			// charge l'adresse de MAX dans r2
		ldr r3, =size 			// charge l'adresse de size dans r3

		mov r5, #0				// initie r5 à 0; taille de la chaîne de caract.

check:	cmp r2, #0 				// vérifie que l'on n'a pas atteind la taille max de caractères
		beq 1f					// si nbre de caract. max est atteind, on saute à 1:
		ldrb r4, [r1], #1 		// charge un caractère de la source, indexé de 1
		cmp r4, #0				// compare si le caract. chargé est "null"
		strb r4, [r0], #1		// enregistre le caract. "null" en bout de chaîne
		beq 1f					// si plus de caract., on saute à 1:

loop:	cmp r4, #97 			// compare le caractère chargé avec 'a' / 97
		blo	save				// si plus petit, on l'enregistre directement (donc en maj. ou autre, !minuscule)
		cmp r4, #122			// compare le caractère chargé avec 'z' / 122
		bhi save				// si plus grand, on l'enregistre directement (!minuscule)
		add r4, #OFFSET			// donc minuscule. On ajoute -32 pour le passer en majuscule

save:	strb r4, [r0], #1		// sauvegarde dans r0 la valeur de r4, indexée de 1
		sub r2, #1 				// soustrait 1 à r2 (valeur max de caractères à traiter)
		add r5, #1				// ajoute 1 à la chaîne de caract.
		b check

1:		strh r5, [r3]			// sauvegarde la taille de chaîne dans r3 (size)
		bl imx27_serial_puts	// affiche les caract. dans la console
		nop
		b	1b
		bx	lr
