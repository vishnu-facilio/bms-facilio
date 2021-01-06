package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlGroupAssetCategory;
import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupFieldContext;
import con.facilio.control.ControlGroupRoutineContext;
import con.facilio.control.ControlGroupRoutineSectionContext;
import con.facilio.control.ControlGroupSection;

public class AddControlGroupRoutineCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupRoutineContext routine = (ControlGroupRoutineContext) context.get(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(routine != null) {
			
			List<FacilioField> controlGroupRoutineFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupRoutineContext> insert = new InsertRecordBuilder<ControlGroupRoutineContext>()
	    			.addRecord(routine)
	    			.fields(controlGroupRoutineFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME)
	    			;
	        
	        insert.save();
	        
	        List<ControlGroupRoutineSectionContext> sections = routine.getSections();
	        for(ControlGroupRoutineSectionContext section : sections) {
	        	section.setRoutine(routine);
	        }
	        
	        routine.setSections(null);
	        
	        List<FacilioField> controlGroupRoutineSectionFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_SECTION_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupRoutineSectionContext> insert1 = new InsertRecordBuilder<ControlGroupRoutineSectionContext>()
	    			.addRecords(sections)
	    			.fields(controlGroupRoutineSectionFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_SECTION_MODULE_NAME)
	    			;
	        
			insert1.save();
	        
			List<ControlGroupFieldContext> fields = new ArrayList<ControlGroupFieldContext>();
			for(ControlGroupRoutineSectionContext section : sections) {
				for(ControlGroupFieldContext field : section.getFields()) {
					field.setRoutine(routine);
					field.setRoutineSection(section);
					fields.add(field);
				}
				section.setFields(null);
			}
	        
			List<FacilioField> controlGroupFieldsFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_FIELD_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupFieldContext> insert2 = new InsertRecordBuilder<ControlGroupFieldContext>()
	    			.addRecords(fields)
	    			.fields(controlGroupFieldsFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_FIELD_MODULE_NAME)
	    			;
	        
			insert2.save();
		}
		return false;
	}

}
