package com.maryanto.dimas.archetype.oauth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OauthAccessTokenHistory {

    private String username;
    private String clientId;
    private String ipAddress;
    private String accessToken;
    private Timestamp loginAt;
    private Timestamp expiredAt;
    private Boolean logout;
    private Timestamp logoutAt;
    private String logoutBy;
}
