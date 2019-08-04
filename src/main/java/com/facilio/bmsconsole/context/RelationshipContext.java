package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class RelationshipContext implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id = -1;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private long orgId = -1;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int relationshipType = -1;

	public RelationshipType getRelationshipTypeEnum() {
		return RelationshipType.valueOf(relationshipType);
	}

	public int getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(int relationshipType) {
		this.relationshipType = relationshipType;
	}

	public static enum RelationshipType {
		UNIDIRECTIONAL(1), BIDERECTIONAL(2);

		private int value;

		private RelationshipType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static RelationshipType valueOf(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}

}
