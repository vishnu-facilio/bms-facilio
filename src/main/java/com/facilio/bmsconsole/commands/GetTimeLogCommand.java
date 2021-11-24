package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetTimeLogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

//         GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
//                 .select(FieldFactory.getTimeLogFields(null))

        return false;
    }
}
