package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PrinterContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class PrinterAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	
	private PrinterContext printer;
	
    public PrinterContext getPrinter() {
		return printer;
	}

	public void setPrinter(PrinterContext printer) {
		this.printer = printer;
	}

	public String addOrUpdate() throws Exception
    {
    	
		FacilioChain addPrinterChain = TransactionChainFactory.addOrUpdatePrinterChain();
		
    	FacilioContext printerChainContext = addPrinterChain.getContext();
        printerChainContext.put(FacilioConstants.ContextNames.RECORD, getPrinter());   	    			
    	addPrinterChain.execute();			
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, printerChainContext.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		return SUCCESS;		
    }
	
//	public String deletePrinter() throws Exception
//    {
//    	
//
//		return SUCCESS;		
//    }
	

	
	
	
    
	
	
}