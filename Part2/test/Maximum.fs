BEGINPROG Maximum

/* Compare two numbers and print the bigger one */

  READ(number1)             // Read first number from user input
  READ(number2)             // Read second number from user input
  result := 0

IF (number1 > number2) THEN
    result := number1
ELSE
    result := number2
ENDIF
PRINT(result)
ENDPROG
