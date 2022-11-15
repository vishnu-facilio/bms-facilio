package com.facilio.bmsconsole.imports.job;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportLookupModuleValueNotFoundException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.imports.config.ImportChainUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.json.simple.JSONObject;

import java.util.logging.Logger;

public class ImportDataJob extends FacilioJob {

    private static final Logger LOGGER = Logger.getLogger(ImportDataJob.class.getName());
    public static final String JOB_NAME = "V3_importDataJob";

    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            FacilioChain importChain = ImportChainUtil.getImportChain();
            FacilioContext context = importChain.getContext();
            context.put(FacilioConstants.ContextNames.ID, jobContext.getJobId());
            importChain.execute();
        } catch (Exception ex) {
            LOGGER.severe("Error Occured in ImportData Job -- " + ex);
            throw ex;
        }
    }
}
