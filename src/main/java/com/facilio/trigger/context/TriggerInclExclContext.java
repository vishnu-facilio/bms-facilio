package com.facilio.trigger.context;

public class TriggerInclExclContext {

		long id = -1l;
		long orgId = -1l;
		long triggerId = -1l;
		long resourceId = -1l;
		Boolean isInclude;
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public long getOrgId() {
			return orgId;
		}
		public void setOrgId(long orgId) {
			this.orgId = orgId;
		}
		public long getTriggerId() {
			return triggerId;
		}
		public void setTriggerId(long triggerId) {
			this.triggerId = triggerId;
		}
		public long getResourceId() {
			return resourceId;
		}
		public void setResourceId(long resourceId) {
			this.resourceId = resourceId;
		}
		public Boolean getIsInclude() {
			return isInclude;
		}
		public void setIsInclude(Boolean isInclude) {
			this.isInclude = isInclude;
		}

}
