package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Chain;

import java.util.List;

public class BaseLineAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String addBaseLine() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BASE_LINE, baseLine);
		Chain addBlChain = FacilioChainFactory.addBaseLineChain();
		addBlChain.execute(context);
		
		return SUCCESS;
	}
	
	private BaseLineContext baseLine;
	public BaseLineContext getBaseLine() {
		return baseLine;
	}
	public void setBaseLine(BaseLineContext baseLine) {
		this.baseLine = baseLine;
	}
	
	public String getAllBaseLineList() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain getBaseLineChain = FacilioChainFactory.getAllBaseLinesChain();
		getBaseLineChain.execute(context);
		
		baseLines = (List<BaseLineContext>) context.get(FacilioConstants.ContextNames.BASE_LINE_LIST);
		return SUCCESS;
	}
	
	public String getBaseLineList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACE_ID, space);
		
		Chain getBaseLineChain = FacilioChainFactory.getBaseLinesOfSpaceChain();
		getBaseLineChain.execute(context);
		
		baseLines = (List<BaseLineContext>) context.get(FacilioConstants.ContextNames.BASE_LINE_LIST);
		return SUCCESS;
	}
	
	private List<BaseLineContext> baseLines;
	public List<BaseLineContext> getBaseLines() {
		return baseLines;
	}
	public void setBaseLines(List<BaseLineContext> baseLines) {
		this.baseLines = baseLines;
	}

	private long space = -1;
	public long getSpace() {
		return space;
	}
	public void setSpace(long space) {
		this.space = space;
	}
	
	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
	public String addReportBaseLines() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, baseLines);
		context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
		
		Chain addReportBl = FacilioChainFactory.addReportBaseLinesChain();
		addReportBl.execute(context);
		
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		
		return SUCCESS;
	}
	
	public String updateReportBaseLines() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, baseLines);
		context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
		
		Chain addReportBl = FacilioChainFactory.updateReportBaseLinesChain();
		addReportBl.execute(context);
		
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		
		return SUCCESS;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
