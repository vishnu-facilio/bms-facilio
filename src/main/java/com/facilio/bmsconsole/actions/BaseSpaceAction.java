package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class BaseSpaceAction extends ActionSupport {
	
	@SuppressWarnings("unchecked")
	public String baseSpaceList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain getAllSpace = FacilioChainFactory.getAllAreaChain();
		getAllSpace.execute(context);
		
		setModuleName("Base Space");
		setBasespaces((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	
	public String childrenList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACE_ID, getSpaceId());
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, getSpaceType());
		
		Chain getAllSpace = FacilioChainFactory.getBaseSpaceChildrenChain();
		getAllSpace.execute(context);
		
		setBasespaces((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	
	private String moduleName;
	public String getModuleName() 
	{
		return moduleName;
	}
	public void setModuleName(String moduleName) 
	{
		this.moduleName = moduleName;
	}
	
	private ActionForm actionForm;
	public ActionForm getActionForm() 
	{
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) 
	{
		this.actionForm = actionForm;
	}
	
	private List<String> customFieldNames;
	public List<String> getCustomFieldNames() 
	{
		return customFieldNames;
	}
	public void setCustomFieldNames(List<String> customFieldNames) 
	{
		this.customFieldNames = customFieldNames;
	}
	
	private List<BaseSpaceContext> basespaces;
	public List<BaseSpaceContext> getBasespaces() 
	{
		return basespaces;
	}
	public void setBasespaces(List<BaseSpaceContext> basespaces) 
	{
		this.basespaces = basespaces;
	}
	
	public String getViewDisplayName()
	{
		return "All Spaces";
	}
	
	public List<BaseSpaceContext> getRecords() 
	{
		return basespaces;
	}
	
	private long spaceId;
	
	public long getSpaceId() {
		return this.spaceId;
	}
	
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	
	private String spaceType;

	public String getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(String spaceType) {
		this.spaceType = spaceType;
	}
	
	private File avatar;
	
	public File getAvatar() {
		return avatar;
	}
	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}
	
	private String avatarFileName;

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}
	
	private String avatarContentType;
	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	private String avatarUrl;
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	public String getAvatarUrl() {
		return this.avatarUrl;
	}
	
	public String uploadSpaceAvatar() throws Exception {
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		long fileId = fs.addFile(getAvatarFileName(), getAvatar(), getAvatarContentType());
		
		UserAPI.updateUserPhoto(spaceId, fileId, null);
		
		setAvatarUrl(fs.getPrivateUrl(fileId));
		
		return SUCCESS;
	}
}