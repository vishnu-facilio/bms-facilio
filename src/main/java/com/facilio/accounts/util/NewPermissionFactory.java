package com.facilio.accounts.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewPermissionFactory {
	public static enum Permission_Child_Type {
		RADIO, CHECKBOX,;

		public int getValue() {
			return ordinal() + 1;
		}
	}

	public static enum Module_Permission implements NewPermissionInterface {
		CREATE(1, "Create"), IMPORT(2, "Import"), READ(4, "Read"), UPDATE(8, "Update"), DELETE(16, "Delete"), EXPORT(32,
				"Export");

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Module_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Module_Permission(long permission, String permissionName, Dashboard_Permission parent,
				Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}

	public static enum Approval_Permission implements NewPermissionInterface {

		CAN_APPROVE(1, "Can Approve");

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Approval_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Approval_Permission(long permission, String permissionName, Dashboard_Permission parent,
				Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}

	public static enum Calendar_Permission implements NewPermissionInterface {

		CALENDAR(1, "Calendar"), PLANNER(2, "Planner");

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Calendar_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Calendar_Permission(long permission, String permissionName, Calendar_Permission parent,
				Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}

	public static enum Reports_Permission implements NewPermissionInterface {

		VIEW(1, "View"), CREATE_EDIT(2, "Create/Edit"), EXPORT(4, "Export"),;

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Reports_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Reports_Permission(long permission, String permissionName, Reports_Permission parent,
				Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}

	public static enum Analytics_Permission implements NewPermissionInterface {

		SAVE_AS_REPORT(1, "Save As Report"), VIEW(2, "View")
		;

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Analytics_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Analytics_Permission(long permission, String permissionName, Analytics_Permission parent,
				Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}
	
	public static enum KPI_Permission implements NewPermissionInterface {

		CREATE(1, "Create"), READ(2, "Read"), UPDATE(4, "Update"), DELETE(8, "Delete")
		;

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		KPI_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		KPI_Permission(long permission, String permissionName, KPI_Permission parent,
				Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}
	
	public static enum Dashboard_Permission implements NewPermissionInterface {

		CREATE(1, "Create"), VIEW(2, "View"), EDIT(4, "Edit"), DELETE(8, "Delete"), SHARE(16, "Share"),

		;

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Dashboard_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Dashboard_Permission(long permission, String permissionName, Dashboard_Permission parent,
				Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}
	
	public static enum Custom_Permission implements NewPermissionInterface {

		CREATE(1, "Create"), READ(2, "Read"), UPDATE(4, "Update"), DELETE(8, "Delete")
		;

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Custom_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Custom_Permission(long permission, String permissionName, Dashboard_Permission parent,
				Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}
	public static enum Workorder_Permission implements NewPermissionInterface {

		CREATE (1,"Create"),
		READ_GROUP(-1,"Read"),
		
		READ_TEAM(2, "Team", Workorder_Permission.READ_GROUP, Permission_Child_Type.RADIO),
		READ_OWN(4, "Own", Workorder_Permission.READ_GROUP, Permission_Child_Type.RADIO),
		READ(8, "All", Workorder_Permission.READ_GROUP, Permission_Child_Type.RADIO),
		
		UPDATE_GROUP(-1,"Update"),
		
		UPDATE_TEAM(16, "Team", Workorder_Permission.UPDATE_GROUP, Permission_Child_Type.RADIO),
		UPDATE_OWN(32, "Own", Workorder_Permission.UPDATE_GROUP, Permission_Child_Type.RADIO),
		UPDATE(64, "All", Workorder_Permission.UPDATE_GROUP, Permission_Child_Type.RADIO),
		
		UPDATE_CHANGE_OWNERSHIP(128,"Change Ownership",Workorder_Permission.UPDATE_GROUP,Permission_Child_Type.CHECKBOX),
		UPDATE_CLOSE_WORKORDER(256,"All",Workorder_Permission.UPDATE_GROUP,Permission_Child_Type.CHECKBOX),
		UPDATE_TASK(512,"Add/Update/Delete Task",Workorder_Permission.UPDATE_GROUP,Permission_Child_Type.CHECKBOX),

		DELETE_GROUP(-1,"Delete"),

		DELETE_TEAM(1024,"Team", Workorder_Permission.DELETE_GROUP, Permission_Child_Type.RADIO),
		DELETE_OWN(2048,"Own",Workorder_Permission.DELETE_GROUP,Permission_Child_Type.RADIO),
		DELETE(4096,"All",Workorder_Permission.DELETE_GROUP,Permission_Child_Type.RADIO),
		IMPORT(8192,"Import"),
		EXPORT(16384, "Export")
		;

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Workorder_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Workorder_Permission(long permission, String permissionName, Workorder_Permission parent, Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}
	
	public static enum Inventory_Permission implements NewPermissionInterface {

		CREATE (1,"Create"),
		READ_GROUP(-1,"Read"),
		
		READ_OWN(2, "Own", Inventory_Permission.READ_GROUP, Permission_Child_Type.RADIO),
		READ(4, "All", Inventory_Permission.READ_GROUP, Permission_Child_Type.RADIO),
		
		UPDATE_GROUP(-1,"Update"),
		
		UPDATE_OWN(8, "Own", Inventory_Permission.UPDATE_GROUP, Permission_Child_Type.RADIO),
		UPDATE(16, "All", Inventory_Permission.UPDATE_GROUP, Permission_Child_Type.RADIO),

		DELETE_GROUP(-1,"Delete"),

		DELETE_OWN(32,"Own",Inventory_Permission.DELETE_GROUP,Permission_Child_Type.RADIO),
		DELETE(64,"All",Inventory_Permission.DELETE_GROUP,Permission_Child_Type.RADIO),
		
		IMPORT(128,"Import"),
		EXPORT(256, "Export")
		;

		long permission;
		String permissionName;
		NewPermissionInterface parent;
		List<NewPermissionInterface> childs;
		Permission_Child_Type childType;

		Inventory_Permission(long permission, String permissionName) {
			this.permission = permission;
			this.permissionName = permissionName;
		}

		Inventory_Permission(long permission, String permissionName, Inventory_Permission parent, Permission_Child_Type childType) {
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

		public NewPermissionInterface getParent() {
			return parent;
		}

		public List<NewPermissionInterface> getChilds() {
			return childs;
		}

		public Permission_Child_Type getChildType() {
			return this.childType;
		}

		public void addChild(NewPermissionInterface child) {
			childs = childs == null ? new ArrayList<>() : childs;
			childs.add(child);
		}

		private static final List<NewPermissionInterface> permissionList = Collections.unmodifiableList(initTypeMap());

		private static List<NewPermissionInterface> initTypeMap() {
			List<NewPermissionInterface> typeMap = new ArrayList<NewPermissionInterface>();

			for (NewPermissionInterface type : values()) {

				if (type.getParent() != null) {
					type.getParent().addChild(type);
				} else {
					typeMap.add(type);
				}
			}
			return typeMap;
		}

		public static List<NewPermissionInterface> getAllPermissions() {
			return permissionList;
		}
	}
	
}
