package com.facilio.bmsconsole.commands;

import java.util.*;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class ValidateQrValueCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ResourceContext asset = (ResourceContext) context.get(FacilioConstants.ContextNames.RECORD);
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        String qrValue = asset.getQrVal();
        if (StringUtils.isNotEmpty(qrValue)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            FacilioModule module = modBean.getModule(moduleName);
            List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

            if (fields == null) {
                fields = modBean.getAllFields(moduleName);
            }

            Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);

            if (beanClassName == null) {
                if (module.isCustom()) {
                    beanClassName = CustomModuleData.class;
                } else {
                    beanClassName = ModuleBaseWithCustomFields.class;
                }
            }
            //edit asset
            if (CollectionUtils.isNotEmpty(recordIds)) {
                SelectRecordsBuilder<ResourceContext> builder = new SelectRecordsBuilder<ResourceContext>()
                        .module(module)
                        .beanClass(beanClassName)
                        .select(fields)
                        .andCondition(CriteriaAPI.getIdCondition(recordIds, module));
                List<ResourceContext> records = builder.get();

                for (ResourceContext record : records) {
                    if (!(qrValue.equals(record.getQrVal()))) {
                        List<ResourceContext> qrValueRecords = selectQueryMethod(module,beanClassName,fields,qrValue);
                        if (qrValueRecords.size() >= 1) {
                            throw new IllegalArgumentException("QR Value already exists");
                        }
                    }
                }
            }
            //create asset
            else {
                List<ResourceContext> qrValueRecords = selectQueryMethod(module,beanClassName,fields,qrValue);
                if (qrValueRecords.size() >= 1) {
                    throw new IllegalArgumentException("QR Value already exists");
                }
            }
        }
        return false;
    }
    private List<ResourceContext> selectQueryMethod(FacilioModule module,Class beanClassName,List<FacilioField> fields,String qrValue) throws Exception{
        SelectRecordsBuilder<ResourceContext> selectRecordsBuilder = new SelectRecordsBuilder<ResourceContext>()
                .module(module)
                .beanClass(beanClassName)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("QR_VALUE", "qrVal", qrValue, NumberOperators.EQUALS));
        List<ResourceContext> qrValueRecords = selectRecordsBuilder.get();
        return qrValueRecords;
    }
}