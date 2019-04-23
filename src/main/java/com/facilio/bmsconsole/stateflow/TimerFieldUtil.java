package com.facilio.bmsconsole.stateflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.constants.FacilioConstants;

public class TimerFieldUtil {
	
	private static Map<String, TimerField> moduleTimerField = Collections.unmodifiableMap(initMap());
	
	public static Map<String, TimerField> initMap() {
		Map<String, TimerField> map = new HashMap<>();
		
		map.put(FacilioConstants.ContextNames.WORK_ORDER, new TimerField("actualWorkDuration", "resumedWorkStart"));
		return map;
	}
	
	public static TimerField getTimerField(String moduleName) {
		return moduleTimerField.get(moduleName);
	}
	
	public static class TimerField {
		private String totalTimeFieldName;
		private String resumeTimeFieldName;
		
		public String getTotalTimeFieldName() {
			return totalTimeFieldName;
		}

		public String getResumeTimeFieldName() {
			return resumeTimeFieldName;
		}

		public TimerField(String totalTimeFieldName, String resumeTimeFieldName) {
			this.totalTimeFieldName = totalTimeFieldName;
			this.resumeTimeFieldName = resumeTimeFieldName;
		}
	}
}
