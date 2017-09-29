package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;

public class FormLayout {
	
	public static List<NewPanel> getNewTicketLayout(List<FacilioField> fields)
	{

		List<NewPanel> panels =new ArrayList<NewPanel>();
		
		NewPanel first =  new NewPanel(NewPanel.Type.HALF);
		panels.add(first);
		
		NewPanel second =  new NewPanel(NewPanel.Type.HALF);
		panels.add(second);
		
		FacilioField requesterEmail = new FacilioField();
		requesterEmail.setName("email");
		requesterEmail.setDisplayName("Requester Email");
		requesterEmail.setDisplayType(FacilioField.FieldDisplayType.EMAIL);
		requesterEmail.setDataType(com.facilio.bmsconsole.modules.FieldType.STRING);
		requesterEmail.setModuleName("requester");
		requesterEmail.setDefault(true);
		second.addField(requesterEmail);
		

//		NewPanel third =  new NewPanel(NewPanel.Type.FULL).setTitle("Scheduling");
//		panels.add(third);
		
		NewPanel fourth =  new NewPanel(NewPanel.Type.HALF);
		panels.add(fourth);
		
		NewPanel fifth =  new NewPanel(NewPanel.Type.HALF);
		panels.add(fifth);
		
		for(FacilioField field : fields) {
			if(field.getName().equals("noOfClosedTasks") || field.getName().equals("noOfTasks") || field.getName().equals("noOfAttachments") || field.getName().equals("noOfNotes") || field.getName().equals("serialNumber") || field.getName().equals("parentWorkOrder") || field.getName().equals("assetId") || field.getName().equals("ticket") || field.getName().equals("sourceType") || field.getName().equals("requester") || field.getName().equals("createdTime")) {
				continue;
			}
			if(field.getName().equals("subject") || field.getName().equals("description") || field.getName().equals("assignedTo") || field.getName().equals("location") || field.getName().equals("assetId") || field.getName().equals("space") || field.getName().equals("assignmentGroup") ) {
				first.addField(field);
			}
			else if(field.getName().equals("scheduledStart") || field.getName().equals("actualWorkStart")){
				//fourth.addField(field);
			}
			else if(field.getName().equals("estimatedEnd") || field.getName().equals("actualWorkEnd")) {
				//fifth.addField(field);
			}
			else {
				second.addField(field);
			}
		}
		FacilioField sendForApproval = new FacilioField();
		sendForApproval.setName("sendForApproval");
		sendForApproval.setDisplayName("Send For Approval");
		sendForApproval.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
		sendForApproval.setDataType(com.facilio.bmsconsole.modules.FieldType.BOOLEAN);
		sendForApproval.setModuleName("ticket");
		sendForApproval.setDefault(true);
		first.addField(sendForApproval);
		
		return panels;
		
	}
	
	
	public static List<NewPanel> getNewCampusLayout(List<FacilioField> fields)
	{
		List<NewPanel> panels = new ArrayList<NewPanel>();
		
		NewPanel first =  new NewPanel(NewPanel.Type.HALF);
		NewPanel second =  new NewPanel(NewPanel.Type.HALF);
		
		for(FacilioField field : fields) {
			if(field.getName().equals("baseSpaceId")) {
				continue;
			}
			if(field.getName().equals("name") || field.getName().equals("location") || field.getName().equals("description")) {
				first.addField(field);
			}
			else {
				second.addField(field);
			}
		}
		
		panels.add(first);
		panels.add(second);
		
		return panels;
	}
	
	public static List<NewPanel> getNewBuildingLayout(List<FacilioField> fields)
	{
		List<NewPanel> panels = new ArrayList<NewPanel>();
		
		NewPanel first =  new NewPanel(NewPanel.Type.FULL);
		NewPanel second =  new NewPanel(NewPanel.Type.HALF);
		NewPanel third =  new NewPanel(NewPanel.Type.HALF);
		 
		for(FacilioField field : fields) {
			if(field.getName().equals("baseSpaceId")) {
				continue;
			}
			if(field.getName().equals("name")) {
				first.addField(field);
			}
			else if(field.getName().equals("campus") || field.getName().equals("floors") || field.getName().equals("location")){
				second.addField(field);
			}
			else {
				third.addField(field);
			}
		}
		panels.add(first);
		panels.add(second);
		panels.add(third);
		
		return panels;
	}
	
	public static List<NewPanel> getNewFloorLayout(List<FacilioField> fields)
	{
		List<NewPanel> panels = new ArrayList<NewPanel>();
		NewPanel first =  new NewPanel(NewPanel.Type.FULL);
		NewPanel second =  new NewPanel(NewPanel.Type.HALF);
		NewPanel third =  new NewPanel(NewPanel.Type.HALF);
		
		for(FacilioField field : fields) {
			if(field.getName().equals("baseSpaceId") || field.getName().equals("mainLevel")) {
				continue;
			}
			if(field.getName().equals("name")) {
				first.addField(field);
			}
			else if(field.getName().equals("building") || field.getName().equals("area")) {
				second.addField(field);
			}
			else {
				third.addField(field);
			}
		}
		
		panels.add(first);
		panels.add(second);
		panels.add(third);
		return panels;
	}
	
	public static List<NewPanel> getNewSpaceLayout(List<FacilioField> fields)
	{
		List<NewPanel> panels = new ArrayList<NewPanel>();
		NewPanel first =  new NewPanel(NewPanel.Type.FULL);
		NewPanel second =  new NewPanel(NewPanel.Type.HALF);
		NewPanel third =  new NewPanel(NewPanel.Type.HALF);
		
		for(FacilioField field : fields) {
			if(field.getName().equals("baseSpaceId") || field.getName().equals("displayName") || field.getName().equals("availability") || field.getName().equals("occupiable")) {
				continue;
			}
			if(field.getName().equals("name")) {
				first.addField(field);
			}
			else if(field.getName().equals("building") || field.getName().equals("floor")) {
				second.addField(field);
			}
			else {
				third.addField(field);
			}
		}
		
		panels.add(first);
		panels.add(second);
		panels.add(third);
		return panels;
	}
	
	public static List<NewPanel> getNewZoneLayout(List<FacilioField> fields)
	{
		List<NewPanel> panels = new ArrayList<NewPanel>();
		
		NewPanel first =  new NewPanel(NewPanel.Type.FULL);
		for(FacilioField field : fields) {
			first.addField(field);
		}
		panels.add(first);
		return panels;
	}
	
	public static List<NewPanel> getNewLocationLayout(List<FacilioField> fields)
	{
		List<NewPanel> panels = new ArrayList<NewPanel>();
		
		NewPanel first =  new NewPanel(NewPanel.Type.FULL);
		for(FacilioField field : fields) {
			first.addField(field);
		}
		panels.add(first);
		return panels;
	}
	
	public static List<NewPanel> getNewSkillLayout(List<FacilioField> fields)
	{
		List<NewPanel> panels = new ArrayList<NewPanel>();
		
		NewPanel first =  new NewPanel(NewPanel.Type.FULL);
		for(FacilioField field : fields) {
			first.addField(field);
		}
		panels.add(first);
		return panels;
	}
	
	public static List<Panel> getNewNoteLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		
		FacilioField field = new FacilioField();
		field.setName("body");
		field.setDisplayName("Content");
		field.setDataType(FieldType.STRING);
		field.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
		field.setModuleName("note");
		field.setDefault(true);
		
		first.add(field);
		fields.add(first);
		
		return fields;
	}
}
class Panel extends ArrayList<FacilioField>
{
	public enum Type {
		
		QUARTER(3),
		ONE_THIRD(4),
		HALF(6),
		TWO_THIRD(8),
		THREE_FOURTH(9),
		FULL(12);
		
		int value;
		Type(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	public Type getDisplay() {
		return display;
	}
	public void setDisplay(Type display) {
		this.display = display;
	}
	Type display;
	public Panel(Type display)
	{
		this.display =display;
	}
	
	String title;
	public Panel setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getTitle() {
		return this.title;
	}
}
class FileField
{
	public static final String DISPLAY_TYPE_SIMPLE = "simple";
	
	public static final String DISPLAY_TYPE_SECTION = "section";
	
	String displayType = DISPLAY_TYPE_SIMPLE; // 'simple' or 'section'
	int maxFiles = 20; // default 20 files
	int maxAllowedSize = 5242880; // default 5 MB per file
	String allowedExtensions = "*"; // default all files
	public FileField() {
		super();
	}
	
	public FileField setDisplayType(String displayType) {
		this.displayType = displayType;
		return this;
	}
	
	public String getDisplayType() {
		return this.displayType;
	}
	
	public FileField setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
		return this;
	}
	
	public int getMaxFiles() {
		return this.maxFiles;
	}
	
	public FileField setMaxAllowedSize(int maxAllowedSize) {
		this.maxAllowedSize = maxAllowedSize;
		return this;
	}
	
	public int getMaxAllowedSize() {
		return this.maxAllowedSize;
	}
	
	public FileField setAllowedExtensions(String allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
		return this;
	}
	
	public String getAllowedExtensions() {
		return this.allowedExtensions;
	}
}