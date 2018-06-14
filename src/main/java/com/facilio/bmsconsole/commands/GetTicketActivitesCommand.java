package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetTicketActivitesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long ticketId = (long) context.get(FacilioConstants.ContextNames.TICKET_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = FieldFactory.getTicketActivityFields();
		if(ticketId != -1) {
			FacilioModule module = ModuleFactory.getTicketActivityModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCustomWhere("TICKET_ID = ?", ticketId)
															.orderBy("MODIFIED_TIME desc");
			
			long portalID =  AccountUtil.getCurrentUser().getPortalId();
			if (portalID > 0)
			{
				Condition con = new Condition();
				int index = fields.indexOf("activityType");
				if( index > -1) {
					con.setField(fields.get(index));
					con.setOperator(StringOperators.IS);
					con.setValue(ActivityType.CLOSE_WORK_ORDER.getValue() + "," + ActivityType.SOLVE_WORK_ORDER.getValue() + "," + ActivityType.ADD_TICKET_NOTE.getValue() + "," + ActivityType.EDIT.getValue());
					selectBuilder.andCondition(con);
				}
			}
			List<Map<String, Object>> activityProps = selectBuilder.get();
			if(activityProps != null && !activityProps.isEmpty()) {
				List<TicketActivity> activities = new ArrayList<>();
				for(Map<String, Object> prop : activityProps) {
					TicketActivity checkIsNotify = FieldUtil.getAsBeanFromMap(prop, TicketActivity.class);
					if (portalID > 0) {
						if (checkIsNotify.getActivityType() == ActivityType.ADD_TICKET_NOTE.getValue())
						{
							if (checkIsNotify.getInfo().get("notifyRequester") != null) {
								if ((boolean) checkIsNotify.getInfo().get("notifyRequester")) {
									activities.add(checkIsNotify);
								}
							}
						}
						else {
							activities.add(checkIsNotify);
						}
					}
					else {
						activities.add(checkIsNotify);
					}
				}
				List<Long> ids = activities.stream().map(activity -> activity.getModifiedBy()).collect(Collectors.toList());
				if (ids.size() > 0) {
					List<User> userList = AccountUtil.getUserBean().getUsers(null, ids);
					Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), 
													(prevValue, curValue) -> { return prevValue; }));
					if (userList != null) {
						for (TicketActivity activi : activities) {
							activi.setName(userMap.get(activi.getModifiedBy()).getName());
						}
					}
				}
				context.put(FacilioConstants.TicketActivity.TICKET_ACTIVITIES, activities);
			}
															
		}
		return false;
	}
	
}
