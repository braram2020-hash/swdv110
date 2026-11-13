package edu.cwi.swdv110.diskusage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AlgorithmsTest {

    private static final List<FileInfo> FILES = List.of(
        new FileInfo("b.log", 50),
        new FileInfo("docs/img/cat.png", 2000),
        new FileInfo("a.txt", 100),
        new FileInfo("z.txt", 100),
        new FileInfo("docs/notes.txt", 300));

    @Test
    void selectionSortIntegersAndDoesNotModifyInput() {
        List<Integer> input = new ArrayList<>(List.of(5, 3, 9, 1, 3));
        assertEquals(List.of(1, 3, 3, 5, 9), Algorithms.selectionSort(input, Comparator.naturalOrder()));
        assertEquals(List.of(5, 3, 9, 1, 3), input, "input list must not change");
    }

    @Test
    void mergeSortIntegers() {
        assertEquals(List.of(), Algorithms.mergeSort(List.<Integer>of(), Comparator.naturalOrder()));
        assertEquals(List.of(7), Algorithms.mergeSort(List.of(7), Comparator.naturalOrder()));
        assertEquals(List.of(9, 5, 3, 3, 1), Algorithms.mergeSort(List.of(5, 3, 9, 1, 3), Comparator.reverseOrder()));
    }

    @Test
    void mergeSortMatchesBuiltInOnRandomData() {
        Random rnd = new Random(17);
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 2_000; i++) {
            data.add(rnd.nextInt(500));
        }
        List<Integer> expected = new ArrayList<>(data);
        expected.sort(null);
        assertEquals(expected, Algorithms.mergeSort(data, Comparator.naturalOrder()));
        assertEquals(expected, Algorithms.selectionSort(data, Comparator.naturalOrder()));
    }

    @Test
    void mergeSortIsStable() {
        List<FileInfo> sorted = Algorithms.mergeSort(FILES, Comparator.comparingLong(FileInfo::size));
        assertEquals(List.of("b.log", "a.txt", "z.txt", "docs/notes.txt", "docs/img/cat.png"),
            sorted.stream().map(FileInfo::path).toList());
    }

    @Test
    void largest() {
        assertEquals(List.of(new FileInfo("docs/img/cat.png", 2000),
            new FileInfo("docs/notes.txt", 300),
            new FileInfo("a.txt", 100)), Algorithms.largest(FILES, 3));
        assertEquals(5, Algorithms.largest(FILES, 50).size());
    }

    @Test
    void binarySearch() {
        List<FileInfo> byPath = Algorithms.mergeSort(FILES, Algorithms.BY_PATH);
        assertEquals(0, Algorithms.binarySearchByPath(byPath, "a.txt"));
        assertEquals(3, Algorithms.binarySearchByPath(byPath, "docs/notes.txt"));
        assertEquals(4, Algorithms.binarySearchByPath(byPath, "z.txt"));
        assertEquals(-1, Algorithms.binarySearchByPath(byPath, "missing.txt"));
        assertEquals(-1, Algorithms.binarySearchByPath(List.of(), "a.txt"));
    }
}