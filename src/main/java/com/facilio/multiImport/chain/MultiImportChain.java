package com.facilio.multiImport.chain;

import com.facilio.multiImport.command.ImportFileUploadCommand;
import com.facilio.multiImport.command.AddImportDataCommand;

import com.facilio.chain.FacilioChain;

public class MultiImportChain {

    public static FacilioChain getCreateImportDataChain(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new AddImportDataCommand());
        return chain;
    }

    public static FacilioChain getImportFileUploadChain(){
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new ImportFileUploadCommand());
        return chain;
    }
}
