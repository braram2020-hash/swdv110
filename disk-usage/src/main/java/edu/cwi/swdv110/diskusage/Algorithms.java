package edu.cwi.swdv110.diskusage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public final class Algorithms {

    public static final Comparator<FileInfo> BY_SIZE_DESC =
        Comparator.comparingLong(FileInfo::size).reversed().thenComparing(FileInfo::path);

    public static final Comparator<FileInfo> BY_PATH =
        Comparator.comparing(FileInfo::path);

    private Algorithms() { }


    public static <T> List<T> selectionSort(List<T> list, Comparator<T> cmp) {
        List<T> a = new ArrayList<>(list);
        for (int i = 0; i < a.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < a.size(); j++) {
                if (cmp.compare(a.get(j), a.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            T temp = a.get(i);
            a.set(i, a.get(minIndex));
            a.set(minIndex, temp);
        }
        return a;
    }


    public static <T> List<T> mergeSort(List<T> list, Comparator<T> cmp) {
        if (list.size() <= 1) {
            return new ArrayList<>(list);
        }
        int mid = list.size() / 2;
        List<T> left = mergeSort(list.subList(0, mid), cmp);
        List<T> right = mergeSort(list.subList(mid, list.size()), cmp);
        return merge(left, right, cmp);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<T> cmp) {
        List<T> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (cmp.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }


    public static int binarySearchByPath(List<FileInfo> sortedByPath, String path) {
        return binarySearch(sortedByPath, path, 0, sortedByPath.size() - 1);
    }

    private static int binarySearch(List<FileInfo> a, String path, int low, int high) {
        if (low > high) {
            return -1;
        }
        int mid = low + (high - low) / 2;
        int cmp = a.get(mid).path().compareTo(path);
        if (cmp == 0) {
            return mid;
        } else if (cmp > 0) {
            return binarySearch(a, path, low, mid - 1);
        } else {
            return binarySearch(a, path, mid + 1, high);
        }
    }


    public static List<FileInfo> largest(List<FileInfo> files, int n) {
        List<FileInfo> sorted = mergeSort(files, BY_SIZE_DESC);
        List<FileInfo> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sorted.size()); i++) {
            result.add(sorted.get(i));
        }
        return result;
    }
}