package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.NamespaceBean;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VirtualMeterTemplateAPI {

   public static VirtualMeterTemplateReadingContext getVirtualMeterTemplateReadingContext(Long virtualMeterTemplateReading) throws Exception {

	   return (VirtualMeterTemplateReadingContext) V3Util.getRecord(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING, virtualMeterTemplateReading, null);
   }

    public static List<VirtualMeterTemplateReadingContext> setVirtualMeterTemplateReadings(long vmTemplateId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<VirtualMeterTemplateReadingContext> builder = new SelectRecordsBuilder<VirtualMeterTemplateReadingContext>()
                .module(module)
                .beanClass(VirtualMeterTemplateReadingContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("virtualMeterTemplate"), String.valueOf(vmTemplateId), NumberOperators.EQUALS));
        List<VirtualMeterTemplateReadingContext> Readings = builder.get();

        NamespaceBean nsBean = (NamespaceBean) BeanFactory.lookup("NamespaceBean");

        if (CollectionUtils.isNotEmpty(Readings)) {

            for(VirtualMeterTemplateReadingContext vmTemplateReading : Readings) {

                NameSpaceContext nameSpace = new NameSpaceContext(nsBean.getNamespaceForParent(vmTemplateReading.getId(), NSType.VIRTUAL_METER));
                vmTemplateReading.setNs(nameSpace);

            }

        }
        return Readings;
    }

    public static VirtualMeterTemplateReadingContext getVmTemplateReading(Long recordId) throws Exception {
        String moduleName = FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING;
        FacilioContext summary = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
        List<VirtualMeterTemplateReadingContext> vmTemplateReading = Constants.getRecordListFromContext(summary, moduleName);
        if (CollectionUtils.isNotEmpty(vmTemplateReading)) {
            return vmTemplateReading.get(0);
        }
        throw new IllegalArgumentException("Invalid vm Template Reading Id");
    }
}
