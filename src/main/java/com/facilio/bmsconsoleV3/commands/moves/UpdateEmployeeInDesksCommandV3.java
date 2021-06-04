package com.facilio.bmsconsoleV3.commands.moves;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3MovesContext;
import com.facilio.bmsconsoleV3.context.V3MovesContext.MoveType;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class UpdateEmployeeInDesksCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        FacilioModule movesModule = modBean.getModule(moduleName);
		List<FacilioField> movesFields = modBean.getAllFields(movesModule.getName());
		
		if(moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) 
		{
			List records = recordMap.get(moduleName);
			if(records != null && !records.isEmpty()) 
			{		
				FacilioModule deskModule = modBean.getModule("desks");
				List<FacilioField> deskFields = modBean.getAllFields(deskModule.getName());
				
				for(Object record:records) 
				{
					if(record != null && (V3MovesContext)record != null)
					{
						V3MovesContext move = (V3MovesContext)record;
						if (move.getTimeOfMove() != null && move.getTimeOfMove() <= System.currentTimeMillis()) {
							if (move != null) {
								if( move.getEmployee() != null && move.getEmployee().getId() > 0) {
									V3EmployeeContext emp = (V3EmployeeContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, move.getEmployee().getId(), V3EmployeeContext.class);
	
									if(move.getFrom() != null && move.getTo() != null) {
										V3DeskContext toDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, move.getTo().getId(), V3DeskContext.class);
										V3DeskContext fromDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, move.getFrom().getId(), V3DeskContext.class);
										fromDesk.setEmployee(null);
										fromDesk.setDepartment(null);
										if(toDesk.getEmployee() != null) {
											List<V3MovesContext> moveprop = new ArrayList<V3MovesContext>();
											V3MovesContext UnAssignEmployee = new V3MovesContext();
											UnAssignEmployee.setTimeOfMove(move.getTimeOfMove());
											UnAssignEmployee.setEmployee(toDesk.getEmployee());
											UnAssignEmployee.setDepartment(toDesk.getDepartment());
											UnAssignEmployee.setFrom(toDesk);
											UnAssignEmployee.setMoveType(MoveType.INSTANT);
											moveprop.add(UnAssignEmployee);
											V3RecordAPI.addRecord(false,moveprop, movesModule, movesFields);
										}
										toDesk.setEmployee(emp);
										toDesk.setDepartment(emp.getDepartment());
										V3RecordAPI.updateRecord(fromDesk, deskModule, deskFields);
										V3RecordAPI.updateRecord(toDesk, deskModule, deskFields);
									} else if( move.getFrom() != null) {
										V3DeskContext fromDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, move.getFrom().getId(), V3DeskContext.class);
										fromDesk.setEmployee(null);
										fromDesk.setDepartment(null);
										V3RecordAPI.updateRecord(fromDesk, deskModule, deskFields);
									} else {
										V3DeskContext toDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, move.getTo().getId(), V3DeskContext.class);
										if(toDesk.getEmployee() != null) {
											List<V3MovesContext> moveprop = new ArrayList<V3MovesContext>();
											V3MovesContext UnAssignEmployee = new V3MovesContext();
											UnAssignEmployee.setTimeOfMove(move.getTimeOfMove());
											UnAssignEmployee.setEmployee(toDesk.getEmployee());
											UnAssignEmployee.setDepartment(toDesk.getDepartment());
											UnAssignEmployee.setFrom(toDesk);
											UnAssignEmployee.setMoveType(MoveType.INSTANT);
											moveprop.add(UnAssignEmployee);
											V3RecordAPI.addRecord(false,moveprop, movesModule, movesFields);
										}
										toDesk.setEmployee(emp);
										toDesk.setDepartment(emp.getDepartment());
										V3RecordAPI.updateRecord(toDesk, deskModule, deskFields);
									}
								}
							}
						}
					}
				}
			}	
		}

		return false;
	}

}
