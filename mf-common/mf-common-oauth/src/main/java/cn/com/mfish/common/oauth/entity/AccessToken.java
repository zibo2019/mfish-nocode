package cn.com.mfish.common.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;


import java.io.Serializable;

/**
 * @author: mfish
 * @date: 2020/2/17 15:09
 */
@Schema(description = "返回accessToken信息")
public class AccessToken implements Serializable {
    @Schema(description = "token值")
    private String access_token;
    @Schema(description = "刷新token用于重新获取token")
    private String refresh_token;
    @Schema(description = "有效期")
    private Long expires_in;

    public AccessToken() {
    }

    public AccessToken(AccessToken token) {
        this.setAccess_token(token.getAccess_token()).setRefresh_token(token.getRefresh_token()).setExpires_in(token.getExpires_in());
    }

    public String getAccess_token() {
        return access_token;
    }

    public AccessToken setAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public AccessToken setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
        return this;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public AccessToken setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
        return this;
    }
}
