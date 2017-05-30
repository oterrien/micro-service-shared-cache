package com.ote.test;

import lombok.Data;

import java.util.List;

@Data
public class MyPage<T> {

    private List<T> content;
    private boolean last;
    private int totalPages;
    private int totalElements;
    private int size;
    private int number;
    private List<Sort> sort;
    private boolean first;
    private int numberOfElements;

    @Data
    public static class Sort {

        private String direction;
        private String property;
        private boolean ignoreCase;
        private String nullHandling;
        private boolean ascending;
        private boolean descending;
    }
}
