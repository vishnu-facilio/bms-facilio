package com.facilio.bmsconsoleV3.commands.meter;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class ValidateMeterQrValueCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3MeterContext> meterList = (List<V3MeterContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("meter"));
        List<Long> recordIds = meterList.stream().map(V3MeterContext::getId).collect(Collectors.toList());
        for(V3MeterContext meter:meterList){
            String qrValue = meter.getQrVal();
            if (StringUtils.isNotEmpty(qrValue)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                String moduleName = FacilioConstants.Meter.METER;
                FacilioModule module = modBean.getModule(moduleName);
                List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
                if (fields == null) {
                    fields = modBean.getAllFields(moduleName);
                }
                if (CollectionUtils.isNotEmpty(recordIds)) {
                    SelectRecordsBuilder<V3MeterContext> builder = new SelectRecordsBuilder<V3MeterContext>()
                            .module(module)
                            .beanClass(V3MeterContext.class)
                            .select(fields)
                            .andCondition(CriteriaAPI.getIdCondition(recordIds, module));
                    List<V3MeterContext> records = builder.get();

                    for (V3MeterContext record : records) {
                        if (record.getId() == meter.getId() && !qrValue.equals(record.getQrVal())) {
                            List<V3MeterContext> qrValueRecords = checkQrValue(module, fields, qrValue);
                            if (qrValueRecords.size() >= 1) {
                                throw new IllegalArgumentException("QR Value already exists");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private List<V3MeterContext> checkQrValue(FacilioModule module,List<FacilioField> fields,String qrValue) throws Exception{
        SelectRecordsBuilder<V3MeterContext> selectRecordsBuilder = new SelectRecordsBuilder<V3MeterContext>()
                .module(module)
                .beanClass(V3MeterContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("QR_VALUE", "qrVal", qrValue, StringOperators.IS));
        List<V3MeterContext> qrValueRecords = selectRecordsBuilder.get();
        return qrValueRecords;
    }
}
