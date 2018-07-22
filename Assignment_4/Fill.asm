// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

@SCREEN
D=A
@TOP
M=D

@KBD
D=A
@BOTTOM
M=D

@TOP
D=M
@J
M=D

(LOOP)
	@KBD
	D=M
	@WHITE
	D;JEQ
	@BLACK
	0;JMP
(BLACK)
	@BOTTOM
	D=M
	@J
	D=D-M
	@LOOP
	D;JEQ
	@J
	A=M
	M=-1
	@J
	M=M+1
	@LOOP
	0;JMP
(WHITE)
	@TOP
	D=M
	@J
	D=D-M
	@LOOP
	D;JEQ
	@J
	M=M-1
	A=M
	M=0
	@LOOP
	0;JMP