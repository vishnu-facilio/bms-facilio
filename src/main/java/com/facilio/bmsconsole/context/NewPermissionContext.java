package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.NewPermission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPermissionContext extends NewPermission {
    private long id;
    @JsonIgnore
    private WebTabContext webTabContext;
    @JsonIgnore
    private ApplicationContext applicationContext;
}
