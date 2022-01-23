package com.maryanto.dimas.archetype.dto.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TableExampleDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class New {

        @NotNull
        @NotEmpty
        private String name;
        @NotNull
        private boolean active;
        @NotNull
        @Min(1)
        private Integer counter;
        @NotNull
        @Min(0)
        private BigDecimal currency;
        private String description;
        private Double floating;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update {

        @NotNull
        @NotEmpty
        private String id;
        @NotNull
        @NotEmpty
        private String name;
        @NotNull
        private boolean active;
        @NotNull
        @Min(1)
        private Integer counter;
        @NotNull
        @Min(0)
        private BigDecimal currency;
        private String description;
        private Double floating;
    }
}
