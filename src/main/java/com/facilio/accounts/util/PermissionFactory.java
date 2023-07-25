package com.facilio.accounts.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionFactory {
	
	public static enum Permission_Child_Type {
		RADIO,
		CHECKBOX,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
	}
	
	
	
	public static enum Dashboard_Permission implements PermissionInterface {
		
		CREATE(1,"Create"),
		VIEW(2,"View"),
		EDIT(4,"Edit"),
		DELETE(8,"Delete"),
		SHARE(16,"Share"),
		
		;

		long permission;
		String permissionName;
		String moduleName="dashboard";
		String moduleNameDisplayName="Dashboard";
		PermissionInterface parent;
		List<PermissionInterface> childs;
		Permission_Child_Type childType;

		Dashboard_Permission(long permission,String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}
		
		Dashboard_Permission(long permission,String permissionName,Dashboard_Permission parent,Permission_Child_Type childType) {
			this.permission = permission;
			this.permissionName = permissionName;
			this.parent = parent;
			this.childType = childType;
		}

		public long getPermission() {
			return this.permission;
		}
		public String getPermissionName() {
			return this.permissionName;
		}
		public String getModuleName() {
			return this.moduleName;
		}
		public static String getModuleDisplayName() {
			return "Dashboard";
		}
		public PermissionInterface getParent() {
			return parent;
		}
		public List<PermissionInterface> getChilds() {
			return childs;
		}
		public Permission_Child_Type getChildType() {
			return this.childType;
		}
		public void addChild(PermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}
		
		private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<PermissionInterface> initTypeMap() {
			List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

			for (PermissionInterface type : values()) {
				
				if(type.getParent() != null) {
					type.getParent().addChild(type);
				}
				else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}
		
		public static List<PermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}
	

	
public static enum Portfolio_Permission implements PermissionInterface {
		
		CREATE (1,"Create"),
		IMPORT(2,"Import"),
		READ(4,"Read"),
		UPDATE(8,"Update"),
		ADD_READING(16,"Add Reading"),
		DELETE(32,"Delete")
			
		;

		long permission;
		String permissionName;
		String moduleName="portfolio";
		String moduleNameDisplayName="Portfolio";
		PermissionInterface parent;
		List<PermissionInterface> childs;
		Permission_Child_Type childType;

		Portfolio_Permission(long permission,String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}
		
		Portfolio_Permission(long permission,String permissionName,Portfolio_Permission parent,Permission_Child_Type childType) {
			this.permission = permission;
			this.permissionName = permissionName;
			this.parent = parent;
			this.childType = childType;
		}

		public long getPermission() {
			return this.permission;
		}
		public String getPermissionName() {
			return this.permissionName;
		}
		public String getModuleName() {
			return this.moduleName;
		}
		public static String getModuleDisplayName() {
			return "Portfolio";
		}
		public PermissionInterface getParent() {
			return parent;
		}
		public List<PermissionInterface> getChilds() {
			return childs;
		}
		public Permission_Child_Type getChildType() {
			return this.childType;
		}
		public void addChild(PermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}
		
		private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<PermissionInterface> initTypeMap() {
			List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

			for (PermissionInterface type : values()) {
				
				if(type.getParent() != null) {
					type.getParent().addChild(type);
				}
				else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}
		
		public static List<PermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}
	

public static enum Reservation_Permission implements PermissionInterface {
	
	CREATE (1,"Create"),
	READ(2,"Read"),
	UPDATE(4,"Update"),
	DELETE(8,"Delete"),
		
	;

	long permission;
	String permissionName;
	String moduleName="reservation";
	String moduleNameDisplayName="Reservations";
	PermissionInterface parent;
	List<PermissionInterface> childs;
	Permission_Child_Type childType;

	Reservation_Permission(long permission,String permissionName) {
		this.permission = permission;
		this.permissionName = permissionName;
	}
	
	Reservation_Permission(long permission,String permissionName,Reservation_Permission parent,Permission_Child_Type childType) {
		this.permission = permission;
		this.permissionName = permissionName;
		this.parent = parent;
		this.childType = childType;
	}

	public long getPermission() {
		return this.permission;
	}
	public String getPermissionName() {
		return this.permissionName;
	}
	public String getModuleName() {
		return this.moduleName;
	}
	public static String getModuleDisplayName() {
		return "Reservations";
	}
	public PermissionInterface getParent() {
		return parent;
	}
	public List<PermissionInterface> getChilds() {
		return childs;
	}
	public Permission_Child_Type getChildType() {
		return this.childType;
	}
	public void addChild(PermissionInterface child) {
		childs = childs == null ? new ArrayList<>() : childs;
		childs.add(child);
	}
	
	private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

	private static List<PermissionInterface> initTypeMap() {
		List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

		for (PermissionInterface type : values()) {
			
			if(type.getParent() != null) {
				type.getParent().addChild(type);
			}
			else {
				typeMap.add(type);
			}
		}
		return typeMap;
	}
	
	public static List<PermissionInterface> getAllPermissions() {
		return permissionList;
	}
}


public static enum Asset_Permission implements PermissionInterface {
	
	CREATE (1,"Create"),
	IMPORT(2,"Import"),
	READ(4,"Read"),
	UPDATE(8,"Update"),
	DELETE(16,"Delete")
	;

	long permission;
	String permissionName;
	String moduleName="asset";
	String moduleNameDisplayName="Assets";
	PermissionInterface parent;
	List<PermissionInterface> childs;
	Permission_Child_Type childType;

	Asset_Permission(long permission,String permissionName) {
		this.permission = permission;
		this.permissionName = permissionName;
	}
	
	Asset_Permission(long permission,String permissionName,Asset_Permission parent,Permission_Child_Type childType) {
		this.permission = permission;
		this.permissionName = permissionName;
		this.parent = parent;
		this.childType = childType;
	}

	public long getPermission() {
		return this.permission;
	}
	public String getPermissionName() {
		return this.permissionName;
	}
	public String getModuleName() {
		return this.moduleName;
	}
	public static String getModuleDisplayName() {
		return "Assets";
	}
	public PermissionInterface getParent() {
		return parent;
	}
	public List<PermissionInterface> getChilds() {
		return childs;
	}
	public Permission_Child_Type getChildType() {
		return this.childType;
	}
	public void addChild(PermissionInterface child) {
		childs = childs == null ? new ArrayList<>() : childs;
		childs.add(child);
	}
	
	private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

	private static List<PermissionInterface> initTypeMap() {
		List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

		for (PermissionInterface type : values()) {
			
			if(type.getParent() != null) {
				type.getParent().addChild(type);
			}
			else {
				typeMap.add(type);
			}
		}
		return typeMap;
	}
	
	public static List<PermissionInterface> getAllPermissions() {
		return permissionList;
	}
}


public static enum Inventory_Permission implements PermissionInterface {
	
	CREATE (1,"Create"),
	READ(2,"Read"),
	UPDATE(4,"Update"),
	APPROVAL(8,"Approval"),
	DELETE(16,"Delete"),
	;

	long permission;
	String permissionName;
	String moduleName="inventory";
	String moduleNameDisplayName="Inventory";
	PermissionInterface parent;
	List<PermissionInterface> childs;
	Permission_Child_Type childType;

	Inventory_Permission(long permission,String permissionName) {
		this.permission = permission;
		this.permissionName = permissionName;
	}
	
	Inventory_Permission(long permission,String permissionName,Inventory_Permission parent,Permission_Child_Type childType) {
		this.permission = permission;
		this.permissionName = permissionName;
		this.parent = parent;
		this.childType = childType;
	}

	public long getPermission() {
		return this.permission;
	}
	public String getPermissionName() {
		return this.permissionName;
	}
	public String getModuleName() {
		return this.moduleName;
	}
	public static String getModuleDisplayName() {
		return "Inventory";
	}
	public PermissionInterface getParent() {
		return parent;
	}
	public List<PermissionInterface> getChilds() {
		return childs;
	}
	public Permission_Child_Type getChildType() {
		return this.childType;
	}
	public void addChild(PermissionInterface child) {
		childs = childs == null ? new ArrayList<>() : childs;
		childs.add(child);
	}
	
	private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

	private static List<PermissionInterface> initTypeMap() {
		List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

		for (PermissionInterface type : values()) {
			
			if(type.getParent() != null) {
				type.getParent().addChild(type);
			}
			else {
				typeMap.add(type);
			}
		}
		return typeMap;
	}
	
	public static List<PermissionInterface> getAllPermissions() {
		return permissionList;
	}
}

public static enum Purchase_Order_Permission implements PermissionInterface {
	
	CREATE (1,"Create"),
	READ(2,"Read"),
	UPDATE(4,"Update"),
	DELETE(8,"Delete"),
	APPROVAL_COMPLETE_PO(16,"Appproval/Complete PO")
	;

	long permission;
	String permissionName;
	String moduleName="purchaseorder";
	String moduleNameDisplayName="Purchase Orders";
	PermissionInterface parent;
	List<PermissionInterface> childs;
	Permission_Child_Type childType;

	Purchase_Order_Permission(long permission,String permissionName) {
		this.permission = permission;
		this.permissionName = permissionName;
	}
	
	Purchase_Order_Permission(long permission,String permissionName,Purchase_Order_Permission parent,Permission_Child_Type childType) {
		this.permission = permission;
		this.permissionName = permissionName;
		this.parent = parent;
		this.childType = childType;
	}

	public long getPermission() {
		return this.permission;
	}
	public String getPermissionName() {
		return this.permissionName;
	}
	public String getModuleName() {
		return this.moduleName;
	}
	public static String getModuleDisplayName() {
		return "Purchase Orders";
	}
	public PermissionInterface getParent() {
		return parent;
	}
	public List<PermissionInterface> getChilds() {
		return childs;
	}
	public Permission_Child_Type getChildType() {
		return this.childType;
	}
	public void addChild(PermissionInterface child) {
		childs = childs == null ? new ArrayList<>() : childs;
		childs.add(child);
	}
	
	private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

	private static List<PermissionInterface> initTypeMap() {
		List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

		for (PermissionInterface type : values()) {
			
			if(type.getParent() != null) {
				type.getParent().addChild(type);
			}
			else {
				typeMap.add(type);
			}
		}
		return typeMap;
	}
	
	public static List<PermissionInterface> getAllPermissions() {
		return permissionList;
	}
}


public static enum Contract_Permission implements PermissionInterface {
	
	CREATE (1,"Create"),
	READ(2,"Read"),
	UPDATE(4,"Update"),
	DELETE(8,"Delete"),
	APPROVAL(16,"Approval"),
	;

	long permission;
	String permissionName;
	String moduleName="contracts";
	String moduleNameDisplayName="Contracts";
	PermissionInterface parent;
	List<PermissionInterface> childs;
	Permission_Child_Type childType;

	Contract_Permission(long permission,String permissionName) {
		this.permission = permission;
		this.permissionName = permissionName;
	}
	
	Contract_Permission(long permission,String permissionName,Contract_Permission parent,Permission_Child_Type childType) {
		this.permission = permission;
		this.permissionName = permissionName;
		this.parent = parent;
		this.childType = childType;
	}

	public long getPermission() {
		return this.permission;
	}
	public String getPermissionName() {
		return this.permissionName;
	}
	public String getModuleName() {
		return this.moduleName;
	}
	public static String getModuleDisplayName() {
		return "Contracts";
	}
	public PermissionInterface getParent() {
		return parent;
	}
	public List<PermissionInterface> getChilds() {
		return childs;
	}
	public Permission_Child_Type getChildType() {
		return this.childType;
	}
	public void addChild(PermissionInterface child) {
		childs = childs == null ? new ArrayList<>() : childs;
		childs.add(child);
	}
	
	private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

	private static List<PermissionInterface> initTypeMap() {
		List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

		for (PermissionInterface type : values()) {
			
			if(type.getParent() != null) {
				type.getParent().addChild(type);
			}
			else {
				typeMap.add(type);
			}
		}
		return typeMap;
	}
	
	public static List<PermissionInterface> getAllPermissions() {
		return permissionList;
	}
}


public static enum Asset_Reports_Permission implements PermissionInterface {
	
	VIEW (1,"View"),
	CREATE_EDIT(2,"Create/Edit"),
	EXPORT(4,"Export"),
	;

	long permission;
	String permissionName;
	String moduleName="assetreports";
	String moduleNameDisplayName="Reports";
	PermissionInterface parent;
	List<PermissionInterface> childs;
	Permission_Child_Type childType;

	Asset_Reports_Permission(long permission,String permissionName) {
		this.permission = permission;
		this.permissionName = permissionName;
	}
	
	Asset_Reports_Permission(long permission,String permissionName,Asset_Reports_Permission parent,Permission_Child_Type childType) {
		this.permission = permission;
		this.permissionName = permissionName;
		this.parent = parent;
		this.childType = childType;
	}

	public long getPermission() {
		return this.permission;
	}
	public String getPermissionName() {
		return this.permissionName;
	}
	public String getModuleName() {
		return this.moduleName;
	}
	public static String getModuleDisplayName() {
		return "Reports";
	}
	public PermissionInterface getParent() {
		return parent;
	}
	public List<PermissionInterface> getChilds() {
		return childs;
	}
	public Permission_Child_Type getChildType() {
		return this.childType;
	}
	public void addChild(PermissionInterface child) {
		childs = childs == null ? new ArrayList<>() : childs;
		childs.add(child);
	}
	
	private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

	private static List<PermissionInterface> initTypeMap() {
		List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

		for (PermissionInterface type : values()) {
			
			if(type.getParent() != null) {
				type.getParent().addChild(type);
			}
			else {
				typeMap.add(type);
			}
		}
		return typeMap;
	}
	
	public static List<PermissionInterface> getAllPermissions() {
		return permissionList;
	}
}


public static enum WorkOrder_Permission implements PermissionInterface {
	
	CREATE (1,"Create"),
	READ(-1,"Read"),
	
	READ_TEAM(2,"Team", WorkOrder_Permission.READ, Permission_Child_Type.RADIO),
	READ_OWN(4,"Own",WorkOrder_Permission.READ, Permission_Child_Type.RADIO),
	READ_IN_ACCESSIBLE_SPACES(8,"All",WorkOrder_Permission.READ, Permission_Child_Type.RADIO),
	
	UPDATE(-1,"Update"),
	
	UPDATE_TEAM(16,"Team", WorkOrder_Permission.UPDATE, Permission_Child_Type.RADIO),
	UPDATE_OWN(32,"Own",WorkOrder_Permission.UPDATE,Permission_Child_Type.RADIO),
	UPDATE_IN_ACCESSIBLE_SPACES(64,"All",WorkOrder_Permission.UPDATE,Permission_Child_Type.RADIO),
	
	UPDATE_CHANGE_OWNERSHIP(128,"Change Ownership",WorkOrder_Permission.UPDATE,Permission_Child_Type.CHECKBOX),
	UPDATE_CLOSE_WORKORDER(256,"All",WorkOrder_Permission.UPDATE,Permission_Child_Type.CHECKBOX),
	UPDATE_TASK(512,"Add/Update/Delete Task",WorkOrder_Permission.UPDATE,Permission_Child_Type.CHECKBOX),

	DELETE(-1,"Delete"),

	DELETE_TEAM(16,"Team", WorkOrder_Permission.DELETE, Permission_Child_Type.RADIO),
	DELETE_OWN(32,"Own",WorkOrder_Permission.DELETE,Permission_Child_Type.RADIO),
	DELETE_IN_ACCESSIBLE_SPACES(64,"All",WorkOrder_Permission.DELETE,Permission_Child_Type.RADIO),
	;

	long permission;
	String permissionName;
	String moduleName="workorder";
	String moduleNameDisplayName="WorkOrders";
	PermissionInterface parent;
	List<PermissionInterface> childs;
	Permission_Child_Type childType;

	WorkOrder_Permission(long permission,String permissionName) {
		this.permission = permission;
		this.permissionName = permissionName;
	}
	
	WorkOrder_Permission(long permission,String permissionName,WorkOrder_Permission parent,Permission_Child_Type childType) {
		this.permission = permission;
		this.permissionName = permissionName;
		this.parent = parent;
		this.childType = childType;
	}

	public long getPermission() {
		return this.permission;
	}
	public String getPermissionName() {
		return this.permissionName;
	}
	public String getModuleName() {
		return this.moduleName;
	}
	public static String getModuleDisplayName() {
		return "WorkOrders";
	}
	public PermissionInterface getParent() {
		return parent;
	}
	public List<PermissionInterface> getChilds() {
		return childs;
	}
	public Permission_Child_Type getChildType() {
		return this.childType;
	}
	public void addChild(PermissionInterface child) {
		childs = childs == null ? new ArrayList<>() : childs;
		childs.add(child);
	}
	
	private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

	private static List<PermissionInterface> initTypeMap() {
		List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

		for (PermissionInterface type : values()) {
			
			if(type.getParent() != null) {
				type.getParent().addChild(type);
			}
			else {
				typeMap.add(type);
			}
		}
		return typeMap;
	}
	
	public static List<PermissionInterface> getAllPermissions() {
		return permissionList;
	}
}


public static enum Approval_Permission implements PermissionInterface {
	
	ITEM_REQUEST(-1,"Item Request"),
	
	ITEM_REQ_CREATE(1,"Create", Approval_Permission.ITEM_REQUEST, Permission_Child_Type.CHECKBOX),
	ITEM_REQ_EDIT(2,"Edit",Approval_Permission.ITEM_REQUEST, Permission_Child_Type.CHECKBOX),
	ITEM_REQ_APPROVAL(4,"Approval",Approval_Permission.ITEM_REQUEST, Permission_Child_Type.CHECKBOX),
	ITEM_REQ_DELETE(8,"Delete",Approval_Permission.ITEM_REQUEST, Permission_Child_Type.CHECKBOX),
	
	WORK_REQUEST(-1,"Work Request"),
	
	WORK_REQ_CREATE(1,"Create", Approval_Permission.WORK_REQUEST, Permission_Child_Type.CHECKBOX),
	WORK_REQ_EDIT(2,"Edit",Approval_Permission.WORK_REQUEST, Permission_Child_Type.CHECKBOX),
	WORK_REQ_APPROVAL(4,"Approval",Approval_Permission.WORK_REQUEST, Permission_Child_Type.CHECKBOX),
	WORK_REQ_DELETE(8,"Delete",Approval_Permission.WORK_REQUEST, Permission_Child_Type.CHECKBOX),
	
	;

	long permission;
	String permissionName;
	String moduleName="approvals";
	String moduleNameDisplayName="Approvals";
	PermissionInterface parent;
	List<PermissionInterface> childs;
	Permission_Child_Type childType;

	Approval_Permission(long permission,String permissionName) {
		this.permission = permission;
		this.permissionName = permissionName;
	}
	
	Approval_Permission(long permission,String permissionName,Approval_Permission parent,Permission_Child_Type childType) {
		this.permission = permission;
		this.permissionName = permissionName;
		this.parent = parent;
		this.childType = childType;
	}

	public long getPermission() {
		return this.permission;
	}
	public String getPermissionName() {
		return this.permissionName;
	}
	public String getModuleName() {
		return this.moduleName;
	}
	public static String getModuleDisplayName() {
		return "Approvals";
	}
	public PermissionInterface getParent() {
		return parent;
	}
	public List<PermissionInterface> getChilds() {
		return childs;
	}
	public Permission_Child_Type getChildType() {
		return this.childType;
	}
	public void addChild(PermissionInterface child) {
		childs = childs == null ? new ArrayList<>() : childs;
		childs.add(child);
	}
	
	private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

	private static List<PermissionInterface> initTypeMap() {
		List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

		for (PermissionInterface type : values()) {
			
			if(type.getParent() != null) {
				type.getParent().addChild(type);
			}
			else {
				typeMap.add(type);
			}
		}
		return typeMap;
	}
	
	public static List<PermissionInterface> getAllPermissions() {
		return permissionList;
	}
}
	public static enum
	IndoorFloorPlan_Permission implements PermissionInterface {

		CREATE (1,"Create"),
		EDIT(2,"Edit"),
		VIEW(4,"View"),
		ASSIGN(8,"Assign"),
		BOOKING(16,"Booking"),
		VIEW_ASSIGNMENT(-1,"View Assignment"),

		VIEW_ASSIGNMENT_ALL(32,"All", IndoorFloorPlan_Permission.VIEW_ASSIGNMENT, Permission_Child_Type.RADIO),
		VIEW_ASSIGNMENT_DEPARTMENT(64,"Department",IndoorFloorPlan_Permission.VIEW_ASSIGNMENT, Permission_Child_Type.RADIO),
		VIEW_ASSIGNMENT_OWN(128,"Own",IndoorFloorPlan_Permission.VIEW_ASSIGNMENT, Permission_Child_Type.RADIO),

		VIEW_BOOKING(-1,"View Booking"),

		VIEW_BOOKING_ALL(256,"All", IndoorFloorPlan_Permission.VIEW_BOOKING, Permission_Child_Type.RADIO),
		VIEW_BOOKING_DEPARTMENT(512,"Department",IndoorFloorPlan_Permission.VIEW_BOOKING,Permission_Child_Type.RADIO),
		VIEW_BOOKING_OWN(1024,"Own",IndoorFloorPlan_Permission.VIEW_BOOKING,Permission_Child_Type.RADIO),

		;

		long permission;
		String permissionName;
		String moduleName="indoorfloorplan";
		String moduleNameDisplayName="IndoorFloorPlan";
		PermissionInterface parent;
		List<PermissionInterface> childs;
		Permission_Child_Type childType;

		IndoorFloorPlan_Permission(long permission,String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		IndoorFloorPlan_Permission(long permission,String permissionName,IndoorFloorPlan_Permission parent,Permission_Child_Type childType) {
			this.permission = permission;
			this.permissionName = permissionName;
			this.parent = parent;
			this.childType = childType;
		}

		public long getPermission() {
			return this.permission;
		}
		public String getPermissionName() {
			return this.permissionName;
		}
		public String getModuleName() {
			return this.moduleName;
		}
		public static String getModuleDisplayName() {
			return "IndoorFloorPlan";
		}
		public PermissionInterface getParent() {
			return parent;
		}
		public List<PermissionInterface> getChilds() {
			return childs;
		}
		public Permission_Child_Type getChildType() {
			return this.childType;
		}
		public void addChild(PermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<PermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<PermissionInterface> initTypeMap() {
			List<PermissionInterface> typeMap = new ArrayList<PermissionInterface>();

			for (PermissionInterface type : values()) {

				if(type.getParent() != null) {
					type.getParent().addChild(type);
				}
				else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}
		public static List<PermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}


	
}
