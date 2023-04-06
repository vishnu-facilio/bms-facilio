package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AddWorkOrderNotesFromAlarmCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
        if (note != null) {
            WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
            FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
            FacilioContext noteContext = addNote.getContext();
            note.setParentId(workOrder.getId());
            noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
            noteContext.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
            noteContext.put(FacilioConstants.ContextNames.NOTE, note);
            noteContext.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
            addNote.execute();
        }

        return false;
    }
}
