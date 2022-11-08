package com.facilio.bmsconsoleV3.commands.jobplanTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;

public class FillReadingObjForJobPlanTaskCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		List<JobPlanTasksContext> tasks = ((Map<String, List>) context.get(Constants.RECORD_MAP)).get(moduleName);
		
		if (CollectionUtils.isNotEmpty(tasks)) {
			
			for(JobPlanTasksContext task : tasks) {
				if(task.getReadingFieldId() != null && task.getReadingFieldId() > 0) {
					task.setReadingField(Constants.getModBean().getField(task.getReadingFieldId()));
				}
			}
		}
		
		return false;
	}

}
