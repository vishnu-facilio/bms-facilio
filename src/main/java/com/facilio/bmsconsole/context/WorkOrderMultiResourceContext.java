package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class WorkOrderMultiResourceContext extends V3Context {
	
	private  V3AssetContext asset;

	private  BaseSpaceContext space;
	
	private String description;
	
	private Number sequence;
	
 	private V3WorkOrderContext parent;

}
