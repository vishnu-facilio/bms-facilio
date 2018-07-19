package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStoreFactory;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HandlePortalSSOCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		File publicKeyFile = (File)context.get(FacilioConstants.ContextNames.PUBLICKEYFILE);
		PortalInfoContext servicePortal  = (PortalInfoContext) context.get(FacilioConstants.ContextNames.PORTALINFO);
		String fileName = (String)context.get(FacilioConstants.ContextNames.PUBLICKEYFILENAME);
		String contentType = (String)context.get(FacilioConstants.ContextNames.PUBLICKEYFILETYPE);
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());

		servicePortal.setPublicKey(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile(fileName,publicKeyFile,contentType));

		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_DEFAULT);
		Map<String, Object> serviceProtalProps = (Map<String, Object>) mapper.convertValue(servicePortal, PortalInfoContext.class);
		

		FacilioModule module = ModuleFactory.getServicePortalModule();
		
		List<FacilioField> fields = FieldFactory.getServicePortalFields();
		
        Object portalid = serviceProtalProps.remove("portalId");
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
												.table(module.getTableName())
												.fields(fields)
												.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
												.andCustomWhere("PORTALID = ? ", portalid);
		builder.update(serviceProtalProps);
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}
	
}
