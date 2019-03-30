package com.facilio.bmsconsole.modules;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EnumField extends FacilioField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EnumField() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	protected EnumField(EnumField field) { // Do not forget to Handle here if new property is added
		super(field);
		this.values = field.values;
	}
	
	@Override
	public EnumField clone() {
		// TODO Auto-generated method stub
		return new EnumField(this);
	}

	private List<String> values;
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}

	public int getIndex(String value) {
		if (values != null) {
			int idx = values.indexOf(value);
			if (idx != -1) {
				idx += 1;
			}
			return idx;
		}
		return -1;
	}
	
	public String getValue(int idx) {
		if (values != null && idx > 0 && idx <= values.size()) {
			return values.get(idx-1);
		}
		return null;
	}
	
	@JsonIgnore
	public Map<Integer, Object> getEnumMap() {
		if (values != null) {
			return IntStream.range(0, values.size()).boxed().collect(Collectors.toMap(i -> i + 1, values::get));
		}
		return null;
	}
}
