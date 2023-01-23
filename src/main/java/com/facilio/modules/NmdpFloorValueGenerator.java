package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3FloorContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationVendorsContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.V3Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NmdpFloorValueGenerator extends ValueGenerator{

    @Override
    public Object generateValueForCondition(int appType) {
        if(appType == AppDomain.AppDomainType.FACILIO.getIndex()) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                long peopleId = AccountUtil.getCurrentUser().getPeopleId();
                FacilioModule empModule = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
                List<FacilioField> fields = modBean.getAllFields(empModule.getName());

                List<V3EmployeeContext> employees = V3RecordAPI.getRecordsListWithSupplements(empModule.getName(), Collections.singletonList(peopleId), V3EmployeeContext.class, null, null, null, null, true );
                if(CollectionUtils.isNotEmpty(employees)){
                    List<Long> empIds = employees.stream().map(V3EmployeeContext::getId).collect(Collectors.toList());
                    Criteria criteria = new Criteria();
                    criteria.addAndCondition(CriteriaAPI.getCondition("LEFT_ID","left",StringUtils.join(empIds,","), NumberOperators.EQUALS));
                    List<RelRecord> relRecs = V3RecordAPI.getRecordsListWithSupplements("employee-floor-allowedfloors_employee-rel", null, RelRecord.class, criteria, null, null, null, true );
                    if(CollectionUtils.isNotEmpty(relRecs)){
                        List<Long> floorIds = new ArrayList<>();
                        for(RelRecord relRecord : relRecs){
                            Map<String,Object> floor = (Map<String,Object>) relRecord.getRight();
                            floorIds.add((Long)floor.get("id"));
                        }
                        return StringUtils.join(floorIds,",");
                    }
                }
                FacilioModule floorModule = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
                List<V3FloorContext> floors = V3RecordAPI.getRecordsListWithSupplements(floorModule.getName(), null, V3FloorContext.class, null, null, null, null, true );
                if(CollectionUtils.isNotEmpty(floors)){
                    List<Long> floorIds = floors.stream().map(V3FloorContext::getId).collect(Collectors.toList());
                    return StringUtils.join(floorIds, ",");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "NMDP Employee Floors";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.NmdpFloorValueGenerator";
    }

    @Override
    public String getModuleName() {
        return "floor";
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 9;
    }

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        return null;
    }

}
