package com.facilio.qa.context.client.answers;

import java.util.ArrayList;
import java.util.List;

import com.facilio.qa.context.ClientAnswerContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class MatrixAnswerContext extends ClientAnswerContext<MatrixAnswerContext.MatrixAnswer> {

	MatrixAnswer answer;
	
	@Getter @Setter
	public static class MatrixAnswer {
		List<RowAnswer> rowAnswer;
		
		public void addRowAnswer(RowAnswer row) {
			
			rowAnswer = rowAnswer == null ? new ArrayList<RowAnswer>() : rowAnswer;
			rowAnswer.add(row);
		}
	}
	@Getter @Setter
	public static class RowAnswer {
		Long row;
		List<ColumnAnswer> columnAnswer;
		
		public void addColumnAnswer(ColumnAnswer collAns) {
			
			columnAnswer = columnAnswer == null ? new ArrayList<ColumnAnswer>() : columnAnswer;
			columnAnswer.add(collAns);
		}
	}

	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public static class ColumnAnswer {
		Long column;
		Object answer;
	}

}
