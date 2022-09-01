package com.facilio.bmsconsole.commands;


import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SetOuIdInPeopleGroupMemberCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Boolean isOldApi = (Boolean) context.getOrDefault("isOldApi",false);

		if (isOldApi){
			return false;
		}

		Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
		String moduleName = Constants.getModuleName(context);
		List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

		if(CollectionUtils.isNotEmpty(records)) {
			for(ModuleBaseWithCustomFields record : records){
				if(record.getSubForm() != null) {
					List<Map<String,Object>> list = record.getSubForm().get(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
					if(CollectionUtils.isNotEmpty(list)){
						for (Map<String, Object> groupMember : list) {
							groupMember.put("memberRole",AccountConstants.GroupMemberRole.MEMBER.getMemberRole());
							V3PeopleContext people = FieldUtil.getAsBeanFromMap((Map<String, Object>) groupMember.get(FacilioConstants.ContextNames.PEOPLE),V3PeopleContext.class);
							FacilioUtil.throwIllegalArgumentException(people == null ,"People does not exist.");
							groupMember.put("ouid", Objects.requireNonNull(PeopleAPI.getUserIdForPeople(people.getId())).get(0));
						}
					}
				}
			}
		}

		return false;
	}

}
