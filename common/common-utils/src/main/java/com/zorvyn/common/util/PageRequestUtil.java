package com.zorvyn.common.util;

public final class PageRequestUtil {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    private PageRequestUtil() {}

    public static int validPage(int page) {
        return Math.max(page, DEFAULT_PAGE);
    }

    public static int validSize(int size) {
        return Math.min(size, MAX_SIZE);
    }

    public static int defaultSize() {
        return DEFAULT_SIZE;
    }
}
