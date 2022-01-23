package com.maryanto.dimas.archetype.oauth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OauthApplication implements Serializable {

    private String id;
    private String name;
    private String createdBy;
    private Timestamp createdDate;
    private String lastUpdateBy;
    private Timestamp lastUpdateDate;
}
