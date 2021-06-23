package com.facilio.bmsconsoleV3.commands.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.V3FloorContext;
import com.facilio.bmsconsoleV3.context.V3MovesContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext.SpaceType;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilitySpecialAvailabilityContext;
import com.facilio.bmsconsoleV3.context.V3DepartmentContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class ValidateMovesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3MovesContext> moves = recordMap.get(moduleName);
        
        if(CollectionUtils.isNotEmpty(moves)) {
        	
        for(V3MovesContext move : moves){
		
		V3DeskContext fromDesk = move.getFrom();
		V3DeskContext toDesk = move.getTo();
		if(toDesk != null && toDesk.getId() > 0) {
			toDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, toDesk.getId(), V3DeskContext.class);
		}
		if(fromDesk != null && fromDesk.getId() > 0) {
			fromDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, fromDesk.getId(), V3DeskContext.class);
		}
		V3EmployeeContext employee = move.getEmployee();
		
		if (employee != null && employee.getId() > 0) {
			employee = (V3EmployeeContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, employee.getId(), V3EmployeeContext.class);
		} else if(employee == null && fromDesk!=null && fromDesk.getEmployee() != null) {
			employee = (V3EmployeeContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, fromDesk.getEmployee().getId(), V3EmployeeContext.class);
			move.setEmployee(employee);
		}
		
		if (employee != null && employee.getId() > 0) {
		FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
        List<FacilioField> deskFields = modBean.getAllFields(FacilioConstants.ContextNames.Floorplan.DESKS);
        Map<String, FacilioField> deskfieldsAsMap = FieldFactory.getAsMap(deskFields);
        
        SelectRecordsBuilder<V3DeskContext> deskbuilder = new SelectRecordsBuilder<V3DeskContext>()
                .moduleName(deskModule.getName())
                .select(deskFields)
                .beanClass(V3DeskContext.class)
                .andCondition(CriteriaAPI.getCondition(deskfieldsAsMap.get("isArchived"), String.valueOf(false), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(deskfieldsAsMap.get("employee"), String.valueOf(employee.getId()), PickListOperators.IS));

        List<V3DeskContext> desklist = deskbuilder.get();
        List<Long> deskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(desklist)) {
        	desklist.forEach(i -> deskIds.add(i.getId()));
        }
			if(fromDesk != null) {
				if(!deskIds.contains(fromDesk.getId())) {
					throw new RESTException(ErrorCode.VALIDATION_ERROR, "Current desk of the employee does not match with the given data");
				}
			} else if(CollectionUtils.isNotEmpty(desklist)){
				V3DeskContext desk = (V3DeskContext) desklist.get(0);
				move.setFrom(desk);
			}
			move.setDepartment(employee.getDepartment());
		}
        }
	}
        
        return false;
	}
	
}
