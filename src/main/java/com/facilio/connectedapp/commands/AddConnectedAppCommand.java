package com.facilio.connectedapp.commands;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddConnectedAppCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ConnectedAppContext connectedAppContext = (ConnectedAppContext) context.get(FacilioConstants.ContextNames.RECORD);
        if (connectedAppContext != null) {

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getConnectedAppsModule().getTableName())
                    .fields(FieldFactory.getConnectedAppFields());

            Map<String, Object> props = FieldUtil.getAsProperties(connectedAppContext);

            insertBuilder.addRecord(props);
            insertBuilder.save();
            Long recordId = (Long) props.get("id");
            connectedAppContext.setId(recordId);

            context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP, connectedAppContext);
        }

        return false;
    }
}
