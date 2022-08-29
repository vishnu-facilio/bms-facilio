package com.facilio.bmsconsoleV3.commands.failureclass;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.WorkOrderFailureClassRelationship;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class CheckForAssociatedModulesBeforeDeletion extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get("moduleName");
        List<Long> ids = (List<Long>) context.get("recordIds");
        Boolean isAvailable = null;
            if(moduleName.equals(FacilioConstants.ContextNames.FAILURE_CODE_PROBLEMS)){
                for(Long id:ids) {
                     isAvailable = isFailureClassAssociated(id, "FAILURE_PROBLEM", "failureProblem");
                    if (isAvailable != null && isAvailable) {
                        throw new IllegalArgumentException("This problem has been associated to multiple records and cannot be deleted");
                    }
                }
            }else if(moduleName.equals(FacilioConstants.ContextNames.FAILURE_CODE_CAUSES)){
                for(Long id:ids) {
                     isAvailable = isFailureClassAssociated(id, "FAILURE_CAUSE", "failureCause");
                    if (isAvailable != null && isAvailable) {
                        throw new IllegalArgumentException("This cause has been associated to multiple records and cannot be deleted");
                    }
                }
            }else if(moduleName.equals(FacilioConstants.ContextNames.FAILURE_CODE_REMEDIES)){
                for(Long id:ids) {
                    isAvailable = isFailureClassAssociated(id, "FAILURE_REMEDY", "failureRemedy");
                    if (isAvailable != null && isAvailable) {
                        throw new IllegalArgumentException("This remedy has been associated to multiple records and cannot be deleted");
                    }
                }
            }
        return false;
    }
    private Boolean isFailureClassAssociated(Long id,String columnName,String FieldName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<WorkOrderFailureClassRelationship> selectBuilder = new SelectRecordsBuilder<WorkOrderFailureClassRelationship>()
                .module(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_FAILURE_CLASS_RELATIONSHIP))
                .table("WorkOrder_FailureClass_Relationship")
                .select(modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_FAILURE_CLASS_RELATIONSHIP))
                .beanClass(WorkOrderFailureClassRelationship.class)
                .andCondition(CriteriaAPI.getCondition(columnName, FieldName, String.valueOf(id), NumberOperators.EQUALS));
        List<WorkOrderFailureClassRelationship> props = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(props)){
            return true;
        }
        return null;
    }
}
