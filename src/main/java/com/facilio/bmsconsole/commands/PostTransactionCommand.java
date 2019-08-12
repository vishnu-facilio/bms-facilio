package com.facilio.bmsconsole.commands;

public interface PostTransactionCommand {
	boolean postExecute() throws Exception;
	default void onError() throws Exception {
	}
}