package com.facilio.bmsconsole.context;
public class HistoricalJobMLContext{
		long mlId = -1;
		long executionInterval = -1;
		
		public long getMlId() {
			return mlId;
		}
		public void setMlId(long mlId) {
			this.mlId = mlId;
		}
		public long getExecutionInterval() {
			return executionInterval;
		}
		public void setExecutionInterval(long executionInterval) {
			this.executionInterval = executionInterval;
		}
		
	}