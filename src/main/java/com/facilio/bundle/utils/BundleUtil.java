package com.facilio.bundle.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;

import com.facilio.bundle.anotations.ExcludeInBundle;
import com.facilio.bundle.anotations.IncludeInBundle;
import com.facilio.modules.FieldUtil;

import lombok.extern.log4j.Log4j;

@Log4j
public class BundleUtil {

	
	public static void getFormattedObject(Object beanObject) throws Exception {
		
		List<Class<?>> superClasses = getSuperClasses(beanObject.getClass());
		
		JSONObject jsonValue = FieldUtil.getAsJSON(beanObject);
		
		for(Class<?> superClass : superClasses) {
			
			Reflections reflections = new Reflections(superClass.getName(), new FieldAnnotationsScanner());
			
			Set<Field> includeFields = reflections.getFieldsAnnotatedWith(IncludeInBundle.class);
			
			
			if(includeFields != null && !includeFields.isEmpty()) {
				
				for(Field field : includeFields) {
//					System.out.println("fields To Be added on include --- "+field.getName() +" value --- "+jsonValue.get(field.getName()));
				}
			}
			else {
				Set<Field> excludeFields = reflections.getFieldsAnnotatedWith(ExcludeInBundle.class);
				if(excludeFields != null && !excludeFields.isEmpty()) {
					
					Field[] fields = superClass.getDeclaredFields();
					
					List<String> excludeFieldNames = excludeFields.stream().map(Field::getName).collect(Collectors.toList());
					
					for(Field field : fields) {
						
						if(!excludeFieldNames.contains(field.getName())) {
//							System.out.println("fields To Be added on exclude --- "+field.getName() +" value --- "+jsonValue.get(field.getName()));
						}
					}
				}
			}
			
		}
		
	}
	
	private static List<Class<?>> getSuperClasses(Class<?> testClass) {
        ArrayList<Class<?>> results = new ArrayList<Class<?>>();
        Class<?> current = testClass;
        while (current != null && !current.getName().equals(Object.class.getName())) {
            results.add(current);
            current = current.getSuperclass();
        }
        return results;
    }
	
}
