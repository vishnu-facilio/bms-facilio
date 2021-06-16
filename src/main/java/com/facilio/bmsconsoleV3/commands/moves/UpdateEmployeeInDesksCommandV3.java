package com.facilio.bmsconsoleV3.commands.moves;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.DeskActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3MovesContext;
import com.facilio.bmsconsoleV3.context.V3MovesContext.MoveType;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.util.DesksAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
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
                                        UpdateDeskInEmployee(toDesk, true); // to update the desk in employee
                                        fromDesk.setEmployee(null);
                                        fromDesk.setDepartment(null);
                                        if(toDesk.getEmployee() != null) {
                                            List<V3MovesContext> moveprop = new ArrayList<V3MovesContext>();
                                            V3MovesContext UnAssignEmployee = new V3MovesContext();
                                            UnAssignEmployee.setTimeOfMove(move.getTimeOfMove());
                                            UnAssignEmployee.setEmployee(toDesk.getEmployee());
                                            UnAssignEmployee.setDepartment(emp.getDepartment());
                                            UnAssignEmployee.setFrom(toDesk);
                                            UnAssignEmployee.setMoveType(MoveType.INSTANT);
                                            moveprop.add(UnAssignEmployee);
                                            V3RecordAPI.addRecord(false,moveprop, movesModule, movesFields);
                                        }
                                        toDesk.setEmployee(emp);
                                        toDesk.setDepartment(emp.getDepartment());
                                        
                                        
                                        Map<Long, List<UpdateChangeSet>> fromDeskChangeSet = V3RecordAPI.updateRecord(fromDesk, deskModule, deskFields, true);
                                        
                                        JSONObject frominfo = new JSONObject();
                                        List<Object> changeList = new ArrayList<Object>();
                                        for (UpdateChangeSet changeset : fromDeskChangeSet.get(fromDesk.getId())) {
                                        	long fieldid = changeset.getFieldId();
                                            Object oldValue = changeset.getOldValue();
                                            Object newValue = changeset.getNewValue();
                                            FacilioField field = modBean.getField(fieldid, FacilioConstants.ContextNames.Floorplan.DESKS);
                                            
                                            JSONObject changeObj = new JSONObject();
                                            changeObj.put("field", field.getName());
                                            changeObj.put("displayName", field.getDisplayName());
                                            changeObj.put("oldValue", oldValue);
                                            changeObj.put("newValue", newValue);
                                            changeList.add(changeObj);
                                            
                                        }
                                        frominfo.put("changeSet", changeList);
                                        frominfo.put("empName", emp.getName());
                                        frominfo.put("deskName",fromDesk.getName());
                                        
                                        DesksAPI.addDeskActivity(fromDesk.getId(), -1, DeskActivityType.UNASSIGN_EMPLOYEE, frominfo);
                                        
                                        Map<Long, List<UpdateChangeSet>> toDeskChangeSet = V3RecordAPI.updateRecord(toDesk, deskModule, deskFields,true);
                                        
                                        JSONObject toinfo = new JSONObject();
                                        List<Object> tochangeList = new ArrayList<Object>();
                                        for (UpdateChangeSet changeset : toDeskChangeSet.get(toDesk.getId())) {
                                        	long fieldid = changeset.getFieldId();
                                            Object oldValue = changeset.getOldValue();
                                            Object newValue = changeset.getNewValue();
                                            FacilioField field = modBean.getField(fieldid, FacilioConstants.ContextNames.Floorplan.DESKS);
                                            
                                            JSONObject changeObj = new JSONObject();
                                            changeObj.put("field", field.getName());
                                            changeObj.put("displayName", field.getDisplayName());
                                            changeObj.put("oldValue", oldValue);
                                            changeObj.put("newValue", newValue);
                                            tochangeList.add(changeObj);
                                            
                                        }
                                        toinfo.put("changeSet", tochangeList);
                                        toinfo.put("empName", emp.getName());
                                        toinfo.put("deskName",toDesk.getName());
                                        
                                        DesksAPI.addDeskActivity(toDesk.getId(), -1, DeskActivityType.ASSIGN_EMPLOYEE, toinfo);
                                        UpdateDeskInEmployee(toDesk, false); // to update the desk in employee
                                    } else if( move.getFrom() != null) {
                                        V3DeskContext fromDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, move.getFrom().getId(), V3DeskContext.class);
                                        UpdateDeskInEmployee(fromDesk, true);
                                        
                                        fromDesk.setEmployee(null);
                                        fromDesk.setDepartment(null);
                                        Map<Long, List<UpdateChangeSet>> fromDeskChangeSet = V3RecordAPI.updateRecord(fromDesk, deskModule, deskFields, true);
                                        
                                        JSONObject frominfo = new JSONObject();
                                        List<Object> changeList = new ArrayList<Object>();
                                        for (UpdateChangeSet changeset : fromDeskChangeSet.get(fromDesk.getId())) {
                                            long fieldid = changeset.getFieldId();
                                            Object oldValue = changeset.getOldValue();
                                            Object newValue = changeset.getNewValue();
                                            FacilioField field = modBean.getField(fieldid, FacilioConstants.ContextNames.Floorplan.DESKS);
                                            
                                            JSONObject changeObj = new JSONObject();
                                            changeObj.put("field", field.getName());
                                            changeObj.put("displayName", field.getDisplayName());
                                            changeObj.put("oldValue", oldValue);
                                            changeObj.put("newValue", newValue);
                                            changeList.add(changeObj);
                                            
                                        }
                                        frominfo.put("changeSet", changeList);
                                        frominfo.put("empName", emp.getName());
                                        frominfo.put("deskName",fromDesk.getName());
                                        
                                        
                                        DesksAPI.addDeskActivity(fromDesk.getId(), -1, DeskActivityType.UNASSIGN_EMPLOYEE, frominfo);
                                    } else {
                                        V3DeskContext toDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, move.getTo().getId(), V3DeskContext.class);
                                        if(toDesk.getEmployee() != null) {
                                            List<V3MovesContext> moveprop = new ArrayList<V3MovesContext>();
                                            V3MovesContext UnAssignEmployee = new V3MovesContext();
                                            UnAssignEmployee.setTimeOfMove(move.getTimeOfMove());
                                            UnAssignEmployee.setEmployee(toDesk.getEmployee());
                                            UnAssignEmployee.setDepartment(emp.getDepartment());
                                            UnAssignEmployee.setFrom(toDesk);
                                            UnAssignEmployee.setMoveType(MoveType.INSTANT);
                                            moveprop.add(UnAssignEmployee);
                                            V3RecordAPI.addRecord(false,moveprop, movesModule, movesFields);
                                            UpdateDeskInEmployee(toDesk, true);

                                        }
                                        toDesk.setEmployee(emp);
                                        toDesk.setDepartment(emp.getDepartment());
                                        UpdateDeskInEmployee(toDesk, false);
                                        Map<Long, List<UpdateChangeSet>> toDeskChangeSet = V3RecordAPI.updateRecord(toDesk, deskModule, deskFields,true);
                                        
                                        JSONObject toinfo = new JSONObject();
                                        List<Object> tochangeList = new ArrayList<Object>();
                                        for (UpdateChangeSet changeset : toDeskChangeSet.get(toDesk.getId())) {
                                            long fieldid = changeset.getFieldId();
                                            Object oldValue = changeset.getOldValue();
                                            Object newValue = changeset.getNewValue();
                                            FacilioField field = modBean.getField(fieldid, FacilioConstants.ContextNames.Floorplan.DESKS);
                                            
                                            JSONObject changeObj = new JSONObject();
                                            changeObj.put("field", field.getName());
                                            changeObj.put("displayName", field.getDisplayName());
                                            changeObj.put("oldValue", oldValue);
                                            changeObj.put("newValue", newValue);
                                            tochangeList.add(changeObj);
                                            
                                        }
                                        toinfo.put("changeSet", tochangeList);
                                        toinfo.put("empName", emp.getName());
                                        toinfo.put("deskName",toDesk.getName());
                                        
                                        DesksAPI.addDeskActivity(toDesk.getId(), -1, DeskActivityType.ASSIGN_EMPLOYEE, toinfo);
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
    
    private boolean UpdateDeskInEmployee(V3DeskContext desk, boolean unassign) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule employeeModule = modBean.getModule("employee");
        List<FacilioField> employeeFields = modBean.getAllFields(employeeModule.getName());
    	
    	V3EmployeeContext employeeDesk = desk.getEmployee();
       
        
    	 if (employeeDesk != null) {
    		V3EmployeeContext employee = (V3EmployeeContext) V3RecordAPI.getRecord("employee", employeeDesk.getId(), V3EmployeeContext.class);

            if (unassign == true) {
        		V3SpaceContext space = new V3SpaceContext();
        		space.setId(-99);
                employee.setSpace(space);
                V3RecordAPI.updateRecord(employee, employeeModule, employeeFields, true);
            }
            else {
    		V3SpaceContext space = new V3SpaceContext();
    		space.setId(desk.getId());
    		employee.setSpace(space);
    		V3RecordAPI.updateRecord(employee, employeeModule, employeeFields, true);
            }


    		
    	}
    	

    	
    	return true;
    	
    }

}
