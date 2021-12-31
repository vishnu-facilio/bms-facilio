package com.facilio.bmsconsole.context;

import java.io.Serializable;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.util.FacilioUtil;

public class VisitorSettingsContext implements Serializable {

	
	private static final long serialVersionUID = 1L;
	long orgId=-1;
	long visitorTypeId=-1;
	String ndaContent;
	Boolean approvalRequiredForInvite;
	private Boolean responseEnabled;
	
	private Boolean faceRecognitionEnabled;
	public Boolean getResponseEnabled() {
		return responseEnabled;
	}
	public void setResponseEnabled(Boolean responseEnabled) {
		this.responseEnabled = responseEnabled;
	}

	public Boolean getFaceRecognitionEnabled() {
		return faceRecognitionEnabled;
	}
	public void setFaceRecognitionEnabled(Boolean faceRecognitionEnabled) {
		this.faceRecognitionEnabled = faceRecognitionEnabled;
	}
	public Boolean getApprovalRequiredForInvite() {
		return approvalRequiredForInvite;
	}
	public void setApprovalRequiredForInvite(Boolean approvalRequiredForInvite) {
		this.approvalRequiredForInvite = approvalRequiredForInvite;
	}
	public String getNdaContent() {
		if(ndaContent==null)
		{
			return " \n          1. I may be given access to confidential information belonging to (the {{company}}) through my relationship with {{company}} or as a result of\n           my access to {{company}}'s premises.\n\n          2. I understand and acknowledge that {{company}}'s trade secrets consist of information and materials that are valuable and not generally known by\n           {{company}}'s competitors, including:\n\n          (a) Any and all information concerning {{company}}'s current, future or proposed products, including, but not limited to, computer code, drawings, \n          specifications, notebook entries, technical notes and graphs, computer printouts, technical memoranda and correspondence, product development \n          agreements and related agreements.\n\n          (b) Information and materials relating to  {{company}}'s purchasing, accounting, and marketing; including, but not limited to, marketing plans, \n          sales data, unpublished promotional material, cost and pricing information and customer lists.\n\n          (c) Information of the type described above which {{company}} obtained from another party and which {{company}} treats\n          as confidential, whether or not owned or developed by {{company}}.\n\n          (d) Other: __________________________________\n\n          3. In consideration of being admitted to {{company}}'s facilities, I will hold in the strictest confidence any trade secrets or confidential information \n          that is disclosed to me. I will not remove any document, equipment or other materials from the premises without {{company}}'s written permission. I will \n          not photograph or otherwise record any information to which I may have access during my visit.\n\n          4. This Agreement is binding on me, my heirs, executors, administrators and assigns and inures to the benefit of {{company}}, its successors, and \n          assigns.\n          \n          5. This Agreement constitutes the entire understanding between {{company}} and me with respect to its subject matter. It supersedes all earlier \n          representations and understandings, whether oral or written.\n";          
		}
		return ndaContent;
	}
	public void setNdaContent(String ndaContent) {
		this.ndaContent = ndaContent;
	}
	
	public String getFinalNdaContent() {
		if (AccountUtil.getCurrentOrg() != null) {
			return this.getNdaContent().replaceAll("\\{\\{company\\}\\}", AccountUtil.getCurrentOrg().getName());
		}
		return getNdaContent();
	}

	private JSONObject photoSettings;
	public JSONObject getPhotoSettings() {
		if(photoSettings==null)
		{
			JSONObject photoSetttingsDefault=new JSONObject() ;
			photoSetttingsDefault.put("retakeReturningUserPhotos", false);
			return  photoSetttingsDefault;
		
		}
		return photoSettings;
		
	}
	public void setPhotoSettings(JSONObject photoSettings) {
		this.photoSettings = photoSettings;
	}
	
	
	@JSON(serialize = false)
	public String getPhotoSettingsJson() {
		if (this.photoSettings!=null)
		return photoSettings.toJSONString();
		else {
			return null;
		}
	}
	public void setPhotoSettingsJson(String photoSettingsJson) {
		if(photoSettingsJson!=null)
		{
		try {
			this.photoSettings = FacilioUtil.parseJson(photoSettingsJson);
		} catch (ParseException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}
		}
	}
	
	

	
	private JSONObject hostSetttings;
	public JSONObject getHostSettings() {
		if(hostSetttings==null)
		{
			JSONObject hostSettingsDefault=new JSONObject() ;
			hostSettingsDefault.put("requireApproval", false);
			hostSettingsDefault.put("hostType", Integer.toString(ContactsContext.ContactType.EMPLOYEE.getIndex()));
			hostSettingsDefault.put("required", false);
			return hostSettingsDefault;
		}
		return hostSetttings;
		
	}
	public void setHostSettings(JSONObject hostSetttings) {
		this.hostSetttings = hostSetttings;
	}
	
	
	@JSON(serialize = false)
	public String getHostSettingsJson() {
		if (this.hostSetttings!=null)
		return hostSetttings.toJSONString();
		else {
			return null;
		}
	}
	public void setHostSettingsJson(String hostSettingsJson) {
		if(hostSettingsJson!=null)
		{
		try {
			this.hostSetttings = FacilioUtil.parseJson(hostSettingsJson);
		} catch (ParseException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}
		}
	}
	
	
	
	
	private Long successId;
	private Long failureId;
	public Long getSuccessId() {
		return successId;
	}
	public void setSuccessId(Long successId) {
		this.successId = successId;
	}
	public Long getFailureId() {
		return failureId;
	}
	public void setFailureId(Long failureId) {
		this.failureId = failureId;
	}

	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getVisitorTypeId() {
		return visitorTypeId;
	}
	public void setVisitorTypeId(long visitorTypeId) {
		this.visitorTypeId = visitorTypeId;
	}
	

	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}
	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}

	public long getAutoSignoutTime() {
		return autoSignoutTime;
	}
	public void setAutoSignoutTime(long autoSignoutTime) {
		this.autoSignoutTime = autoSignoutTime;
	}
	public long getVisitorLogFormId() {
		return visitorLogFormId;
	}
	public void setVisitorLogFormId(long visitorLogFormId) {
		this.visitorLogFormId = visitorLogFormId;
	}
	public long getVisitorInviteFormId() {
		return visitorInviteFormId;
	}
	public void setVisitorInviteFormId(long visitorInviteFormId) {
		this.visitorInviteFormId = visitorInviteFormId;
	}
	public FacilioForm getVisitorLogForm() {
		return visitorLogForm;
	}
	public void setVisitorLogForm(FacilioForm visitorLogForm) {
		this.visitorLogForm = visitorLogForm;
	}
	public FacilioForm getVisitorInviteForm() {
		return visitorInviteForm;
	}
	public void setVisitorInviteForm(FacilioForm visitorInviteForm) {
		this.visitorInviteForm = visitorInviteForm;
	}

	long visitorLogFormId=-1;
	long visitorInviteFormId=-1;
	FacilioForm visitorLogForm;
	FacilioForm visitorInviteForm;
	
	VisitorTypeContext visitorType;
	
	public Boolean getHostEnabled() {
		return hostEnabled;
	}
	public void setHostEnabled(Boolean hostEnabled) {
		this.hostEnabled = hostEnabled;
	}
	public Boolean getNdaEnabled() {
		return ndaEnabled;
	}
	public void setNdaEnabled(Boolean ndaEnabled) {
		this.ndaEnabled = ndaEnabled;
	}
	public Boolean getBadgeEnabled() {
		return badgeEnabled;
	}
	public void setBadgeEnabled(Boolean badgeEnabled) {
		this.badgeEnabled = badgeEnabled;
	}
	public Boolean getPhotoEnabled() {
		return photoEnabled;
	}
	public void setPhotoEnabled(Boolean photoEnabled) {
		this.photoEnabled = photoEnabled;
	}
	Boolean hostEnabled;
	Boolean ndaEnabled;
	Boolean badgeEnabled;
	Boolean photoEnabled;
	Boolean idScanEnabled;
	public Boolean getIdScanEnabled() {
		return idScanEnabled;
	}
	public void setIdScanEnabled(Boolean idScanEnabled) {
		this.idScanEnabled = idScanEnabled;
	}
	long autoSignoutTime;
//	

	
	
}
