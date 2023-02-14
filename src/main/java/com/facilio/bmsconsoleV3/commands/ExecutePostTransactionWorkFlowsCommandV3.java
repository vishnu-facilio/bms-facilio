package com.facilio.bmsconsoleV3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExecutePostTransactionWorkFlowsCommandV3 extends FacilioCommand implements PostTransactionCommand {

    private Context context;

    private List<Command> commands = new ArrayList<>();

    public ExecutePostTransactionWorkFlowsCommandV3 addCommand(FacilioCommand command) {
        if (command instanceof Serializable) {
            commands.add(command);
        }
        return this;
    }

    @Override
    @WithSpan
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        this.context = context;
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        // TODO Auto-generated method stub
        if (!commands.isEmpty()) {
            Context noSeriablable = new FacilioContext();
            for (Object key : context.keySet()) {
                Object object = context.get(key);
                if (object != null && object instanceof Serializable) {
                    noSeriablable.put(key, object);
                }
            }
            noSeriablable.put(FacilioConstants.Job.FORKED_COMMANDS, commands);
            FacilioTimer.scheduleInstantJob("ForkedChain", (FacilioContext) noSeriablable);
        }
        return false;
    }
}
