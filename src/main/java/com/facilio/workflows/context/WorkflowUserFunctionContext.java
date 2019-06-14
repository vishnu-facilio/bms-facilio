package com.facilio.workflows.context;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Lexer;
import com.facilio.workflowv2.autogens.WorkflowV2Parser;

public class WorkflowUserFunctionContext extends WorkflowContext {
	
	private static final Logger LOGGER = Logger.getLogger(WorkflowUserFunctionContext.class.getName());
	
	Long nameSpaceId;
	String name;
	
	public Long getNameSpaceId() {
		return nameSpaceId;
	}
	public void setNameSpaceId(Long nameSpaceId) {
		this.nameSpaceId = nameSpaceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void fillNameFromScript() throws Exception {
		WorkflowFunctionVisitor visitor = null;
		
		InputStream stream = new ByteArrayInputStream(workflowV2String.getBytes(StandardCharsets.UTF_8));
		
		WorkflowV2Lexer lexer = new WorkflowV2Lexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
        
		WorkflowV2Parser parser = new WorkflowV2Parser(new CommonTokenStream(lexer));
        ParseTree tree = parser.parse();
        
        visitor = new WorkflowFunctionVisitor();
        visitor.setWorkflowContext(this);
        visitor.visitFunctionHeader(tree);
	}
}
