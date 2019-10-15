/**
 * 
 */
package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;

/**
 * @author facilio
 *
 */
public class CommissionAction extends FacilioAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String exportPointsModule() throws Exception {
		
		FacilioContext context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, FileFormat.getFileFormat(type));
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, getControllerId());

		FacilioChain exportModule = TransactionChainFactory.getExportPointsChain();
		exportModule.execute(context);
		String fileUrl = (String) context.get(FacilioConstants.ContextNames.FILE_URL);
		setResult("fileUrl", fileUrl);
		return SUCCESS;
	}
	
	
	
	private int type=-1;
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	private long controllerId=-1;

	/**
	 * @return the controllerId
	 */
	public long getControllerId() {
		return controllerId;
	}

	/**
	 * @param controllerId the controllerId to set
	 */
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}

}
