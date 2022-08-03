package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.ConfigureControllerCommand;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import com.facilio.services.filestore.PublicFileUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.Map;

public class DeletePublicFileCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long publicFileId = (Long) context.get(FacilioConstants.ContextNames.PUBLIC_FILE_ID);
        long orgId = AccountUtil.getCurrentOrg().getId();
        Map<String, Object> publicFileFields =FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> PublicFileUtil.getPublicFileObj(publicFileId,orgId));

        long fileId  = (long) publicFileFields.get("fileId");
        boolean isFromFilesTable = (boolean) publicFileFields.get("isFromFilesTable");

        if(!isFromFilesTable){
            FacilioChain deleteFileChain = FacilioChainFactory.getDeleteFileChain();
            FacilioContext facilioContext = deleteFileChain.getContext();

            facilioContext.put(FacilioConstants.ContextNames.FILE_ID, fileId);

            deleteFileChain.execute();

        }

        FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() ->deletePublicFile(publicFileId,orgId));

        return false;
    }
    private int deletePublicFile(long publicFileId,long orgId) throws  Exception {

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getPublicFilesModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(publicFileId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));

        return deleteRecordBuilder.delete();
    }
}
