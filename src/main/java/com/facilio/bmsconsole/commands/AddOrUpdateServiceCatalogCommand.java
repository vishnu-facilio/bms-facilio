package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext.SharingType;
import com.facilio.bmsconsole.util.ServiceCatalogApi;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateServiceCatalogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ServiceCatalogContext serviceCatalog = (ServiceCatalogContext) context.get(FacilioConstants.ContextNames.SERVICE_CATALOG);
        if (serviceCatalog != null && StringUtils.isNotEmpty(moduleName)) {
            validateServiceCatalog(serviceCatalog);

            if (serviceCatalog.getId() > 0) {
                updateServiceCatalog(serviceCatalog);
            }
            else {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                if (module == null) {
                    throw new IllegalArgumentException("Invalid module");
                }

                serviceCatalog.setModuleId(module.getModuleId());
                addServiceCatalog(serviceCatalog);
            }
            
            addServiceCatalogItemSharing(serviceCatalog);
            
        }
        return false;
    }

    private void validateServiceCatalog(ServiceCatalogContext serviceCatalog) throws Exception {
        if (serviceCatalog.getTypeEnum() == null) {
            serviceCatalog.setType(ServiceCatalogContext.Type.MODULE_FORM);
        }

        switch (serviceCatalog.getTypeEnum()) {
            case MODULE_FORM:
                if (serviceCatalog.getFormId() == -1) {
                    throw new IllegalArgumentException("Form cannot be empty");
                }
                break;

            case EXTERNAL_LINK:
                if (StringUtils.isEmpty(serviceCatalog.getExternalURL())) {
                    throw new IllegalArgumentException("External URL is not found");
                }
                break;
        }
    }

    private void updateServiceCatalog(ServiceCatalogContext serviceCatalog) throws Exception {
        checkForDuplicateName(serviceCatalog.getName(), serviceCatalog.getId(),serviceCatalog.getAppId());

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .fields(FieldFactory.getServiceCatalogFields())
                .andCondition(CriteriaAPI.getIdCondition(serviceCatalog.getId(), ModuleFactory.getServiceCatalogModule()))
                ;
        builder.update(FieldUtil.getAsProperties(serviceCatalog));
    }

    private void checkForDuplicateName(String name, long id,long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .select(FieldFactory.getServiceCatalogFields())
                .andCondition(CriteriaAPI.getCondition("NAME", "name", name, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));;
        if (id > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(id), NumberOperators.NOT_EQUALS));
        }
        Map<String, Object> map = builder.fetchFirst();
        if (map != null) {
            throw new IllegalArgumentException("Name cannot be duplicated");
        }
    }

    private void addServiceCatalog(ServiceCatalogContext serviceCatalog) throws Exception {
        checkForDuplicateName(serviceCatalog.getName(), -1,serviceCatalog.getAppId());
        if (serviceCatalog.isComplaintType()) {
        		serviceCatalog.setGroupId(ServiceCatalogApi.getComplaintCategory().getId());
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .fields(FieldFactory.getServiceCatalogFields())
                ;
        Map<String, Object> map = FieldUtil.getAsProperties(serviceCatalog);
        builder.insert(map);
        serviceCatalog.setId((long) map.get("id"));
    }
    
    private void addServiceCatalogItemSharing(ServiceCatalogContext serviceCatalog) throws Exception {
    	SharingContext<SingleSharingContext> sharing = serviceCatalog.getSharing();
		SharingAPI.deleteSharingForParent(Collections.singletonList(serviceCatalog.getId()), ModuleFactory.getServiceCatalogItemSharingModule());

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(sharing == null) {
			sharing = new SharingContext<>();
		}
		
			List<Long> orgUsersId = sharing.stream().filter(value -> value.getTypeEnum() == SharingType.USER)
					.map(val -> val.getUserId()).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(orgUsersId) && !orgUsersId.contains(AccountUtil.getCurrentUser().getId())) {
				SingleSharingContext newSharing = new SingleSharingContext(); 
				newSharing.setUserId(AccountUtil.getCurrentUser().getId());
				newSharing.setType(SharingType.USER);
				sharing.add(newSharing);	
			}
			SharingAPI.addSharing(sharing, serviceCatalog.getId(), ModuleFactory.getServiceCatalogItemSharingModule());
    }
}
