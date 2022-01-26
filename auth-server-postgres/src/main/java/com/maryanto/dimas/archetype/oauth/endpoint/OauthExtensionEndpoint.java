package com.maryanto.dimas.archetype.oauth.endpoint;

import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenExtended;
import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenHistory;
import com.maryanto.dimas.archetype.oauth.repository.JdbcTokenStoreCustom;
import com.maryanto.dimas.archetype.oauth.service.DefaultTokenService;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@FrameworkEndpoint
@RequestMapping("/api/oauth")
@Slf4j
public class OauthExtensionEndpoint {

    @Autowired
    private DefaultTokenService tokenServices;
    @Autowired
    private JdbcTokenStoreCustom tokenStore;

    @ResponseBody
    @PostMapping("/revoke")
    public ResponseEntity<?> revokeToken(
            @RequestBody Map<String, Object> params,
            @RequestParam String logout,
            @RequestParam String clientId,
            Authentication auth) {
        if (tokenStore.findTokensByClientIdAndUserName(clientId, logout).isEmpty()) {
            return noContent().build();
        }

        tokenServices.revokeTokenByUsername(
                params.get("access_token").toString(),
                auth != null ? auth.getName() : "annonymous");

        return ok().build();
    }

    @ResponseBody
    @PostMapping("/logout")
    public ResponseEntity<?> oauthLogout(
            @RequestBody Map<String, Object> params,
            @RequestParam String clientId,
            Authentication auth) {
        String username = auth != null ? auth.getName() : "annonymous";
        if (tokenStore.findTokensByClientIdAndUserName(clientId, username).isEmpty()) {
            return noContent().build();
        }

        tokenServices.revokeTokenByUsername(
                params.get("access_token").toString(),
                username);
        return ok().build();
    }

    @PostMapping("/token/current/datatables")
    @ResponseBody
    public DataTablesResponse<OauthAccessTokenExtended> dataTablesCurrentUsersLogon(
            @RequestParam(required = false, value = "draw", defaultValue = "0") Long draw,
            @RequestParam(required = false, value = "start", defaultValue = "0") Long start,
            @RequestParam(required = false, value = "length", defaultValue = "10") Long length,
            @RequestParam(required = false, value = "order[0][column]", defaultValue = "1") Long iSortCol0,
            @RequestParam(required = false, value = "order[0][dir]", defaultValue = "asc") String sSortDir0,
            @RequestBody(required = false) OauthAccessTokenExtended oauth) {
        if (oauth == null) oauth = new OauthAccessTokenExtended();
        log.info("draw: {}, start: {}, length: {}, type: {}", draw, start, length, oauth);
        return tokenServices.datatables(new DataTablesRequest<>(draw, length, start, sSortDir0, iSortCol0, oauth));
    }

    @PostMapping("/token/history/datatables/byUsernameAndClientId")
    @ResponseBody
    public DataTablesResponse<OauthAccessTokenHistory> dataTablesHistoryByUserAndClientId(
            @RequestParam(required = false, value = "draw", defaultValue = "0") Long draw,
            @RequestParam(required = false, value = "start", defaultValue = "0") Long start,
            @RequestParam(required = false, value = "length", defaultValue = "10") Long length,
            @RequestParam(required = false, value = "order[0][column]", defaultValue = "0") Long iSortCol0,
            @RequestParam(required = false, value = "order[0][dir]", defaultValue = "asc") String sSortDir0,
            @RequestParam(value = "userName", required = true) String username,
            @RequestParam(value = "clientId", required = true) String clientId,
            @RequestBody(required = false) OauthAccessTokenHistory oauth) {
        if (oauth == null) oauth = new OauthAccessTokenHistory();
        log.info("draw: {}, start: {}, length: {}, type: {}", draw, start, length, oauth);
        return tokenServices.historyByUserAndClientIdDatatables(
                username,
                clientId,
                new DataTablesRequest<>(draw, length, start, sSortDir0, iSortCol0, oauth)
        );
    }

    @PostMapping("/token/history/datatables/byUsername")
    @ResponseBody
    public DataTablesResponse<OauthAccessTokenHistory> dataTablesHistoryByUser(
            @RequestParam(required = false, value = "draw", defaultValue = "0") Long draw,
            @RequestParam(required = false, value = "start", defaultValue = "0") Long start,
            @RequestParam(required = false, value = "length", defaultValue = "10") Long length,
            @RequestParam(required = false, value = "order[0][column]", defaultValue = "0") Long iSortCol0,
            @RequestParam(required = false, value = "order[0][dir]", defaultValue = "asc") String sSortDir0,
            @RequestParam(value = "userName", defaultValue = "asc") String username,
            @RequestBody(required = false) OauthAccessTokenHistory oauth) {
        if (oauth == null) oauth = new OauthAccessTokenHistory();
        log.info("draw: {}, start: {}, length: {}, type: {}", draw, start, length, oauth);
        return tokenServices.historyByUserDatatables(
                username,
                new DataTablesRequest<>(draw, length, start, sSortDir0, iSortCol0, oauth)
        );
    }

    @PostMapping("/token/history/datatables/byClientId")
    @ResponseBody
    public DataTablesResponse<OauthAccessTokenHistory> dataTablesHistoryByClientId(
            @RequestParam(required = false, value = "draw", defaultValue = "0") Long draw,
            @RequestParam(required = false, value = "start", defaultValue = "0") Long start,
            @RequestParam(required = false, value = "length", defaultValue = "10") Long length,
            @RequestParam(required = false, value = "order[0][column]", defaultValue = "0") Long iSortCol0,
            @RequestParam(required = false, value = "order[0][dir]", defaultValue = "asc") String sSortDir0,
            @RequestParam(value = "clientId", defaultValue = "asc") String clientId,
            @RequestBody(required = false) OauthAccessTokenHistory oauth) {
        if (oauth == null) oauth = new OauthAccessTokenHistory();
        log.info("draw: {}, tart: {}, length: {}, type: {}", draw, start, length, oauth);
        return tokenServices.historyByClientIdDatatables(
                clientId,
                new DataTablesRequest<>(draw, length, start, sSortDir0, iSortCol0, oauth)
        );
    }

}
