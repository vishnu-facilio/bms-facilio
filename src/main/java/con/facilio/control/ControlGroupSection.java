package con.facilio.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ControlGroupSection extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String name;
	ControlGroupContext controlGroup;
	Section_Type type;
	List<ControlGroupAssetCategory> categories;
	
	public void addCategory(ControlGroupAssetCategory category) {
		if(categories == null) { categories = new ArrayList<ControlGroupAssetCategory>(); }
		categories.add(category);
	}
	
	public void setType(int typeint) {
		type = Section_Type.getAllOptions().get(typeint);
	}
	
	public int getType() {
		if(type != null) { return type.getIntVal(); }
		return -1;
	}
	
	
	public enum Section_Type {
		
		HVAC(1, "HVAC"),
		LIGHTING(2, "Lighting"),
		ELEVATOR(3, "Elevator"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Section_Type(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Section_Type> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Section_Type> initTypeMap() {
			Map<Integer, Section_Type> typeMap = new HashMap<>();

			for (Section_Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Section_Type> getAllOptions() {
			return optionMap;
		}
	}
}
