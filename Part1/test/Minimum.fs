BEGINPROG Minimum

/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*
**************************************************
* Compare two numbers and print the smaller one  *
**************************************************
*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/

  READ(number1)             // Read first number from user input
  READ(number2)             // Read second number from user input
  result := 0

IF (number1 > number2) THEN
    result := number2
ELSE
    result := number1
ENDIF
PRINT(result)
ENDPROG
