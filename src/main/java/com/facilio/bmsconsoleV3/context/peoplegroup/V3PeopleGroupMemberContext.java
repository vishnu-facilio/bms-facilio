package com.facilio.bmsconsoleV3.context.peoplegroup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class V3PeopleGroupMemberContext extends V3Context {

    private long memberId;
    private V3PeopleGroupContext group;
    private long ouid;
    private int memberRole;
    private V3PeopleContext people;
    private String email;
    private String phone;
    private String mobile;
    private List<Long> accessibleSpace;

    public int getMemberRole() {
        return memberRole;
    }
    public AccountConstants.GroupMemberRole getMemberRoleEnum() {
        return AccountConstants.GroupMemberRole.getGroupMemberRole(getMemberRole());
    }
    public void setMemberRole(int memberRole) {
        this.memberRole = memberRole;
    }
    public void setMemberRole(AccountConstants.GroupMemberRole memberRole) {
        this.memberRole = memberRole.getMemberRole();
    }
}
