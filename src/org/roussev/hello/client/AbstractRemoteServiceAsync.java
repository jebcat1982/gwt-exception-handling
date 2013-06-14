package org.roussev.hello.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AbstractRemoteServiceAsync {

	void hookException(AsyncCallback<Void> callback);
	
}
