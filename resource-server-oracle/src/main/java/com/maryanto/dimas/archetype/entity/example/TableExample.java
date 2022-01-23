package com.maryanto.dimas.archetype.entity.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableExample {

    private String id;
    private String name;
    private Date createdDate;
    private Timestamp createdTime;
    private Boolean active;
    private Integer counter;
    private BigDecimal currency;
    private String description;
    private Double floating;
}
