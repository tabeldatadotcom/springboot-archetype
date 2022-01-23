package com.maryanto.dimas.archetype.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Serializable {

    private String id;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private boolean locked;
    private boolean sudo;
    private boolean alwaysActive;
    private Integer loginFailedTimes;
    private String createdBy;
    private Timestamp createdDate;
    private String lastUpdateBy;
    private Timestamp lastUpdateDate;
}
