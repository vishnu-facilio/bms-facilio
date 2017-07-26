package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class FormLayout {
	
	public static List<Panel> getNewTicketLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		first.add(new Field("Subject","inputSubject","ticket.subject",Field.FieldType.TEXTBOX).setRequired(true));
		first.add(new Field("Description","inputDescription","ticket.description",Field.FieldType.TEXTAREA).setRequired(true).setPlaceholder("More about the problem.."));
		first.add(new Field("Assigned To","inputAssignedTo","ticket.assignedToId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("users", "Users").setPreloadedList("userList")));
		first.add(new Field("Asset","inputAsset","ticket.assetId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("assets", "Assets").setPreloadedList("assetList")));
		first.add(new Field("Space","inputAreaId","ticket.areaId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("area", "Space")));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Requester","inputRequester","ticket.requester",Field.FieldType.TEXTBOX).setRequired(true));
		second.add(new Field("Opened date","inputOpenedDate","ticket.openedDate",Field.FieldType.DATE));
		second.add(new Field("Priority","inputPriority","ticket.priority",Field.FieldType.SELECTBOX).setListName("statusList"));
		second.add(new Field("Status","inputStatus","ticket.statusCode",Field.FieldType.SELECTBOX).setListName("statusList"));
		second.add(new Field("Due Date","inputDueDate","ticket.dueDate",Field.FieldType.DATETIME));
		second.add(new Field("Category","inputCategory","ticket.category",Field.FieldType.SELECTBOX).setListName("statusList"));
		fields.add(second);
		
		Panel third =  new Panel(Panel.Type.FULL);
		third.add(new Field("Attachments","inputAttachment","attachmentId",Field.FieldType.FILE).setIcon("fa fa-paperclip").setFileField(new FileField().setDisplayType(FileField.DISPLAY_TYPE_SECTION)));
		fields.add(third);
		
		return fields;
		
	}
	
	public static List<Panel> getNewTaskLayout()
	{
		List<Panel> fields =new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		first.add(new Field("Title","inputTitle","task.title",Field.FieldType.TEXTBOX).setRequired(true));
		first.add(new Field("Description","inputDescription","task.description",Field.FieldType.TEXTAREA).setRequired(true).setPlaceholder("More about the problem.."));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Assigned To","inputAssignedTo","task.assignedToId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("users", "Users").setPreloadedList("userList")));
		second.add(new Field("Status","inputStatus","ticket.statusCode",Field.FieldType.SELECTBOX).setListName("statusList"));
		second.add(new Field("Due Date","inputDueDate","ticket.dueDate",Field.FieldType.DATETIME));
		fields.add(second);
		
		Panel third =  new Panel(Panel.Type.FULL).setTitle("Scheduling");
		fields.add(third);
		
		Panel fourth =  new Panel(Panel.Type.ONE_THIRD);
		fourth.add(new Field("Scheduled Start","inputScheduledStart","task.ScheduledStart",Field.FieldType.DATETIME));
		fourth.add(new Field("Actual Task Start","inputActual TaskStart","task.actualTaskStart",Field.FieldType.DATETIME));
		fields.add(fourth);
		
		Panel fifth =  new Panel(Panel.Type.ONE_THIRD);
		fifth.add(new Field("Estimated  End","inputEstimatedEnd","task.EstimatedEnd",Field.FieldType.DATETIME));
		fifth.add(new Field("Actual Task End","inputActualTaskEnd","task.ActualTaskEnd",Field.FieldType.DATETIME));
		fields.add(fifth);
		
		Panel sixth =  new Panel(Panel.Type.ONE_THIRD);
		sixth.add(new Field("Estimated Work Durations","inputEstimatedWorkDuration","task.EstimatedWorkDuration",Field.FieldType.DATETIME));
		fields.add(sixth);
		
		return fields;
		
	}
	
	public static List<Panel> getNewCampusLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		first.add(new Field("Name","inputName","campus.name",Field.FieldType.TEXTBOX));
		first.add(new Field("Location","inputLocationId","campus.locationId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("locations", "Locations").setPreloadedList("locations")));
		first.add(new Field("Description","inputDescription","campus.description",Field.FieldType.TEXTAREA));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Managed By","inputManagedBy","campus.managedBy",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("users", "Users").setPreloadedList("userList")));
		second.add(new Field("Max Occupancy","inputMaxOccupancy","campus.maxOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		second.add(new Field("Current Occupancy","inputCurrentOccupany","campus.currentOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		second.add(new Field("Area","inputarea","campus.area",Field.FieldType.TEXTBOX));
		fields.add(second);
		
		return fields;
	}
	
	public static List<Panel> getNewBuildingLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		first.add(new Field("Name","inputName","building.name",Field.FieldType.TEXTBOX));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Campus","inputCampusId","building.campusId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("campus", "Campus")));
		second.add(new Field("Floors","inputFloors","building.floors",Field.FieldType.TEXTBOX));
		second.add(new Field("Location","inputLocationId","building.locationId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("locations", "Locations").setPreloadedList("locations")));
		fields.add(second);
		
		Panel third =  new Panel(Panel.Type.HALF);
		third.add(new Field("Max Occupancy","inputMaxOccupancy","building.maxOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Current Occupancy","inputCurrentOccupany","building.currentOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Area","inputArea","building.area",Field.FieldType.TEXTBOX));
		fields.add(third);
		
		return fields;
	}
	
	public static List<Panel> getNewFloorLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		first.add(new Field("Name","inputName","floor.name",Field.FieldType.TEXTBOX));
		first.add(new Field("Building","inputBuildingId","floor.buildingId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("building", "Building")));
		first.add(new Field("Area","inputArea","floor.area",Field.FieldType.TEXTBOX));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Main Level","inputMainLevel","floor.mainLevel",Field.FieldType.DECISION_BOX));
		second.add(new Field("Current Occupancy","inputCurrentOccupany","floor.currentOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		second.add(new Field("Max Occupancy","inputMaxOccupancy","floor.maxOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		fields.add(second);
		
		return fields;
	}
	
	public static List<Panel> getNewSpaceLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		first.add(new Field("Display Name","inputDisplayName","space.displayName",Field.FieldType.TEXTBOX).setIsDisabled(true));
		first.add(new Field("Name","inputName","space.name",Field.FieldType.TEXTBOX));
		first.add(new Field("Building","inputBuildingId","space.buildingId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("building", "Building")));
		first.add(new Field("Floor","inputFloorId","space.floorId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("floor", "Floor")));
		first.add(new Field("Area","inputArea","space.area",Field.FieldType.TEXTBOX));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Occupiable","inputOccupiable","space.occupiable",Field.FieldType.DECISION_BOX));
		second.add(new Field("Availability","inputAvailability","space.availability",Field.FieldType.SELECTBOX).setListName("spaceAvailabilityList"));
		second.add(new Field("Max Occupancy","inputMaxOccupancy","space.maxOccupancy",Field.FieldType.TEXTBOX));
		second.add(new Field("Current Occupancy","inputCurrentOccupany","space.currentOccupancy",Field.FieldType.TEXTBOX));
		second.add(new Field("Category","inputSpaceCategoryId","space.spaceCategoryId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("space_category", "Space Category")));
		fields.add(second);
		
		return fields;
	}
	
	public static List<Panel> getNewZoneLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		first.add(new Field("Name","inputName","zone.name",Field.FieldType.TEXTBOX));
		first.add(new Field("Short Description","inputShortDescription","zone.shortDescription",Field.FieldType.TEXTAREA));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.FULL);
		second.add(new Field("Area","inputAreaId","zone.areaId",Field.FieldType.LOOKUP).setIcon("fa fa-building").setLookupModule(new LookupModule("area", "Space").setDisplayType(FileField.DISPLAY_TYPE_SECTION)));
		fields.add(second);
		
		return fields;
	}
	
	public static List<Panel> getNewSkillLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		first.add(new Field("Name","inputName","skill.name",Field.FieldType.TEXTBOX).setRequired(true));
		first.add(new Field("Description","inputDescription","skill.description",Field.FieldType.TEXTAREA));
		first.add(new Field("Active","inputActive","skill.isActive",Field.FieldType.DECISION_BOX));
		fields.add(first);
		
		return fields;
	}
}
class Panel extends ArrayList<Field>
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
class Field
{
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStyleclass() {
		return styleclass;
	}
	public void setStyleclass(String styleclass) {
		this.styleclass = styleclass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public Field setIcon(String icon) {
		this.icon = icon;
		return this;
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public Field setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		return this;
	}
	public String getList() {
		return list;
	}
	public void setList(String list) {
		this.list = list;
	}
	public boolean isRequired() {
		return required;
	}
	public Field setRequired(boolean required) {
		this.required = required;
		return this;
	}
	String label;
	String id;
	String styleclass="form-control";
	String name;
	String icon;
	String placeholder;
	boolean required;
	
	public enum FieldType {
	    TEXTBOX, SELECTBOX, RADIO, TEXTAREA,DATE,DATETIME, EMAIL, LOOKUP, FILE, DECISION_BOX
	}
	FieldType displayType;
	public Field(String label, String id,  String name, FieldType f) {
		super();
		this.label = label;
		this.id = id;
		this.name = name;
		this.displayType = f;
	}
	public FieldType getDisplayType() {
		return displayType;
	}
	public void setDisplayType(FieldType displayType) {
		this.displayType = displayType;
	}
	String list;
	public Field setListName(String list)
	{
		this.list=list;
		return this;
	}
	LookupModule lookupModule;
	public LookupModule getLookupModule() {
		return lookupModule;
	}
	public Field setLookupModule(LookupModule lookupModule) {
		this.lookupModule = lookupModule;
		return this;
	}
	FileField fileField;
	public FileField getFileField() {
		return fileField;
	}
	public Field setFileField(FileField fileField) {
		this.fileField = fileField;
		return this;
	}
	public String toString()
	{
		return name +"-"+label;
	}
	
	public String getHtml5Type()
	{
		switch(displayType)
		{
		case TEXTBOX:return "text";
		case EMAIL: return "email";
		case DECISION_BOX: return "checkbox";
		case DATETIME :
		case DATE :
			return "text";
		}
		
		return null;
	}
	
	boolean isDisabled = false;
	public Field setIsDisabled(boolean isDisabled)
	{
		this.isDisabled = isDisabled;
		return this;
	}
	
	public boolean getIsDisabled()
	{
		return this.isDisabled;
	}
}
class LookupModule
{
	public static final String DISPLAY_TYPE_SIMPLE = "simple";
	public static final String DISPLAY_TYPE_SECTION = "section";
	
	String name;
	String label;
	String criteria;
	String preloadedList;
	
	String displayType = DISPLAY_TYPE_SIMPLE; // 'simple' or 'section'	
	public LookupModule(String name, String label) {
		super();
		this.name = name;
		this.label = label;
	}
	
	public LookupModule setDisplayType(String displayType) {
		this.displayType = displayType;
		return this;
	}
	
	public String getDisplayType() {
		return this.displayType;
	}

	public LookupModule setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName() {
		return this.name;
	}
	
	public LookupModule setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public LookupModule setCriteria(String criteria) {
		this.criteria = criteria;
		return this;
	}
	
	public String getCriteria() {
		return this.criteria;
	}
	
	public LookupModule setPreloadedList(String preloadedList) {
		this.preloadedList = preloadedList;
		return this;
	}
	
	public String getPreloadedList() {
		return this.preloadedList;
	}
	
	public String getLookupIcon() {
		if ("locations".equalsIgnoreCase(this.name)) {
			return "fa fa-map-marker";
		}
		if ("assets".equalsIgnoreCase(this.name)) {
			return "fa fa-tablet";
		}
		else if ("users".equalsIgnoreCase(this.name)) {
			return "fa fa-user";
		}
		else if ("groups".equalsIgnoreCase(this.name)) {
			return "fa fa-users";
		}
		else if ("building".equalsIgnoreCase(this.name)) {
			return "fa fa-building";
		}
		return "fa fa-search";
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