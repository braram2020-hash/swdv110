El SQL es una injection, es cuando alguien mete un texto con la sintaxis SQL en un campo normal para cambiar lo que hace la base de datos. Si en el apartado de search hubiera pegado el texto con + en vez de usar ?, con OR '1'='1 tendria que dejarlo asi

```sql
SELECT * FROM books WHERE lower(title) LIKE '%'' OR ''1''=''1%' OR lower(author) LIKE '%'' OR ''1''=''1%'
```

Cada metodo abre y cierra su propia conexion para que no queden conexiones colgadas usando try-with-resources y tambien para que cada una de estas empiece limpia[cite: 11, 12]. Si olvidas cerrarlas se van acumulando estos archivos abiertos en SQLite, lo que puede trabar la base de datos y pues dar fallos o usar recursos de mas

Es mucho mas facil cambiar como guardamos los datos despues sin tener que andar cambiando el menu de la consola. Tambien mantiene el codigo ordenado y mas facil de probar

Resumen de `queries.sql`:
- Todos los libros ordenados por año (DESC) y título: 10 filas
- Libros de Andy Weir ordenados por año: 3 filas
- Libros sin leer (`is_read = 0`) ordenados por título: 3 filas
- Libros con `'the'` en el título: 3 filas
- Conteo de libros por autor: 8 filas
- Promedio de rating: 1 fila (promedio: 4.43)
- `UPDATE` de `'Sapiens'` (`is_read = 1, rating = 4`): 1 fila actualizada
- Libros con `rating = 0`, `is_read = 0` y `year < 2000`: 1 fila
- `DELETE` de libros con `rating = 0`, `is_read = 0` y `year < 2000`: 1 fila borrada