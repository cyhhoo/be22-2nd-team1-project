package com.mycompany.project.user.command.domain.aggregate;

public enum AdminLevel {
        LEVEL_1("1"),
        LEVEL_5("5");

        private final String value;

        AdminLevel(String value) {
            this.value = value;
        }
    }