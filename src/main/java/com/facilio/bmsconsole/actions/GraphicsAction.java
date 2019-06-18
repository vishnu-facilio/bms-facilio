package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class GraphicsAction extends FacilioAction{

	private static final long serialVersionUID = 1L;

	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private GraphicsContext graphics;
	public GraphicsContext getGraphics() {
		return graphics;
	}
	public void setGraphics(GraphicsContext graphics) {
		this.graphics = graphics;
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
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		Chain chain = TransactionChainFactory.getDeleteGraphicsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID, recordId);
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
