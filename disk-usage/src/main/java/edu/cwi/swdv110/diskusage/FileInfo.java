package edu.cwi.swdv110.diskusage;

/** One file: its path relative to the scanned folder (always using '/') and its size in bytes. */
public record FileInfo(String path, long size) { }