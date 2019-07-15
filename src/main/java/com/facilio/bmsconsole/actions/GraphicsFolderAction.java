package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.GraphicsFolderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class GraphicsFolderAction extends FacilioAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	public GraphicsFolderContext getGraphicsFolder() {
		return graphicsFolder;
	}
	public void setGraphicsFolder(GraphicsFolderContext graphicsFolder) {
		this.graphicsFolder = graphicsFolder;
	}
	private long recordId;
	private GraphicsFolderContext graphicsFolder;
	
	public String getGraphicsFolderList() throws Exception {
		FacilioContext context = new FacilioContext();
 		
		Chain chain = ReadOnlyChainFactory.getGraphicsFolderListChain();
		chain.execute(context);
		
		List<GraphicsFolderContext> graphicsList = (List<GraphicsFolderContext>) context.get(FacilioConstants.ContextNames.GRAPHICS_FOLDERS);
		setResult(FacilioConstants.ContextNames.GRAPHICS_FOLDERS, graphicsList);
		
		return SUCCESS;
	}
	
	public String getGraphicsFolderDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		Chain chain = ReadOnlyChainFactory.getGraphicsFolderDetailsChain();
		chain.execute(context);
		
		GraphicsFolderContext graphicsFolderContext = (GraphicsFolderContext) context.get(FacilioConstants.ContextNames.GRAPHICS_FOLDER);
		setResult(FacilioConstants.ContextNames.GRAPHICS_FOLDER, graphicsFolderContext);
		
		return SUCCESS;
	}
	
	public String addGraphicsFolder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GRAPHICS_FOLDER, graphicsFolder);
		
		Chain chain = TransactionChainFactory.getAddGraphicsFolderChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.GRAPHICS_FOLDER, graphicsFolder);
		return SUCCESS;
	}
	
	public String deleteGraphicsFolder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		Chain chain = TransactionChainFactory.getDeleteGraphicsFolderChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID, recordId);
		return SUCCESS;
	}
	
	public String updateGraphicsFolder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GRAPHICS_FOLDER, graphicsFolder);

		Chain updateGraphicsChain = TransactionChainFactory.getUpdateGraphicsFolderChain();
		updateGraphicsChain.execute(context);
		setRecordId(graphicsFolder.getId());
		setResult(FacilioConstants.ContextNames.GRAPHICS_FOLDER, graphicsFolder);
		return SUCCESS;
	}
	
	

}
