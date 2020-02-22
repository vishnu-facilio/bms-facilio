package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.OccupantsContext;
import com.facilio.bmsconsole.context.OccupantsContext.OccupantType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class OccupantsAPI {

	
	public static void addUserAsRequester(OccupantsContext occupant) throws Exception {
//		User user = new User();
//		user.setEmail(occupant.getEmail());
//		user.setPhone(occupant.getPhone());
//		user.setName(occupant.getName());
//		user.setUserVerified(false);
//		user.setInviteAcceptStatus(false);
//		user.setInvitedTime(System.currentTimeMillis());
//
//		if(occupant.getOccupantType() == 1) {
//			user.setAppType(1);
//		}
//		else if(occupant.getOccupantType() == 2) {
//			user.setAppType(2);
//		}
//		else if(occupant.getOccupantType() == 3) {
//			user.setAppType(1);
//		}
//		long userId = AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false);
//		user.setId(userId);
//		occupant.setRequester(user);
	}
	
	public static void updatePortalUserAccess(OccupantsContext occupant, boolean updateRecord) throws Exception {
		
//		if(occupant != null && occupant.getRequester() != null && occupant.getRequester().getOuid() > 0) {
//			if(!occupant.isPortalAccessNeeded()) {
//				AccountUtil.getUserBean().disableUser(occupant.getRequester().getOuid());
//			}
//			else {
//				User user = AccountUtil.getUserBean().getPortalUser(occupant.getRequester().getUid());
//				if(!user.isUserVerified() || !user.isInviteAcceptStatus() ) {
//					AccountUtil.getUserBean().resendInvite(user.getOuid());
//				}
//				else {
//					AccountUtil.getUserBean().enableUser(occupant.getRequester().getOuid());
//				}
//			}
//		}
//		else {
//			if(occupant.getIsPortalAccessNeeded()) {
//				addUserAsRequester(occupant);
//			}
//		}
//		if(updateRecord) {
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.OCCUPANT);
//			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.OCCUPANT);
//		
//			RecordAPI.updateRecord(occupant, module, fields);
//		}
//		
	}
	
	public static boolean checkForDuplicateOccupant(OccupantsContext occupant) throws Exception {
		OccupantsContext occupantExisiting = getOccupant(occupant.getEmail());
		if(occupantExisiting != null && occupant.getId() != occupantExisiting.getId()) {
			return true;
		}
		return false;
	}
	
	public static OccupantsContext getOccupant(String email) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.OCCUPANT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.OCCUPANT);
		SelectRecordsBuilder<OccupantsContext> builder = new SelectRecordsBuilder<OccupantsContext>()
														.module(module)
														.beanClass(OccupantsContext.class)
														.select(fields)
														;
		
		if(StringUtils.isNotEmpty(email)) {
			builder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", String.valueOf(email), StringOperators.IS));
		}
		
		OccupantsContext records = builder.fetchFirst();
		return records;
	}

	public static void addOccupantFromRequester(User requester, OccupantType occupantType) throws Exception {
		
		OccupantsContext occupant = new OccupantsContext();
		occupant.setIsPortalAccessNeeded(true);
		occupant.setOccupantType(occupantType);
		occupant.setRequester(requester);
		occupant.setName(requester.getName());
		occupant.setEmail(requester.getEmail());
		occupant.setPhone(requester.getMobile());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.OCCUPANT);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		RecordAPI.addRecord(true, java.util.Collections.singletonList(occupant), module, fields);
	}
	
	public static List<Map<String,Object>> getTenantOccupants(List<Long> tenantIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.OCCUPANT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.OCCUPANT);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField tenantId = fieldMap.get("tenant");
		
		SelectRecordsBuilder<OccupantsContext> builder = new SelectRecordsBuilder<OccupantsContext>()
														.module(module)
														.beanClass(OccupantsContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition(tenantId, tenantIds, PickListOperators.IS))
														;
		
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		LookupField requesterField = (LookupField) fieldsAsMap.get("requester");
		
		builder.fetchSupplement(requesterField);
		
		List<Map<String,Object>> records = builder.getAsProps();
		return records;
		
	}
}
