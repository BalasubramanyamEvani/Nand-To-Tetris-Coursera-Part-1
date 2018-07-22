//code for multiplication
//INPUT: R0 AND R1
//OUTPUT: R2

@R2	//R2=0
M=0

@R0	//D=R0
D=M
	
@I	//I=R0
M=D

(START)		//START PROCEDURE
	@I
	D=M	//D=I
	@LOOP	//START LOOP
	D;JGT	//IF D >= 0 JUMP
	
	(STOP)		//ELSE STOP
		@STOP
		0;JMP

(LOOP)
	@R1
	D=M //D=R1
	@R2
	M=M+D  //R2=R2+R1
	@I	//DECREMENT I
	M=M-1

	@START	//CONTINUE LOOP
	0;JMP