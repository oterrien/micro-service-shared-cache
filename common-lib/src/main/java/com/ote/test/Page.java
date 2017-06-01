package com.ote.test;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Page<T> {

    private List<T> content;

    private boolean last;
    private int totalElements;
    private int totalPages;
    private int size;
    private int number;
    private List<Sort> sort;
    private boolean first;
    private int numberOfElements;

    @Data
    private static class Sort{
        private String direction;
        private String property;
        private boolean ignoreCase;
        private String nullHandling;
        private boolean ascending;
        private boolean descending;
    }
}
