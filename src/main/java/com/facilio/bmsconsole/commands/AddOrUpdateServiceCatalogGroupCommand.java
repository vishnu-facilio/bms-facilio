package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddOrUpdateServiceCatalogGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ServiceCatalogGroupContext serviceCatalogGroup = (ServiceCatalogGroupContext) context.get(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP);
        if (serviceCatalogGroup != null) {
            if (serviceCatalogGroup.getId() > 0) {
                updateServiceCatalogGroup(serviceCatalogGroup);
            }
            else {
                addServiceCatalogGroup(serviceCatalogGroup);
            }
        }
        return false;
    }

    private void updateServiceCatalogGroup(ServiceCatalogGroupContext serviceCatalogGroup) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                .fields(FieldFactory.getServiceCatalogGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(serviceCatalogGroup.getId(), ModuleFactory.getServiceCatalogGroupModule()))
                ;
        builder.update(FieldUtil.getAsProperties(serviceCatalogGroup));
    }

    private void addServiceCatalogGroup(ServiceCatalogGroupContext serviceCatalogGroup) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                .fields(FieldFactory.getServiceCatalogGroupFields())
                ;
        Map<String, Object> map = FieldUtil.getAsProperties(serviceCatalogGroup);
        builder.insert(map);
        serviceCatalogGroup.setId((long) map.get("id"));
    }
}
