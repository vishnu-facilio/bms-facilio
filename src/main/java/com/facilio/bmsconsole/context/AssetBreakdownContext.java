package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class AssetBreakdownContext extends ModuleBaseWithCustomFields{
       private static final long serialVersionUID = 1L;
       private long id = -1;

       public long getId() {
               return id;
       }

       public void setId(long id) {
               this.id = id;
       }

       private long parentId = -1;

       public long getParentId() {
               return parentId;
       }

       public void setParentId(long parentId) {
               this.parentId = parentId;
       }

       private long fromtime = -1;

       public long getFromtime() {
               return fromtime;
       }

       public void setFromtime(long fromtime) {
               this.fromtime = fromtime;
       }

       private long totime = -1;

       public long getTotime() {
               return totime;
       }

       public void setTotime(long totime) {
               this.totime = totime;
       }

       private long duration = -1; // In seconds

       public long getDuration() {
               return duration;
       }

       public void setDuration(long duration) {
               this.duration = duration;
       }
       
    private long timeBetweenFailure = -1; // In seconds
	public long getTimeBetweenFailure() {
		return timeBetweenFailure;
	}
	public void setTimeBetweenFailure(long timeBetweenFailure) {
		this.timeBetweenFailure = timeBetweenFailure;
	}

	private String condition;

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
