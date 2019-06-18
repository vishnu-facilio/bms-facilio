package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class GraphicsAction extends FacilioAction{

	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
    private List<Long> recordIds;
	
	public List<Long> getRecordIds() {
		return recordIds;
	}
	public void setRecordIds(List<Long> recordIds) {
		this.recordIds = recordIds;
	}

	private Boolean fetchCount;
	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}
	
	private GraphicsContext graphics;
	public GraphicsContext getGraphics() {
		return graphics;
	}
	public void setGraphics(GraphicsContext graphics) {
		this.graphics = graphics;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	private int status;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	
	public String getGraphicsList() throws Exception {
		FacilioContext context = new FacilioContext();
 		
		Chain chain = ReadOnlyChainFactory.getGraphicsListChain();
		chain.execute(context);
		
		List<GraphicsContext> graphicsList = (List<GraphicsContext>) context.get(FacilioConstants.ContextNames.GRAPHICS_LIST);
		setResult(FacilioConstants.ContextNames.GRAPHICS_LIST, graphicsList);
		
		return SUCCESS;
	}

	public String addGraphics() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GRAPHICS, graphics);
		
		Chain chain = TransactionChainFactory.getAddGraphicsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.GRAPHICS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getGraphicsDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		Chain chain = ReadOnlyChainFactory.getGraphicsDetailsChain();
		chain.execute(context);
		
		GraphicsContext graphicsContext = (GraphicsContext) context.get(FacilioConstants.ContextNames.GRAPHICS);
		setResult(FacilioConstants.ContextNames.GRAPHICS, graphicsContext);
		
		return SUCCESS;
	}
	
	public String deleteGraphics() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		Chain chain = TransactionChainFactory.getDeleteGraphicsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	public String graphicsCount() throws Exception {
		return getGraphicsList();
	}
	
	public String updateGraphics() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GRAPHICS, graphics);

		Chain updateGraphicsChain = TransactionChainFactory.getUpdateGraphicsChain();
		updateGraphicsChain.execute(context);
		setRecordId(graphics.getId());
		setResult(FacilioConstants.ContextNames.GRAPHICS, graphics);
		return SUCCESS;
	}


}
