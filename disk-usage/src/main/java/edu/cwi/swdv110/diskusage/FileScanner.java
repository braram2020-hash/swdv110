package edu.cwi.swdv110.diskusage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public final class FileScanner {

    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB"};

    private FileScanner() { }

    static File[] sortedChildren(File dir) {
        File[] kids = dir.listFiles();
        if (kids == null) {
            return new File[0];
        }
        Arrays.sort(kids, Comparator.comparing(File::getName));
        return kids;
    }




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


    public static List<FileInfo> listAll(File root) {
        List<FileInfo> result = new ArrayList<>();
        collect(root, "", result);
        return result;
    }


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


    public static String tree(File root, int maxDepth) {
        StringBuilder sb = new StringBuilder();
        tree(root, 0, maxDepth, sb);
        return sb.toString();
    }

    private static void tree(File f, int depth, int maxDepth, StringBuilder sb) {
    
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