package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.io.IOUtils;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import lombok.extern.log4j.Log4j;

@Log4j
public class GetUserSignatureCommand extends FacilioCommand{
	
	@Override
	public boolean executeCommand(Context context) throws Exception{
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOrgUserModule().getTableName())
				.select(AccountConstants.getAppOrgUserFields())
				.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("USERID", "uid", String.valueOf(AccountUtil.getCurrentUser().getId()), NumberOperators.EQUALS));
			  
		Map<String,Object> select = selectBuilder.fetchFirst();
		
		Long signatureFileId= (Long) select.get("signatureFileId");
		
		if(signatureFileId!=null) {
			String signatureContent = new String();
			User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
			FileStore fs =  FacilioFactory.getFileStore();
			if(superAdmin != null) {
				fs = FacilioFactory.getFileStore(superAdmin.getId());
			}
			try(InputStream body = fs.readFile(signatureFileId)) {
				if(body != null) {
					signatureContent=IOUtils.toString(body);
				}else {
					LOGGER.info("Exception Occurred in getting data from file - Nullpointer ");
				}
			}
			catch(Exception e) {
				LOGGER.error("Exception occurred ", e);
				throw e;
			}
			
			Map<String,Object> signature =new HashMap<String, Object>();
		
			signature.put(FacilioConstants.ContextNames.SIGNATURE_CONTENT, signatureContent);
			signature.put(FacilioConstants.ContextNames.SIGNATURE_FILE_ID, signatureFileId);
			
			context.put(FacilioConstants.ContextNames.SIGNATURE, signature);
				
		}else {
			Map<String,Object> signature =new HashMap<String, Object>();
			
			signature.put(FacilioConstants.ContextNames.SIGNATURE_CONTENT, "");
			signature.put(FacilioConstants.ContextNames.SIGNATURE_FILE_ID, "");
			
			context.put(FacilioConstants.ContextNames.SIGNATURE, signature);
		}
		
		return false;
	}
}