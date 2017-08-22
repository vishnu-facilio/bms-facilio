package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;

public class FormLayout {
	
	public static List<Panel> getNewTicketLayout(List<FacilioField> fields)
	{

		List<Panel> panels =new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		Panel second =  new Panel(Panel.Type.HALF);
		FacilioField requesterEmail = new FacilioField();
		requesterEmail.setName("email");
		requesterEmail.setDisplayName("Requester Email");
		requesterEmail.setDisplayType(FacilioField.FieldDisplayType.EMAIL);
		requesterEmail.setDataType(com.facilio.bmsconsole.modules.FieldType.STRING);
		requesterEmail.setModuleName("workorder.requester");
		requesterEmail.setDefault(true);
		second.add(requesterEmail);
		
		for(FacilioField field : fields) {
			if(field.getName().equals("parentWorkOrder") || field.getName().equals("assetId") || field.getName().equals("scheduleId") || field.getName().equals("ticket") || field.getName().equals("sourceType") || field.getName().equals("requester")) {
				continue;
			}
			if(field.getName().equals("subject") || field.getName().equals("description") || field.getName().equals("assignedTo") || field.getName().equals("location") || field.getName().equals("assetId") || field.getName().equals("createdDate") || field.getName().equals("assignmentGroup")) {
				first.add(field);
			}
			else {
				second.add(field);
			}
		}
		panels.add(first);
		panels.add(second);
		
		Panel third =  new Panel(Panel.Type.FULL).setTitle("Scheduling");
		panels.add(third);
		
		Panel fourth =  new Panel(Panel.Type.HALF);
		FacilioField scheduleStart = new FacilioField();
		scheduleStart.setName("scheduledStart");
		scheduleStart.setDisplayName("Scheduled Start");
		scheduleStart.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
		scheduleStart.setDataType(com.facilio.bmsconsole.modules.FieldType.DATE_TIME);
		scheduleStart.setModuleName("ticket.schedule");
		scheduleStart.setDefault(true);
		fourth.add(scheduleStart);
	 
		FacilioField actualWorkStart = new FacilioField();
		actualWorkStart.setName("actualWorkStart");
		actualWorkStart.setDisplayName("Actual Work Start");
		actualWorkStart.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
		actualWorkStart.setDataType(com.facilio.bmsconsole.modules.FieldType.DATE_TIME);
		actualWorkStart.setModuleName("ticket.schedule");
		actualWorkStart.setDefault(true);
		fourth.add(actualWorkStart);
		panels.add(fourth);
		
		Panel fifth =  new Panel(Panel.Type.HALF);
		FacilioField estimatedEnd = new FacilioField();
		estimatedEnd.setName("estimatedEnd");
		estimatedEnd.setDisplayName("Estimated End");
		estimatedEnd.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
		estimatedEnd.setDataType(com.facilio.bmsconsole.modules.FieldType.DATE_TIME);
		estimatedEnd.setModuleName("ticket.schedule");
		estimatedEnd.setDefault(true);
		fifth.add(estimatedEnd);
	 
		FacilioField actualWorkEnd = new FacilioField();
		actualWorkEnd.setName("actualWorkEnd");
		actualWorkEnd.setDisplayName("Actual Work End");
		actualWorkEnd.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
		actualWorkEnd.setDataType(com.facilio.bmsconsole.modules.FieldType.DATE_TIME);
		actualWorkEnd.setModuleName("ticket.schedule");
		actualWorkEnd.setDefault(true);
		fifth.add(actualWorkEnd);
		panels.add(fifth);
		
//		Panel third =  new Panel(Panel.Type.FULL);
//		third.add(new Field("Attachments","inputAttachment","attachmentId",Field.FieldType.FILE).setIcon("fa fa-paperclip").setFileField(new FileField().setDisplayType(FileField.DISPLAY_TYPE_SECTION)));
//		panels.add(third);
		
		return panels;
		
	}
	
	
	public static List<Panel> getNewCampusLayout(List<FacilioField> fields)
	{
		List<Panel> panels = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		Panel second =  new Panel(Panel.Type.HALF);
		
		for(FacilioField field : fields) {
			if(field.getName().equals("baseSpaceId")) {
				continue;
			}
			if(field.getName().equals("name") || field.getName().equals("location") || field.getName().equals("description")) {
				first.add(field);
			}
			else {
				second.add(field);
			}
		}
		
		panels.add(first);
		panels.add(second);
		
		return panels;
	}
	
	public static List<Panel> getNewBuildingLayout(List<FacilioField> fields)
	{
		List<Panel> panels = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		Panel second =  new Panel(Panel.Type.HALF);
		Panel third =  new Panel(Panel.Type.HALF);
		 
		for(FacilioField field : fields) {
			if(field.getName().equals("baseSpaceId")) {
				continue;
			}
			if(field.getName().equals("name")) {
				first.add(field);
			}
			else if(field.getName().equals("campus") || field.getName().equals("floors") || field.getName().equals("location")){
				second.add(field);
			}
			else {
				third.add(field);
			}
		}
		panels.add(first);
		panels.add(second);
		panels.add(third);
		
		return panels;
	}
	
	public static List<Panel> getNewFloorLayout(List<FacilioField> fields)
	{
		List<Panel> panels = new ArrayList<Panel>();
		Panel first =  new Panel(Panel.Type.HALF);
		Panel second =  new Panel(Panel.Type.HALF);
		
		for(FacilioField field : fields) {
			if(field.getName().equals("baseSpaceId")) {
				continue;
			}
			if(field.getName().equals("name") || field.getName().equals("building") || field.getName().equals("area")) {
				first.add(field);
			}
			else {
				second.add(field);
			}
		}
		
		panels.add(first);
		panels.add(second);
		return panels;
	}
	
	public static List<Panel> getNewSpaceLayout(List<FacilioField> fields)
	{
		List<Panel> panels = new ArrayList<Panel>();
		Panel first =  new Panel(Panel.Type.HALF);
		Panel second =  new Panel(Panel.Type.HALF);
		
		for(FacilioField field : fields) {
			if(field.getName().equals("baseSpaceId")) {
				continue;
			}
			if(field.getName().equals("name") || field.getName().equals("displayName") || field.getName().equals("building") || field.getName().equals("floor") || field.getName().equals("area")) {
				first.add(field);
			}
			else {
				second.add(field);
			}
		}
		
		panels.add(first);
		panels.add(second);
		return panels;
	}
	
	public static List<Panel> getNewZoneLayout(List<FacilioField> fields)
	{
		List<Panel> panels = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		for(FacilioField field : fields) {
			first.add(field);
		}
		panels.add(first);
		return panels;
	}
	
	public static List<Panel> getNewSkillLayout(List<FacilioField> fields)
	{
		List<Panel> panels = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		for(FacilioField field : fields) {
			first.add(field);
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