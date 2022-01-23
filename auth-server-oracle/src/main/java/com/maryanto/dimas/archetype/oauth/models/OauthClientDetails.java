package com.maryanto.dimas.archetype.oauth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"redirectUrls", "oauthGrantTypes", "oauthScopes", "applications", "authorities"})
public class OauthClientDetails implements Serializable {

    private String id;
    private String name;
    private String password;
    private boolean autoApprove;
    private Integer expiredInSecond;
    private String createdBy;
    private Timestamp createdDate;
    private String lastUpdateBy;
    private Timestamp lastUpdateDate;
    private List<String> redirectUrls;
    private List<String> authorities;
    private List<OauthGrantType> oauthGrantTypes;
    private List<OauthScope> oauthScopes;
    private List<OauthApplication> applications;

}
