package edu.cwi.swdv110.diskusage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileScannerTest {

    @TempDir
    Path tmp;
    File root;

    private static void makeFile(Path p, int bytes) throws IOException {
        Files.createDirectories(p.getParent());
        Files.write(p, new byte[bytes]);
    }

    @BeforeEach
    void setUp() throws IOException {
        Path r = tmp.resolve("root");
        makeFile(r.resolve("a.txt"), 100);
        makeFile(r.resolve("b.log"), 50);
        makeFile(r.resolve("docs/notes.txt"), 300);
        makeFile(r.resolve("docs/img/cat.png"), 2000);
        Files.createDirectories(r.resolve("empty"));
        root = r.toFile();
    }

    // ---------- Part 1 ----------
    @Test
    void totalSize() {
        assertEquals(2450, FileScanner.totalSize(root));
        assertEquals(2300, FileScanner.totalSize(new File(root, "docs")));
        assertEquals(100, FileScanner.totalSize(new File(root, "a.txt")));
        assertEquals(0, FileScanner.totalSize(new File(root, "empty")));
    }

    @Test
    void countFiles() {
        assertEquals(4, FileScanner.countFiles(root));
        assertEquals(1, FileScanner.countFiles(new File(root, "a.txt")));
        assertEquals(0, FileScanner.countFiles(new File(root, "empty")));
    }

    @Test
    void maxDepth() {
        assertEquals(3, FileScanner.maxDepth(root));
        assertEquals(2, FileScanner.maxDepth(new File(root, "docs")));
        assertEquals(1, FileScanner.maxDepth(new File(root, "docs/img")));
        assertEquals(0, FileScanner.maxDepth(new File(root, "empty")));
        assertEquals(0, FileScanner.maxDepth(new File(root, "a.txt")));
    }

    // ---------- Part 2 ----------
    @Test
    void listAllUsesRelativePathsInNameOrder() {
        assertEquals(List.of(
            new FileInfo("a.txt", 100),
            new FileInfo("b.log", 50),
            new FileInfo("docs/img/cat.png", 2000),
            new FileInfo("docs/notes.txt", 300)), FileScanner.listAll(root));
    }

    @Test
    void findByExtension() {
        assertEquals(List.of(new FileInfo("a.txt", 100), new FileInfo("docs/notes.txt", 300)),
            FileScanner.findByExtension(root, "TXT"));
        assertEquals(List.of(), FileScanner.findByExtension(root, "java"));
    }

    @Test
    void humanSize() {
        assertEquals("0 B", FileScanner.humanSize(0));
        assertEquals("999 B", FileScanner.humanSize(999));
        assertEquals("1.0 KB", FileScanner.humanSize(1024));
        assertEquals("1.5 KB", FileScanner.humanSize(1536));
        assertEquals("1.0 MB", FileScanner.humanSize(1024 * 1024));
        assertEquals("2.5 GB", FileScanner.humanSize(2_684_354_560L));
    }

    // ---------- Part 3 ----------
    @Test
    void treeFullDepth() {
        String nl = System.lineSeparator();
        String expected =
            "root/ (2.4 KB)" + nl
            + " a.txt (100 B)" + nl
            + " b.log (50 B)" + nl
            + " docs/ (2.2 KB)" + nl
            + " img/ (2.0 KB)" + nl
            + " cat.png (2.0 KB)" + nl
            + " notes.txt (300 B)" + nl
            + " empty/ (0 B)" + nl;
        assertEquals(expected, FileScanner.tree(root, 10));
    }

    @Test
    void treeStopsAtMaxDepth() {
        String nl = System.lineSeparator();
        String expected =
            "root/ (2.4 KB)" + nl
            + " a.txt (100 B)" + nl
            + " b.log (50 B)" + nl
            + " docs/ (2.2 KB)" + nl
            + " empty/ (0 B)" + nl;
        assertEquals(expected, FileScanner.tree(root, 1));
    }
}