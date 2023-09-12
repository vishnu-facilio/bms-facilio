package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PeopleUserContextExtendedProps extends PeopleUserContext {
    @Getter @Setter
    public static class RoleAppScopingProps{
        private long roleId;
        private long scopingId;
        private long applicationId;

        private Role role;
        private ApplicationContext applicationContext;
    }

    private List<RoleAppScopingProps> roleAppScopingProps;
}

