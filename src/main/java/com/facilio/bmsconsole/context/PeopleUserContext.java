package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.Role;
import com.facilio.identity.client.dto.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
public class PeopleUserContext {
    private long id;
    private long orgUserId;
    @Deprecated
    private long iamOrgUserId;
    private long uid;
    private long peopleId;
    private long applicationId;
    private long roleId;
    private Long scopingId;
    private User user;
    private PeopleContext people;
    private Role role;
    private List<Long> accessibleSpace;
    private List<Long> groups;

}
