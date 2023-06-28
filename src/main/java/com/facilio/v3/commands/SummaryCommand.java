package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.unitconversion.Unit;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryCommand extends FacilioCommand {
    private FacilioModule module;

    public SummaryCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = module.getName();
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(module)) {
            fields.addAll(FieldFactory.getCurrencyPropsFields(module));
        }

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
                .select(fields)
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(recordIds, module))
                .beanClass(beanClass);

        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            selectRecordsBuilder.fetchSupplements(supplementFields);
        }

        boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
        if (skipModuleCriteria) {
            selectRecordsBuilder.skipModuleCriteria();
        }
        List<ModuleBaseWithCustomFields> list = selectRecordsBuilder.get();

        setDisplayUnitIdForNumberFieldAndDecimalField(fields,list);

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName, list);

        context.put(Constants.RECORD_MAP, recordMap);

        return false;
    }
    private void setDisplayUnitIdForNumberFieldAndDecimalField(List<FacilioField> fields,List<ModuleBaseWithCustomFields> records){
        if(CollectionUtils.isEmpty(records)){
            return;
        }
        Map<FacilioField, Unit> fieldVsDisplayUnit = new HashMap<>();
        for (FacilioField field:fields){
            if(field.getDataTypeEnum()== FieldType.NUMBER || field.getDataTypeEnum()==FieldType.DECIMAL){
                NumberField numberField = (NumberField) field;
                if(numberField.getMetric()!=-1 && numberField.getUnitId()!=-1){
                    int unitId = numberField.getUnitId(); //Display Unit id
                    fieldVsDisplayUnit.put(numberField,Unit.valueOf(unitId));
                }
            }
        }

       for(ModuleBaseWithCustomFields record:records){
          for(FacilioField field:fieldVsDisplayUnit.keySet()){
              String fieldName = field.getName();
              Unit unitId = fieldVsDisplayUnit.get(field);
              if(record.getDatum(fieldName)!=null && unitId!=null){
                  record.setDatum(fieldName+"Unit",unitId.getUnitId());
              }
          }
       }

    }
}
