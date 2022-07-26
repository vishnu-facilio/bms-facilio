package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import lombok.extern.log4j.Log4j;

@Log4j
public class DeleteUserSignatureCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{
		
		Long signatureFileId =null;
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOrgUserModule().getTableName())
				.select(AccountConstants.getAppOrgUserFields())
				.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouid", String.valueOf(AccountUtil.getCurrentUser().getId()), NumberOperators.EQUALS));
			  
		Map<String,Object> select = selectBuilder.fetchFirst();
		
		if(MapUtils.isNotEmpty(select) && select.containsKey(FacilioConstants.ContextNames.SIGNATURE_FILE_ID)) {
			 signatureFileId= (Long) select.get(FacilioConstants.ContextNames.SIGNATURE_FILE_ID);
		}
		if(signatureFileId!=null) {
			Map<String, Object> props = new HashMap<>();									        
			props.put("signatureFileId",-99);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getOrgUserModule().getTableName())
					.fields(AccountConstants.getAppOrgUserFields())
					.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouid", String.valueOf(AccountUtil.getCurrentUser().getId()), NumberOperators.EQUALS));
			
			int updatedRows = updateBuilder.update(props);
			
			if(updatedRows > 0) {							
				context.put(FacilioConstants.ContextNames.SIGNATURE_DELETE, "success");
			}
		}else {
			throw new IllegalArgumentException("Internal Error - Signature does not exists");
	}
	return false;
 }
}
