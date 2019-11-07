package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetAllServiceCatalogGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                .select(FieldFactory.getServiceCatalogGroupFields());

        List<Map<String, Object>> maps = builder.get();
        List<ServiceCatalogGroupContext> serviceCatalogGroups = FieldUtil.getAsBeanListFromMapList(maps, ServiceCatalogGroupContext.class);
        context.put(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUPS, serviceCatalogGroups);

        return false;
    }
}
