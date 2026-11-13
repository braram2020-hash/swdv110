package edu.cwi.swdv110.diskusage;

import java.io.File;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Usa el directorio actual o uno por defecto para el análisis
        File targetDir = new File(args.length > 0 ? args[0] : ".");
        System.out.println("=== ANALIZADOR DE DISCO (Disk Usage Analyzer) ===");
        System.out.println("Analizando directorio: " + targetDir.getAbsolutePath() + "\n");

        // 1. Totales y conteo
        long totalSize = FileScanner.totalSize(targetDir);
        int fileCount = FileScanner.countFiles(targetDir);
        int maxDepth = FileScanner.maxDepth(targetDir);

        System.out.println("--- Estadísticas Generales ---");
        System.out.println("Tamaño total: " + FileScanner.humanSize(totalSize));
        System.out.println("Cantidad de archivos: " + fileCount);
        System.out.println("Profundidad máxima: " + maxDepth + "\n");

        // 2. Árbol de directorios
        System.out.println("--- Estructura de Árbol (Nivel 3) ---");
        System.out.print(FileScanner.tree(targetDir, 3));

        // 3. Archivos más grandes (Largest)
        List<FileInfo> allFiles = FileScanner.listAll(targetDir);
        System.out.println("\n--- Los 3 archivos más grandes ---");
        List<FileInfo> largestFiles = Algorithms.largest(allFiles, 3);
        for (FileInfo f : largestFiles) {
            System.out.println("  * " + f.path() + " (" + FileScanner.humanSize(f.size()) + ")");
        }

        // 4. Búsqueda por extensión (.java o .txt)
        System.out.println("\n--- Búsqueda de archivos .java ---");
        List<FileInfo> javaFiles = FileScanner.findByExtension(targetDir, "java");
        System.out.println("Encontrados: " + javaFiles.size() + " archivos .java");

        // 5. Búsqueda Binaria
        if (!javaFiles.isEmpty()) {
            List<FileInfo> sortedByPath = Algorithms.mergeSort(javaFiles, Algorithms.BY_PATH);
            String searchTarget = javaFiles.get(0).path();
            int index = Algorithms.binarySearchByPath(sortedByPath, searchTarget);
            System.out.println("Búsqueda binaria de '" + searchTarget + "': Índice encontrado = " + index);
        }

        System.out.println("\n¡Análisis completado con éxito!");
    }
}