package com.facilio.bmsconsoleV3.context;
import java.util.List;
import com.facilio.bmsconsole.context.MultiResourceContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutesContext extends V3Context {

	private  String name;
	
	private String description;
	
	private List<MultiResourceContext> multiResource;

}
