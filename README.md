# Contact Cleaner Reflection

### 1. Inmutabilidad de los Strings
Los Strings en Java son inmutables, lo que significa que no pueden ser modificados una vez que son creados. `trim()` o `toLowerCase()` devuelven un String completamente nuevo en lugar de alterar la variable original. Si olvidas guardar el valor devuelto de vuelta en la variable, el texto no cambiará y causará errores de formato.

### 2. Diferencia entre `split("|")` y `split("\\|")`
`split()` trata su entrada como una expresión regular. En las expresiones regulares, el símbolo `|` significa "O" (OR). Si simplemente usas `split("|")`, el texto se divide en cada uno de los caracteres individuales. Al escaparlo con `split("\\|")`, fuerzas a Java a buscar el carácter literal `|`.

### 3. Uso de `StringBuilder`
Usamos `StringBuilder` en lugar de la concatenación normal de strings (`+`) dentro de los bucles para ahorrar memoria y mejorar el rendimiento. Hacer concatenación de strings repetidamente crea múltiples objetos temporales en segundo plano, mientras que `StringBuilder` nos permite modificar los caracteres directamente utilizando métodos como `setCharAt` y `append`.

### 4. Resultado de las pruebas de Maven