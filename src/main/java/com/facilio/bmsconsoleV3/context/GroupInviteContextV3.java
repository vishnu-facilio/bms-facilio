package com.facilio.bmsconsoleV3.context;

import java.util.List;

import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInviteContextV3 extends V3Context {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	List<InviteVisitorContextV3> groupChildInvites;
	private Long totalInvites;
	private Long checkedInCount;
	private Long notCheckedInCount;
	private Long visitorTypeId;
}
