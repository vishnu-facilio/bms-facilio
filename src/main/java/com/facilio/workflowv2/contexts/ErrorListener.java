package com.facilio.workflowv2.contexts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class ErrorListener implements ANTLRErrorListener,Serializable{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	List<String> errors = null;

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	private static final Logger LOGGER = Logger.getLogger(ErrorListener.class.getName());
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,String msg, RecognitionException e) {
		errors = errors == null ? new ArrayList<>() : errors;
		errors.add(String.format(Locale.ROOT, "Exception parsing script: '%s' on line %s, position %s", msg, line, charPositionInLine));
	}

	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,BitSet ambigAlts, ATNConfigSet configs) {
		
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,BitSet conflictingAlts, ATNConfigSet configs) {
		
	}

	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,ATNConfigSet configs) {
		
	}
	
	public boolean hasErrors() {
		return errors == null ? false : true ;
	}
	
	public void printErrors() {
		if (hasErrors()) {
			for(int i=0;i<errors.size();i++) {
				LOGGER.fine( i+1 +"."+errors.get(i));
			}
		}
	}
	
	public String getErrorsAsString() {
		StringBuilder builder = new StringBuilder();
		if (hasErrors()) {
			for(int i=0;i<errors.size();i++) {
				builder.append(i+1 +"."+errors.get(i) +"\n");
			}
		}
		return builder.toString();
	}
}
