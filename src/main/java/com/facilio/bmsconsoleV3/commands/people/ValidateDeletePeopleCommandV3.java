package com.facilio.bmsconsoleV3.commands.people;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ValidateDeletePeopleCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = new HashMap<String, Object>();
        String moduleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> peopleIds = (List<Long>) context.get("recordIds");
        List<Object> extendedModuleDataList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(peopleIds)) {
            List<V3PeopleContext> peopleList = V3RecordAPI.getRecordsList(moduleName,peopleIds,V3PeopleContext.class);
            if (CollectionUtils.isNotEmpty(peopleList)) {
                for (V3PeopleContext people : peopleList) {
                    Long parentId = null;
                    if (people.getPeopleType() == V3PeopleContext.PeopleType.TENANT_CONTACT.getIndex()) {
                        V3TenantContactContext tenantContact = V3RecordAPI.getRecord(moduleName,people.getId(),V3TenantContactContext.class);
                        extendedModuleDataList.add((V3TenantContactContext) tenantContact);
                        if(tenantContact != null && tenantContact.getTenant() != null){
                            parentId = tenantContact.getTenant().getId();
                            Criteria criteria = new Criteria();
                            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getTenantContactFields()).get("tenant"), String.valueOf(parentId), NumberOperators.EQUALS));
                            FacilioField aggregateField = FieldFactory.getIdField(modBean.getModule(moduleName));
                            List<Map<String, Object>> props = V3RecordAPI.getRecordsAggregateValue(moduleName, null, V3TenantContactContext.class,criteria, BmsAggregateOperators.CommonAggregateOperator.COUNT, aggregateField, null);
                            if(props != null) {
                                Long count = (Long) props.get(0).get(aggregateField.getName());
                                if (count != null && count <= 1) {
                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot delete contact");
                                }
                            }
                        }
                    }
                }
            }
            recordMap.put(moduleName, extendedModuleDataList);
            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        }
        return false;
    }
}
