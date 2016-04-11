/** >copyright & heading...>
*/

/* Export public symbols */
		.global main, res, var2, i

/* Declaration of the constants */
LOOPS = 10

/* Declaration of initialized variables */
		.data
		.align	8
res:	.long	0
var2:	.short	30

/* Declaration of uninitializes variables */
		.bss
		.align 	8
i:		.space	4

/* Implementation of assembler functions and methods */
		.text
main:	nop

		mov		r0, #LOOPS	// attribute LOOPS to r0
		ldr		r1,	=var2	// save var2's address in r1
		ldrh	r1,	[r1]	// find var2's address and take the value
		ldr		r3,	=res	// save res's address in r3
		ldr		r4,	=i		// save i's address in r4
		mov		r5,	#0		// attribute 0 to r5
		str		r5,	[r4]	// save r5's value in the memory case r4

next:	ldr		r2,	[r3]	// load the r3 memory case in the register r2
		add		r2,	r1		// increment r2 by r1
		str		r2,	[r3]	// save r2's value in the memory case r3
		ldr		r5,	[r4]	// load the r3 memory case in the register r2
		add		r5,	#1		// increment r5 by 1
		str		r5,	[r4]	// save r5's value in the memory case r4
		cmp		r5,	r0		// condition r5 = r0
		bne		next		// condition not equal, go to next

1:		nop
		b		1b			// go to 1 (infinity loop)
