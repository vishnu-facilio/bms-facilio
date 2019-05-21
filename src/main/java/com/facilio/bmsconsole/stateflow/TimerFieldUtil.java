package com.facilio.bmsconsole.stateflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.modules.fields.FacilioField;
import com.facilio.constants.FacilioConstants;

public class TimerFieldUtil {
	
	private static Map<String, TimerField> moduleTimerField = Collections.unmodifiableMap(initMap());
	
	public static Map<String, TimerField> initMap() {
		Map<String, TimerField> map = new HashMap<>();
		
		map.put(FacilioConstants.ContextNames.WORK_ORDER, new TimerField("actualWorkDuration", "resumedWorkStart", "actualWorkStart", "actualWorkEnd"));
		return map;
	}
	
	public static TimerField getTimerField(String moduleName) {
		return moduleTimerField.get(moduleName);
	}
	
	public static class TimerField {
		private String totalTimeFieldName;
		private String resumeTimeFieldName;
		private String startTimeFieldName;
		private String endTimeFieldName;
		
		public String getTotalTimeFieldName() {
			return totalTimeFieldName;
		}

		public String getResumeTimeFieldName() {
			return resumeTimeFieldName;
		}

		public String getStartTimeFieldName() {
			return startTimeFieldName;
		}

		public String getEndTimeFieldName() {
			return endTimeFieldName;
		}
		
		public boolean isTimerEnabled() {
			return StringUtils.isNotEmpty(totalTimeFieldName) && StringUtils.isNotEmpty(resumeTimeFieldName) &&
					StringUtils.isNotEmpty(startTimeFieldName) && StringUtils.isNotEmpty(endTimeFieldName);
		}

		public TimerField(String totalTimeFieldName, String resumeTimeFieldName, String startTimeFieldName, String endTimeFieldName) {
			this.totalTimeFieldName = totalTimeFieldName;
			this.resumeTimeFieldName = resumeTimeFieldName;
			this.startTimeFieldName = startTimeFieldName;
			this.endTimeFieldName = endTimeFieldName;
		}

		public List<FacilioField> getAllFields(ModuleBean modBean, String moduleName) throws Exception {
			List<FacilioField> fields = new ArrayList<>();
			if (isTimerEnabled()) {
				fields.add(modBean.getField(totalTimeFieldName, moduleName));
				fields.add(modBean.getField(resumeTimeFieldName, moduleName));
				fields.add(modBean.getField(startTimeFieldName, moduleName));
				fields.add(modBean.getField(endTimeFieldName, moduleName));
			}
			return fields;
		}

		public Collection<? extends FacilioField> getAllFields(ModuleBean modBean, Object m) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
