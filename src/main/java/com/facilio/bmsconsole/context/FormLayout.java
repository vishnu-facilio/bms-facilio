package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class FormLayout {
	
	public static List<Panel> getNewTicketLayout()
	{
		List<Panel> fields =new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		
		first.add(new Field("Subject","inputSubject","ticket.subject",Field.FieldType.TEXTBOX).setRequired(true));
		first.add(new Field("Description","inputDescription","ticket.description",Field.FieldType.TEXTAREA).setRequired(true).setPlaceholder("More about the problem.."));
		first.add(new Field("Assigned To","inputAssignedTo","ticket.assignedToId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("users", "Users").setPreloadedList("userList")));
		first.add(new Field("Location","inputlocation","location",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("locations", "Locations").setPreloadedList("locations")));
		first.add(new Field("Asset","inputAsset","ticket.assetId",Field.FieldType.LOOKUP).setLookupModule(new LookupModule("assets", "Assets").setPreloadedList("assetList")));

		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Requester","inputRequester","ticket.requester",Field.FieldType.TEXTBOX));
		second.add(new Field("Opened date","inputOpenedDate","ticket.openedDate",Field.FieldType.DATE));
		second.add(new Field("Priority","inputPriority","ticket.priority",Field.FieldType.SELECTBOX).setListName("statusList"));
		second.add(new Field("Status","inputStatus","ticket.statusCode",Field.FieldType.SELECTBOX).setListName("statusList"));
		second.add(new Field("Due Date","inputDueDate","ticket.dueDate",Field.FieldType.DATETIME));
		second.add(new Field("Category","inputCategory","ticket.category",Field.FieldType.SELECTBOX).setListName("statusList"));

		fields.add(second);
		
//		Panel third =  new Panel(Panel.Type.FULL);
//
//		third.add(new Field("Subject","inputSubject","ticket.subject",Field.FieldType.TEXTBOX));
//		third.add(new Field("Description","inputDescription","ticket.description",Field.FieldType.TEXTAREA));
//
//		fields.add(third);
		
		return fields;
		
	}
	
	public static List<Panel> getNewCampusLayout()
	{
		List<Panel> fields =new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		first.add(new Field("Name","inputName","campus.name",Field.FieldType.TEXTBOX));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Managed by","inputManagedBy","campus.managedBy",Field.FieldType.SELECTBOX).setListName("userList"));
		second.add(new Field("Location","inputLocation","campus.location",Field.FieldType.SELECTBOX).setListName("locations"));
		second.add(new Field("Gross Area","inputGrossArea","campus.grossArea",Field.FieldType.TEXTBOX));
		second.add(new Field("Usable Area","inputUsableArea","campus.usableArea",Field.FieldType.TEXTBOX));
		second.add(new Field("Assignable Area","inputAssignableArea","campus.assignable",Field.FieldType.TEXTBOX).setIsDisabled(true));
		second.add(new Field("Area Unit","inputAreaUnit","campus.areaUnit",Field.FieldType.TEXTBOX));
		fields.add(second);
		
		Panel third =  new Panel(Panel.Type.HALF);
		third.add(new Field("Current Occupancy","inputCurrentOccupany","campus.currentOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Max Occupancy","inputMaxOccupancy","campus.maxOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Percent Occupied","inputPercentOccupied","campus.percentOccupied",Field.FieldType.TEXTBOX).setIsDisabled(true));
		fields.add(third);
		
		Panel fourth =  new Panel(Panel.Type.FULL);
		fourth.add(new Field("Notes","inputNotes","campus.notes",Field.FieldType.TEXTAREA));
		fields.add(fourth);
		
		return fields;
	}
	
	public static List<Panel> getNewBuildingLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		first.add(new Field("Name","inputName","building.name",Field.FieldType.TEXTBOX));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Campus","inputCampus","building.campus",Field.FieldType.SELECTBOX).setListName("locations"));
		second.add(new Field("Floors","inputFloors","building.floors",Field.FieldType.TEXTBOX));
		second.add(new Field("Location","inputLocation","building.location",Field.FieldType.SELECTBOX).setListName("locations"));
		second.add(new Field("Assignable Area","inputAssignableArea","building.assignable",Field.FieldType.TEXTBOX).setIsDisabled(true));
		second.add(new Field("Usable Area","inputUsableArea","building.usableArea",Field.FieldType.TEXTBOX));
		second.add(new Field("Gross Area","inputGrossArea","building.grossArea",Field.FieldType.TEXTBOX));
		second.add(new Field("Area Unit","inputAreaUnit","building.areaUnit",Field.FieldType.TEXTBOX));
		fields.add(second);
		
		Panel third =  new Panel(Panel.Type.HALF);
		third.add(new Field("Current Occupancy","inputCurrentOccupany","building.currentOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Max Occupancy","inputMaxOccupancy","building.maxOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Percent Occupied","inputPercentOccupied","building.percentOccupied",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Utilization Min","inputUtilizationMin","building.utilizationMin",Field.FieldType.TEXTBOX));
		third.add(new Field("Utilization Max","inputUtilizationMax","building.utilizationMax",Field.FieldType.TEXTBOX));
		fields.add(third);
		
		return fields;
	}
	
	public static List<Panel> getNewFloorLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		first.add(new Field("Name","inputName","floor.name",Field.FieldType.TEXTBOX));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Building","inputBuilding","floor.building",Field.FieldType.SELECTBOX).setListName("locations"));
		second.add(new Field("MainLevel","inputMainLevel","floor.mainLevel",Field.FieldType.TEXTBOX));
		second.add(new Field("Assignable Area","inputAssignableArea","floor.assignable",Field.FieldType.TEXTBOX).setIsDisabled(true));
		second.add(new Field("Usable Area","inputUsableArea","floor.usableArea",Field.FieldType.TEXTBOX));
		second.add(new Field("Gross Area","inputGrossArea","floor.grossArea",Field.FieldType.TEXTBOX));
		second.add(new Field("Area Unit","inputAreaUnit","floor.areaUnit",Field.FieldType.TEXTBOX));
		fields.add(second);
		
		Panel third =  new Panel(Panel.Type.HALF);
		third.add(new Field("Current Occupancy","inputCurrentOccupany","floor.currentOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Max Occupancy","inputMaxOccupancy","floor.maxOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Percent Occupied","inputPercentOccupied","floor.percentOccupied",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Utilization Min","inputUtilizationMin","floor.utilizationMin",Field.FieldType.TEXTBOX));
		third.add(new Field("Utilization Max","inputUtilizationMax","floor.utilizationMax",Field.FieldType.TEXTBOX));
		fields.add(third);
		
		return fields;
	}
	
	public static List<Panel> getNewSpaceLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		first.add(new Field("Display Name","inputDisplayName","space.displayName",Field.FieldType.TEXTBOX));
		first.add(new Field("Name","inputName","space.name",Field.FieldType.TEXTBOX));
		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Building","inputBuilding","space.building",Field.FieldType.SELECTBOX).setListName("locations"));
		second.add(new Field("Floor","inputFloor","space.floor",Field.FieldType.SELECTBOX).setListName("locations"));
		second.add(new Field("Area","inputArea","space.area",Field.FieldType.TEXTBOX));
		second.add(new Field("Area Unit","inputAreaUnit","space.areaUnit",Field.FieldType.TEXTBOX));
		fields.add(second);
		
		Panel third =  new Panel(Panel.Type.HALF);
		third.add(new Field("Status","inputStatus","space.status",Field.FieldType.TEXTBOX));
		third.add(new Field("Availability","inputAvailability","space.availability",Field.FieldType.TEXTBOX));
		third.add(new Field("Current Occupancy","inputCurrentOccupany","space.currentOccupancy",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Max Occupancy","inputMaxOccupancy","space.maxOccupancy",Field.FieldType.TEXTBOX));
		third.add(new Field("Percent Occupied","inputPercentOccupied","space.percentOccupied",Field.FieldType.TEXTBOX).setIsDisabled(true));
		third.add(new Field("Occupiable","inputOccupiable","space.occupiable",Field.FieldType.TEXTBOX));
		fields.add(third);
		
		return fields;
	}
	
	public static List<Panel> getNewZoneLayout()
	{
		List<Panel> fields = new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.FULL);
		first.add(new Field("Name","inputName","zone.name",Field.FieldType.TEXTBOX));
		first.add(new Field("Short Description","inputShortDescription","zone.shortDescription",Field.FieldType.TEXTAREA));
		fields.add(first);
		
		return fields;
	}
}
class Panel extends ArrayList<Field>
{
	public enum Type {
		HALF,FULL
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
	String placeholder;
	boolean required;
	public enum FieldType {
	    TEXTBOX, SELECTBOX, RADIO, TEXTAREA,DATE,DATETIME, EMAIL, LOOKUP
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
	String name;
	String label;
	String criteria;
	String preloadedList;
	public LookupModule(String name, String label) {
		super();
		this.name = name;
		this.label = label;
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
		return "fa fa-search";
	}
}