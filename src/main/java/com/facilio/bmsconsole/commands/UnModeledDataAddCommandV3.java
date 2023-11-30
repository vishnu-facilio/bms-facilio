
package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import org.apache.commons.chain.Context;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UnModeledDataAddCommandV3 extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Map<String, Object>> records = (List<Map<String, Object>>)context.get(FacilioConstants.ContextNames.DataProcessor.UNMODELED_RECORDS);
        insertUnmodeledData(records);
        return false;
    }
    private void insertUnmodeledData(List<Map<String, Object>> records) throws SQLException {

        if (records.isEmpty()) {
            return;
        }
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getUnmodeledDataFields())
                .table(AgentConstants.UNMODELED_DATA_TABLE)
                .addRecords(records);
        insertBuilder.save();
    }
}
