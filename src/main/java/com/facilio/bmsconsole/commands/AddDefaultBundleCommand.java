package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bundle.command.BundleTransactionChainFactory;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import lombok.extern.log4j.Log4j;

@Log4j
public class AddDefaultBundleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Organization org = AccountUtil.getCurrentOrg();
		
		BundleContext defaultBundle = new BundleContext();
		
		defaultBundle.setBundleName("default");
		defaultBundle.setBundleGlobalName("org.facilio."+org.getDomain()+".default");
		defaultBundle.setOrgId(org.getOrgId());
		defaultBundle.setVersion("0.0.1");
		defaultBundle.setTypeEnum(BundleContext.BundleTypeEnum.UN_MANAGED_SYSTEM);
		defaultBundle.setCreatedTime(System.currentTimeMillis());
		defaultBundle.setModifiedTime(defaultBundle.getCreatedTime());
		
		FacilioChain addBundle = BundleTransactionChainFactory.addBundleChain();
		
		FacilioContext contextnew = addBundle.getContext();
		
		contextnew.put(BundleConstants.BUNDLE_CONTEXT, defaultBundle);
		
		addBundle.execute();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, defaultBundle);
		
		addAllModulesAndFieldsAsChangeSet(defaultBundle);
		
		return false;
	}

	private void addAllModulesAndFieldsAsChangeSet(BundleContext defaultBundle) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String,Object> updateMap = new HashMap<String, Object>();
		
		updateMap.put("createdTime", AccountUtil.getCurrentOrg().getCreatedTime());
		updateMap.put("modifiedTime", AccountUtil.getCurrentOrg().getCreatedTime());
		
		GenericUpdateRecordBuilder updateModules = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getModuleModule().getTableName())
				.fields(FieldFactory.getModuleFields())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), ModuleFactory.getModuleModule()));
		
		updateModules.update(updateMap);
		
		
		GenericUpdateRecordBuilder updateFields = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFieldsModule().getTableName())
				.fields(FieldFactory.getAddFieldFields())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), ModuleFactory.getFieldsModule()));
		
		updateFields.update(updateMap);
		

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		ModuleType[] allModuleType = FacilioModule.ModuleType.values();
		
		for(ModuleType moduleType : allModuleType) {
			List<FacilioModule> modules = modBean.getModuleList(moduleType);
			
			for(FacilioModule module : modules) {

				BundleUtil.addBundleChangeSetForSystemComponents(defaultBundle,BundleComponentsEnum.MODULE, module.getModuleId(), module.getDisplayName());
				
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
																.select(FieldFactory.getSelectFieldFields())
																.table("Fields")
																.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", module.getModuleId()+"", NumberOperators.EQUALS));

				List<Map<String, Object>> fieldProps = selectBuilder.get();
				
				if(fieldProps != null && !fieldProps.isEmpty()) {
					
					List<FacilioField> fields = FieldUtil.getAsBeanListFromMapList(fieldProps, FacilioField.class);
					
					for(FacilioField field : fields) {
						BundleUtil.addBundleChangeSetForSystemComponents(defaultBundle,BundleComponentsEnum.FIELD, field.getFieldId(), field.getDisplayName());
					}
				}
			}
		}
	}

}
