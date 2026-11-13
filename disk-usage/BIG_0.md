# Análisis de Complejidad - Disk Usage Analyzer

# 1. Métodos de FileScanner (Recorrido de archivos)
 **`totalSize` / `countFiles` / `maxDepth` — $O(N)$**
   Estos  recorren todo el árbol de carpetas de forma recursiva. Como estas tambien visitan cada archivo y directorio una sola vez, el tiempo crece de forma lineal a diferencia del número total de elementos ($N$).
 **`listAll` / `findByExtension` — $O(N)$**
  Recorren la estructura completa para armar la lista de archivos en orden alfabético, por lo que también toman un tiempo lineal.

# 2. Algoritmos de Búsqueda y Ordenamiento
 **`selectionSort` — $O(N^2)$**
   Cuando se usa dos ciclos anidados para buscar el elemento menor e ir acomodándolo, el número de comparaciones aumenta de forma cuadrática mientras la lista se hace mas grande.
 **`mergeSort` — $O(N \log N)$**
   Divide la lista recursivamente a la mitad (lo que da los niveles logarítmicos) y luego estos se van a ir fusionando a las mitades de manera ordenada linealmente, esto resulta más eficiente que el selection sort.
 **`binarySearchByPath` — $O(\log N)$**
   Como la lista ya está ordenada por ruta, la búsqueda va partiendo el espacio a la mitad en cada paso recursivo, encontrando el elemento muy rápido.