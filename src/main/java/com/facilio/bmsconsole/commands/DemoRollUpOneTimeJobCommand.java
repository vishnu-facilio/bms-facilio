package com.facilio.bmsconsole.commands;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.actions.DemoRollUpActionAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;

public class DemoRollUpOneTimeJobCommand extends FacilioCommand {

	private static final String TABLE_NAME = "DEMO_ROLLUP_TOOL";
	private static final String ORGID = "orgId";
	private static final String ID = "id";
	private static final String START_TIME = "startTime";
	private static final String JOBNAME = "DemoRollUpYearlyOneTimeJob";
	private static final FacilioModule MODULE = getDemoRollUpModule();
    private static final FacilioField ID_FIELD = FieldFactory.getIdField(ID, "ID", MODULE);
    private static final List<FacilioField> FIELDS = getDemoRollUpFields();
    
    private static FacilioModule getDemoRollUpModule() {
        FacilioModule module = new FacilioModule();
        module.setTableName(TABLE_NAME);
        module.setDisplayName(TABLE_NAME);
        module.setName(TABLE_NAME);
        return module;
    }

    private static List<FacilioField> getDemoRollUpFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(ID_FIELD);
        fields.add(FieldFactory.getStringField(START_TIME, "STARTTIME", MODULE));
        return fields;
    }

    @Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		long orgId = (long) context.get("ORGID");
		ZonedDateTime startTime = (ZonedDateTime) context.get("START_TIME");

		Map<String, Object> props = new HashMap<>();
		props.put(ORGID, orgId);
		props.put(START_TIME, startTime);

		long id = DemoRollUpActionAPI.insertValue(props);

		if (id != 0) {
			FacilioTimer.scheduleOneTimeJobWithDelay(id, JOBNAME, 30, "priority");

		}

		return false;
	}
	
}
