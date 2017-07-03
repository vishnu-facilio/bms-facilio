package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class FormLayout {
	
	public static List<Panel> getNewTicketLayout()
	{
		List<Panel> fields =new ArrayList<Panel>();
		
		Panel first =  new Panel(Panel.Type.HALF);
		
		first.add(new Field("Requester","inputRequester","ticket.requester",Field.FieldType.TEXTBOX));
		first.add(new Field("Asset","inputAsset","ticket.failedAssetId",Field.FieldType.SELECTBOX).setListName("assetList"));
		first.add(new Field("Location","inputlocation","location",Field.FieldType.SELECTBOX).setListName("locations"));

		fields.add(first);
		Panel second =  new Panel(Panel.Type.HALF);

		second.add(new Field("Due Time","inputDueTime","ticket.dueTime",Field.FieldType.DATETIME));
		second.add(new Field("Status","inputStatus","ticket.statusCode",Field.FieldType.SELECTBOX).setListName("statusList"));
		second.add(new Field("Assigned To","inputAgent","ticket.assignedToId",Field.FieldType.SELECTBOX).setListName("userList"));

		fields.add(second);
		
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
	String label;
	String id;
	String styleclass="form-control";
	String name;
	public enum FieldType {
	    TEXTBOX, SELECTBOX, RADIO, TEXTAREA,DATE,DATETIME
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
	public String toString()
	{
		return name +"-"+label;
	}
	
	public String getHtml5Type()
	{
		switch(displayType)
		{
		case TEXTBOX:return "email";
		case DATETIME :
		case DATE :
			return "datetime-local";
		}
		
		return null;
	}
}
