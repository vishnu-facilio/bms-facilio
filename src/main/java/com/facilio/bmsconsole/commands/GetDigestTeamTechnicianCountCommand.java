package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetDigestTeamTechnicianCountCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(GetDigestTeamTechnicianCountCommand.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		LOGGER.log(Level.SEVERE, "startTime -- " + System.currentTimeMillis());

		FacilioModule groupsModule = AccountConstants.getGroupModule();
		FacilioModule groupMembersModule = AccountConstants.getGroupMemberModule();
		FacilioModule rolesModule = AccountConstants.getRoleModule();
		FacilioModule orgUserModule = AccountConstants.getOrgUserModule();
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		
		FacilioField countField = new FacilioField();
		countField.setName("count");
		countField.setColumnName("count(DISTINCT "+groupMembersModule.getTableName()+".ORG_USERID)");
		fields.add(countField);
		
		FacilioField siteIdField = FieldFactory.getSiteIdField(groupsModule);
		fields.add(siteIdField);
	
		List<FacilioField> roleFields = AccountConstants.getRoleFields();
		
		GenericSelectRecordBuilder roleSelectBuilder = new GenericSelectRecordBuilder()
				.select(roleFields).table(rolesModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(rolesModule.getTableName()+".NAME", "name" ,"Technician", StringOperators.STARTS_WITH))
				.andCondition(CriteriaAPI.getCondition(rolesModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
			    ;
		List<Map<String, Object>> roleList = roleSelectBuilder.get();
		Long roleId = (Long)roleList.get(0).get("roleId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields).table(groupsModule.getTableName())
				.innerJoin(groupMembersModule.getTableName()).on(groupsModule.getTableName()+".GROUPID = "+groupMembersModule.getTableName()+".GROUPID")
				.innerJoin(orgUserModule.getTableName()).on(groupMembersModule.getTableName()+".ORG_USERID = "+orgUserModule.getTableName()+".ORG_USERID")
				.andCondition(CriteriaAPI.getCondition(orgUserModule.getTableName()+".ROLE_ID", "roleId" ,""+roleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(groupsModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
				.groupBy(siteIdField.getCompleteColumnName())
			    ;
		
		if(AccountUtil.getCurrentOrg().getId() == 92)//spi cinemas org id - to fetch the tech count from teams begin with Digest_
		{
			selectBuilder.andCondition(CriteriaAPI.getCondition(groupsModule.getTableName()+".GROUP_NAME", "name" , "Digest_", StringOperators.STARTS_WITH));
			
		}
		List<Map<String, Object>> list = selectBuilder.get();
		
		Map<Long,Object> countMap = new HashMap<Long, Object>();
	
		for(int i=0;i<list.size();i++)
		{
			Map<String, Object> siteTechMap = list.get(i);
			countMap.put((Long)siteTechMap.get("siteId"), siteTechMap.get("count"));
		}
		context.put(FacilioConstants.ContextNames.TECH_COUNT_GROUP_DIGEST, countMap);

		return false;
	}

	private void sortPMs(List<PMJobsContext> pmJobs) {
		pmJobs.sort(Comparator.comparing(PMJobsContext::getNextExecutionTime, (s1, s2) -> {
			return Long.compare(s1, s2);
		}));
	}

}
