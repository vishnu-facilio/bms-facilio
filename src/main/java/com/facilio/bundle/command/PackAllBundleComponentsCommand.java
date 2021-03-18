package com.facilio.bundle.command;

import java.util.ArrayList;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;

public class PackAllBundleComponentsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList<BundleComponentsEnum> parents = BundleComponentsEnum.getParentComponentList();
		
		JSONObject finalPackedObject = new JSONObject();
		for(BundleComponentsEnum parentComponent : parents) {
			BundleComponentInterface bundleComponentObject = parentComponent.getBundleComponentClassInstance();
			
			JSONArray parentObjects = bundleComponentObject.getAllFormatedObject((FacilioContext) context);
			
			for(int i=0;i<parentObjects.size();i++) {
				
				JSONObject parentObject = (JSONObject) parentObjects.get(i);
				
				fillChildComponents(parentComponent, parentObject, (FacilioContext) context);
			}
			
			finalPackedObject.put(parentComponent.getName(), parentObjects);
		}
		
		context.put(BundleConstants.PACKED_COMPONENTS, finalPackedObject);
		return false;
	}
	
	
	private void fillChildComponents(BundleComponentsEnum parentComponent,JSONObject parentObject,FacilioContext context) throws Exception {
		
		ArrayList<BundleComponentsEnum> childList = BundleComponentsEnum.getParentChildMap().get(parentComponent);
		
		for(BundleComponentsEnum childComponent : childList) {
			
			BundleComponentInterface bundleComponentObject = childComponent.getBundleComponentClassInstance();
			
			context.put(BundleConstants.PARENT_COMPONENT_OBJECT, parentObject);
			
			JSONArray childObjects = bundleComponentObject.getAllFormatedObject(context);
			for(int i=0;i<childObjects.size();i++) {
				
				JSONObject childObject = (JSONObject) childObjects.get(i);
				
				fillChildComponents(childComponent, childObject, context);
			}
			
			parentObject.put(childComponent.getName(), childObjects);
		}
	}
	
	

}
