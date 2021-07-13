package com.facilio.bundle.command;

import java.util.ArrayList;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;

import io.jsonwebtoken.lang.Collections;

public class PopulateBundleToNewOrgCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		JSONObject finalBundledObject = (JSONObject) context.get(BundleConstants.BUNDLED_COMPONENTS);
		
		ArrayList<BundleComponentsEnum> parents = BundleComponentsEnum.getParentComponentList();
		
		
		for(BundleComponentsEnum parentComponent : parents) {
			
			BundleComponentInterface bundleComponentObject = parentComponent.getBundleComponentClassInstance();
			
			JSONArray componentList = (JSONArray) finalBundledObject.get(parentComponent.getName());
			
			for(int i=0;i<componentList.size();i++) {
				
				JSONObject componentJson = (JSONObject) componentList.get(i);
				
				context.put(BundleConstants.COMPONENT_OBJECT, componentJson);
				
				bundleComponentObject.install((FacilioContext) context);
				
			}
			
			for(int i=0;i<componentList.size();i++) {
				
				JSONObject parentObject = (JSONObject) componentList.get(i);
				
				installChildComponents(parentComponent, parentObject, (FacilioContext) context);
			}
			
			for(int i=0;i<componentList.size();i++) {
				
				JSONObject componentJson = (JSONObject) componentList.get(i);
				
				context.put(BundleConstants.COMPONENT_OBJECT, componentJson);
				
				bundleComponentObject.postInstall((FacilioContext) context);
				
			}
			
			for(int i=0;i<componentList.size();i++) {
				
				JSONObject parentObject = (JSONObject) componentList.get(i);
				
				postInstallChildComponents(parentComponent, parentObject, (FacilioContext) context);
			}
		}
		
		return false;
	}

	private void postInstallChildComponents(BundleComponentsEnum parentComponent, JSONObject parentObject,FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList<BundleComponentsEnum> childList = BundleComponentsEnum.getParentChildMap().get(parentComponent);
		
		if(!Collections.isEmpty(childList)) {
			for(BundleComponentsEnum childComponent : childList) {
				
				BundleComponentInterface bundleComponentObject = childComponent.getBundleComponentClassInstance();
				
				JSONArray childObjects = (JSONArray) parentObject.get(childComponent.getName());
				
				if(childObjects != null) {
					
					for(int i=0;i<childObjects.size();i++) {
						
						JSONObject childObject = (JSONObject) childObjects.get(i);
						
						context.put(BundleConstants.PARENT_COMPONENT_OBJECT, parentObject);
						context.put(BundleConstants.COMPONENT_OBJECT, childObject);
						
						bundleComponentObject.postInstall(context);
						
					}
					for(int i=0;i<childObjects.size();i++) {
						
						JSONObject childObject = (JSONObject) childObjects.get(i);
							
						postInstallChildComponents(childComponent, childObject, context);
					}
				}
				
			}
		}
	}

	private void installChildComponents(BundleComponentsEnum parentComponent, JSONObject parentObject,FacilioContext context) throws Exception {
		
		ArrayList<BundleComponentsEnum> childList = BundleComponentsEnum.getParentChildMap().get(parentComponent);
		
		if(!Collections.isEmpty(childList)) {
			for(BundleComponentsEnum childComponent : childList) {
				
				BundleComponentInterface bundleComponentObject = childComponent.getBundleComponentClassInstance();
				
				JSONArray childObjects = (JSONArray) parentObject.get(childComponent.getName());
				
				if(childObjects != null) {
					
					for(int i=0;i<childObjects.size();i++) {
						
						JSONObject childObject = (JSONObject) childObjects.get(i);
						
						context.put(BundleConstants.PARENT_COMPONENT_OBJECT, parentObject);
						context.put(BundleConstants.COMPONENT_OBJECT, childObject);
						
						bundleComponentObject.install(context);
						
					}
					for(int i=0;i<childObjects.size();i++) {
						
						JSONObject childObject = (JSONObject) childObjects.get(i);
							
						installChildComponents(childComponent, childObject, context);
					}
				}
				
			}
		}
		
	}

}
