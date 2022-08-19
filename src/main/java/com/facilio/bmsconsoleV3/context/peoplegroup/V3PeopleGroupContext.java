package com.facilio.bmsconsoleV3.context.peoplegroup;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;

import java.util.List;
@Setter@Getter
public class V3PeopleGroupContext extends V3Context {

    private long groupId =-1;
    private String name;
    private String email;
    private String description;
    private long createdTime = -1;
    private long createdBy = -1;
    private long parent = -1;
    private String phone;
    private String groupMembersEmail;
    private String groupMembersPhone;
    private String groupMembersIds;
    private List<V3PeopleGroupMemberContext> members;
    private Boolean isActive;

    public long getGroupId() {
        return getId();
    }

    public boolean isActive() {
        return isActive != null && isActive.booleanValue();
    }

    @JSON(serialize=false)
    public String getGroupMembersEmail() {
        return groupMembersEmail;
    }

    @JSON(serialize=false)
    public String getGroupMembersPhone() {
        return groupMembersPhone;
    }

    @JSON(serialize=false)
    public String getGroupMembersIds() {
        return groupMembersIds;
    }
}
