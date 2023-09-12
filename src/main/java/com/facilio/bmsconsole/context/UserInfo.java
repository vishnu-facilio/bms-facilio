package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private String userName;
    private String identifier;

    public UserInfo(String userName, String identifier) {
        this.userName = userName;
        this.identifier = identifier;
    }
}
