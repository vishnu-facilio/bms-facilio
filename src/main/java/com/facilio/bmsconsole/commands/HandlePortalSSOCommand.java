package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStoreFactory;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.io.File;
import java.util.List;
import java.util.Map;

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
