package com.facilio.multiImport.chain;

import com.facilio.bmsconsole.commands.GenerateCriteriaFromFilterForNonModulesCommand;
import com.facilio.command.FacilioCommand;
import com.facilio.multiImport.command.*;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Context;

public class MultiImportChain {

    public static FacilioChain getCreateImportDataChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new AddImportDataCommand());
        return chain;
    }

    public static FacilioChain getImportFileUploadChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new ImportFileUploadCommand());
        return chain;
    }

    public static FacilioChain getImportSheetMappingChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new ImportSheetMappingCommand());
        return chain;
    }

    public static FacilioChain getPredictSheetExecutionChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new PredictSheetExecutionOrderCommand());
        return chain;
    }

    public static FacilioChain getReorderSheetExecutionChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new ReorderSheetsExecutionCommand());
        return chain;
    }

    public static FacilioChain getImportFileListChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new GetMultiImportFileListCommand());
        return chain;
    }

    public static FacilioChain getImportSheetsListChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new GetMultiImportSheetListCommand());
        return chain;
    }

    public static FacilioChain getDeleteImportFileChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new DeleteImportFileCommand());
        return chain;
    }

    public static FacilioChain getImportFieldMappingChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new ImportMandatoryFieldsValidationCommand());
        chain.addCommand(new ImportFieldsMappingCommand());
        return chain;
    }

    public static FacilioChain scheduleParseAndImportDataJob() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new ScheduleMultiImportParseDataJobCommand());
        return chain;
    }

    public static FacilioChain scheduleV3_MultiImportDataJob() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new ScheduleMultiImportDataJobCommand());
        return chain;
    }

    public static FacilioChain getImportListChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new GenerateCriteriaFromFilterForNonModulesCommand());
        chain.addCommand(new MultiImportListFilterCommand());
        chain.addCommand(new ListMultiImportDataDetailsCommand());
        chain.addCommand(new GetImportListSupplementsCommand());
        return chain;
    }

    public static FacilioChain getCountChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new GenerateCriteriaFromFilterForNonModulesCommand());
        chain.addCommand(new MultiImportListFilterCommand());
        chain.addCommand(new GetImportListCountCommand());
        return chain;
    }
    public static FacilioChain getAbortChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        chain.addCommand(new AbortMultiImportCommand());
        return chain;
    }
    public static FacilioChain getImportDataDetails(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new MultiImportMetaValidationCommand());
        return chain;
    }
}
