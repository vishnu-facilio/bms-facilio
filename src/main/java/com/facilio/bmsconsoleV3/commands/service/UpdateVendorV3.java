
package com.facilio.bmsconsoleV3.commands.service;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ServiceVendorContext;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3ServiceVendorContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateVendorV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {


        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ServiceContext> service = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(service)) {
            for (V3ServiceContext services : service) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                FacilioModule serviceVendorModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_VENDOR);
                List<FacilioField> fields = modBean.getAllFields(moduleName);
                DeleteRecordBuilder<ServiceVendorContext> deleteBuilder = new DeleteRecordBuilder<ServiceVendorContext>()
                        .module(serviceVendorModule)
                        .andCondition(CriteriaAPI.getCondition("SERVICE_ID", "service", String.valueOf(services.getId()), NumberOperators.EQUALS));
                deleteBuilder.delete();

                if (CollectionUtils.isNotEmpty(services.getServiceVendors())) {
                    updateServiceVendors(services);
                    addRecord(false, services.getServiceVendors(), serviceVendorModule, modBean.getAllFields(serviceVendorModule.getName()));

                }
            }
        }


        return false;
    }


    private  void updateServiceVendors(V3ServiceContext service) {
        for (V3ServiceVendorContext v3ServiceVendorContext : service.getServiceVendors()) {
            v3ServiceVendorContext.setServiceId(service.getId());
        }
    }
    private void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {

        //custom fields multi lookup handling
        List<SupplementRecord> supplements = new ArrayList<>();
        CommonCommandUtil.handleFormDataAndSupplement(fields, list.get(0).getData(), supplements);

        InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
                .module(module)
                .fields(fields);
        if(isLocalIdNeeded) {
            insertRecordBuilder.withLocalId();
        }

        if(CollectionUtils.isNotEmpty(supplements)) {
            insertRecordBuilder.insertSupplements(supplements);
        }
        insertRecordBuilder.addRecord(list.get(0));
        insertRecordBuilder.save();
    }

}