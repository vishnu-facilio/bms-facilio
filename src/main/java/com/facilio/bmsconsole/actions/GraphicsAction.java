package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.chain.FacilioChain;
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
	private boolean fetchOnlyMeta;

	
	public boolean isFetchOnlyMeta() {
		return fetchOnlyMeta;
	}
	public void setFetchOnlyMeta(boolean fetchOnlyMeta) {
		this.fetchOnlyMeta = fetchOnlyMeta;
	}
	public String getGraphicsList() throws Exception {
		FacilioContext context = new FacilioContext();
 		
		FacilioChain chain = ReadOnlyChainFactory.getGraphicsListChain();
		chain.execute(context);
		
		List<GraphicsContext> graphicsList = (List<GraphicsContext>) context.get(FacilioConstants.ContextNames.GRAPHICS_LIST);
		setResult(FacilioConstants.ContextNames.GRAPHICS_LIST, graphicsList);
		
		return SUCCESS;
	}

	public String addGraphics() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GRAPHICS, graphics);
		
		FacilioChain chain = TransactionChainFactory.getAddGraphicsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.GRAPHICS, graphics);
		return SUCCESS;
	}
	
	public String getGraphicsDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		context.put(FacilioConstants.ContextNames.FETCH_ONLY_META,fetchOnlyMeta);
		
		FacilioChain chain = ReadOnlyChainFactory.getGraphicsDetailsChain();
		chain.execute(context);
		
		GraphicsContext graphicsContext = (GraphicsContext) context.get(FacilioConstants.ContextNames.GRAPHICS);
		setResult(FacilioConstants.ContextNames.GRAPHICS, graphicsContext);
		
		return SUCCESS;
	}
	
	public String deleteGraphics() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		FacilioChain chain = TransactionChainFactory.getDeleteGraphicsChain();
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

		FacilioChain updateGraphicsChain = TransactionChainFactory.getUpdateGraphicsChain();
		updateGraphicsChain.execute(context);
		setRecordId(graphics.getId());
		setResult(FacilioConstants.ContextNames.GRAPHICS, graphics);
		return SUCCESS;
	}
	
	private long assetCategoryId = -1;
	public long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	
	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	public String getGraphicsForAssetCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ASSET_CATEGORY, assetCategoryId);
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
		
		FacilioChain chain = ReadOnlyChainFactory.getGraphicsForAssetCategoryChain();
		chain.execute(context);
		
		List<GraphicsContext> graphicsList = (List<GraphicsContext>) context.get(FacilioConstants.ContextNames.GRAPHICS_LIST);
		setResult(FacilioConstants.ContextNames.GRAPHICS_LIST, graphicsList);
		
		return SUCCESS;
	}
}
