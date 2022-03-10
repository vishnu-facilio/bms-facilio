package com.facilio.qa.context.client.answers;

import java.util.List;

import com.facilio.qa.context.ClientAnswerContext;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatrixAnswerContext extends ClientAnswerContext<MatrixAnswerContext.MatrixAnswer> {

	MatrixAnswer answer;
	
	@Getter @Setter
	public static class MatrixAnswer {
		List<RowAnswer> rowAnswer;
	}
	@Getter @Setter
	public static class RowAnswer {
		Long row;
		List<ColumnAnswer> columnAnswer;
	}

	@Getter @Setter
	public static class ColumnAnswer {
		Long column;
		Object answer;
	}

}
