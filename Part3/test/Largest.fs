BEGINPROG Largest

/* Compare three numbers and print the largest one */

READ(number1)             // Read first number from user input
READ(number2)             // Read second number from user input
READ(number3)             // Read third number from user input

IF (number1 > number2) THEN
    IF (number1 > number3) THEN
        largest := number1
    ELSE
         largest := number3
    ENDIF
ELSE
    IF (number2 > number1) THEN
         IF (number2 > number3) THEN
            largest := number2
         ELSE
            largest := number3
         ENDIF
    ENDIF
ENDIF

PRINT(largest)
ENDPROG
