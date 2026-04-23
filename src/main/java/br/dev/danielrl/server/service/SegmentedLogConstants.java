package br.dev.danielrl.server.service;

public final class SegmentedLogConstants {

    private SegmentedLogConstants() {
    }

    public static final long SEGMENT_ROTATION_MS = 10_000L;
    public static final String DATA_DIR = "data";
    public static final String SEGMENTS_DIR = DATA_DIR + "/segments";
    public static final String CONSOLIDATED_DIR = DATA_DIR + "/consolidated";

    public static long alignWindowStart(long timestamp) {
        return (timestamp / SEGMENT_ROTATION_MS) * SEGMENT_ROTATION_MS;
    }

    public static long windowEnd(long windowStart) {
        return windowStart + SEGMENT_ROTATION_MS - 1;
    }
}