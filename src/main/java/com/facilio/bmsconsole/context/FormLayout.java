package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.constants.FacilioConstants;

public class FormLayout {
	
	public static List<Panel> getNewTicketLayout()
	{
		List<Panel> fields =new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		
		first.add(new Field("Subject","inputSubject","ticket.subject",Field.FieldType.TEXTBOX).setRequired(true));
		first.add(new Field("Description","inputDescription","ticket.description",Field.FieldType.TEXTAREA));
		first.add(new Field("Assigned To","inputAssignedTo","ticket.assignedToId",Field.FieldType.SELECTBOX).setListName("userList"));
		first.add(new Field("Location","inputlocation","location",Field.FieldType.LOOKUP).setLookupModule("Locations"));
		first.add(new Field("Asset","inputAsset","ticket.assetId",Field.FieldType.SELECTBOX).setListName("assetList"));

		fields.add(first);
		
		Panel second =  new Panel(Panel.Type.HALF);
		second.add(new Field("Requester","inputRequester","ticket.requester",Field.FieldType.TEXTBOX));
		second.add(new Field("Opened date","inputOpenedDate","ticket.openedDate",Field.FieldType.DATETIME));
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
	String lookupModule;
	public String getLookupModule() {
		return lookupModule;
	}
	public Field setLookupModule(String lookupModule) {
		this.lookupModule = lookupModule;
		return this;
	}
	public String toString()
	{
		return name +"-"+label;
	}
	
	public String getLookupIcon()
	{
		if ("Locations".equalsIgnoreCase(this.lookupModule)) {
			return "fa fa-map-marker";
		}
		return "fa fa-search";
	}
	
	public String getHtml5Type()
	{
		switch(displayType)
		{
		case TEXTBOX:return "text";
		case EMAIL: return "email";
		case DATETIME :
		case DATE :
			return "datetime-local";
		}
		
		return null;
	}
}
