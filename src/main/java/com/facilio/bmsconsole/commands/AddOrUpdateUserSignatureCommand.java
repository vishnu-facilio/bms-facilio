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
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import lombok.extern.log4j.Log4j;

@Log4j
public class AddOrUpdateUserSignatureCommand extends FacilioCommand{
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
			String signatureContent = (String)context.get(FacilioConstants.ContextNames.SIGNATURE_CONTENT);
			Map<String,Object> signature =new HashMap<String, Object>();
			Long oldfileId = null;
			if(signatureContent == null) {
				throw new IllegalArgumentException("Signature Content is empty for User ID - "+ AccountUtil.getCurrentUser().getId() +" and in ORG ID - "+AccountUtil.getCurrentOrg().getId());
			}
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getOrgUserModule().getTableName())
					.select(AccountConstants.getAppOrgUserFields())
					.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("USERID", "uid", String.valueOf(AccountUtil.getCurrentUser().getId()), NumberOperators.EQUALS));
				  
			Map<String,Object> select = selectBuilder.fetchFirst();
			
			if(select.containsKey(FacilioConstants.ContextNames.SIGNATURE_FILE_ID)) {
				oldfileId= (Long) select.get(FacilioConstants.ContextNames.SIGNATURE_FILE_ID);
			}
			User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
			Long signatureFileId = FacilioFactory.getFileStore(superAdmin.getId()).addFile("Signature_"+AccountUtil.getCurrentOrg().getOrgId()+"_"+AccountUtil.getCurrentUser().getId(), signatureContent, "text/plain");
			FileInfo test= FacilioFactory.getFileStore().getFileInfo(signatureFileId);  //TODO be removed after testing

			Map<String, Object> props = new HashMap<>();									        
			props.put("signatureFileId",signatureFileId);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getOrgUserModule().getTableName())
					.fields(AccountConstants.getAppOrgUserFields())
					.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("USERID", "uid", String.valueOf(AccountUtil.getCurrentUser().getId()), NumberOperators.EQUALS));
				  
			int updatedRows = updateBuilder.update(props);
			
			if(updatedRows > 0) {					
				signature.put(FacilioConstants.ContextNames.SIGNATURE_CONTENT, signatureContent);
				signature.put(FacilioConstants.ContextNames.SIGNATURE_FILE_ID, signatureFileId);
				
				context.put(FacilioConstants.ContextNames.SIGNATURE, signature);
				
				if(oldfileId!=null) {
					FileStore fs = FacilioFactory.getFileStore();
					fs.deleteFile(oldfileId);		
				}
			}else{
				LOGGER.info("Update Signature ID in DB failed for USER ID - "+ AccountUtil.getCurrentUser().getId() +" and in ORG ID - "+AccountUtil.getCurrentOrg().getId());
			}
		return false;
	}
}