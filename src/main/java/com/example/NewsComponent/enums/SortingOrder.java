package com.example.NewsComponent.enums;

public enum SortingOrder {

        ASC("ASC"),
        DESC("DESC");

        private final String value;

        SortingOrder(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

