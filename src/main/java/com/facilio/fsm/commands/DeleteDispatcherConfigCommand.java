package com.facilio.fsm.commands;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteDispatcherConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long dispatcherId = (long) context.get(FacilioConstants.Dispatcher.DISPATCHER_ID);
        if(dispatcherId >0) {
            deleteDispatcherBoard(dispatcherId);
            deleteDispatcherBoardSharing(dispatcherId);
        }
        return false;
    }
    private void deleteDispatcherBoard(long id) throws Exception{
        FacilioModule dispatcherModule = ModuleFactory.getDispatcherModule();
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(dispatcherModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, dispatcherModule));
        deleteBuilder.delete();
    }

    private void deleteDispatcherBoardSharing(long id)throws Exception{
        FacilioModule dispatcherBoardSharingModule = ModuleFactory.getDispatcherBoardSharingModule();
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(dispatcherBoardSharingModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("Dispatcher_Board_Sharing.PARENT_ID","parentId", String.valueOf(id), NumberOperators.EQUALS));
        deleteBuilder.delete();
    }
}
