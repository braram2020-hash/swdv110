package edu.cwi.swdv110.diskusage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Recursive folder walking. Files.walk() and other built-in walkers are off-limits. */
public final class FileScanner {

    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB"};

    private FileScanner() { }

    /**
     * GIVEN: the children of a folder sorted by name, or an empty array if the folder
     * can't be read (listFiles() returns null for protected folders – this prevents a crash).
     */
    static File[] sortedChildren(File dir) {
        File[] kids = dir.listFiles();
        if (kids == null) {
            return new File[0];
        }
        Arrays.sort(kids, Comparator.comparing(File::getName));
        return kids;
    }

    // ---------------- Part 1 ----------------

    /**
     * Base case: a file's size is its length().
     * Recursive case: a folder's size is the sum of the totalSize of each child.
     */
    public static long totalSize(File f) {
        if (f.isFile()) {
            return f.length();
        }
        long total = 0;
        for (File kid : sortedChildren(f)) {
            total += totalSize(kid);
        }
        return total;
    }

    /** Number of FILES (not folders) in f and everything under it. A single file counts as 1. */
    public static int countFiles(File f) {
        if (f.isFile()) {
            return 1;
        }
        int count = 0;
        for (File kid : sortedChildren(f)) {
            count += countFiles(kid);
        }
        return count;
    }

    /**
     * How many levels deep the nesting goes.
     * a file, or an empty folder -> 0
     * a folder that only contains files -> 1
     * otherwise -> 1 + the deepest child
     */
    public static int maxDepth(File f) {
        if (f.isFile() || sortedChildren(f).length == 0) {
            return 0;
        }
        int deepest = 0;
        for (File kid : sortedChildren(f)) {
            int depth = maxDepth(kid);
            if (depth > deepest) {
                deepest = depth;
            }
        }
        return 1 + deepest;
    }

    // ---------------- Part 2 ----------------

    /**
     * Every file under root, in name order at each level, with paths relative to root:
     * "a.txt", "docs/img/cat.png", "docs/notes.txt"
     */
    public static List<FileInfo> listAll(File root) {
        List<FileInfo> result = new ArrayList<>();
        collect(root, "", result);
        return result;
    }

    /** Recursive helper: adds the files in dir to out. prefix is the path so far, e.g. "docs/img/". */
    private static void collect(File dir, String prefix, List<FileInfo> out) {
        for (File kid : sortedChildren(dir)) {
            String kidPath = prefix.isEmpty() ? kid.getName() : prefix + "/" + kid.getName();
            if (kid.isFile()) {
                out.add(new FileInfo(kidPath, kid.length()));
            } else {
                collect(kid, kidPath, out);
            }
        }
    }

    /** Files whose name ends with "." + ext (ignore case), in listAll order. */
    public static List<FileInfo> findByExtension(File root, String ext) {
        List<FileInfo> matches = new ArrayList<>();
        String targetExt = "." + ext.toLowerCase(Locale.US);
        for (FileInfo info : listAll(root)) {
            if (info.path().toLowerCase(Locale.US).endsWith(targetExt)) {
                matches.add(info);
            }
        }
        return matches;
    }

    /**
     * Human-readable size, 1024 bytes per KB:
     * 0 -> "0 B", 999 -> "999 B", 1536 -> "1.5 KB", 1048576 -> "1.0 MB"
     */
    public static String humanSize(long bytes) {
        return humanSize(bytes, 0);
    }

    private static String humanSize(double value, int unit) {
        if (value < 1024 || unit >= UNITS.length - 1) {
            if (unit == 0) {
                return String.format(Locale.US, "%.0f %s", value, UNITS[unit]);
            } else {
                return String.format(Locale.US, "%.1f %s", value, UNITS[unit]);
            }
        }
        return humanSize(value / 1024.0, unit + 1);
    }

    // ---------------- Part 3 ----------------

    /**
     * An indented tree, one space per level, folders end with '/', each line shows the size.
     */
    public static String tree(File root, int maxDepth) {
        StringBuilder sb = new StringBuilder();
        tree(root, 0, maxDepth, sb);
        return sb.toString();
    }

    private static void tree(File f, int depth, int maxDepth, StringBuilder sb) {
        // Indentación de 1 espacio por cada nivel de profundidad
        String indent = (depth == 0) ? "" : " ";
        String name = f.getName() + (f.isDirectory() ? "/" : "");
        String sizeStr = humanSize(totalSize(f));
        
        sb.append(indent).append(name).append(" (").append(sizeStr).append(")").append(System.lineSeparator());

        if (f.isDirectory() && depth < maxDepth) {
            for (File kid : sortedChildren(f)) {
                tree(kid, depth + 1, maxDepth, sb);
            }
        }
    }
}