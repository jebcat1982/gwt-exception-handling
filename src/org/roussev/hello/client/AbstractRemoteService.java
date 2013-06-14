package org.roussev.hello.client;

import com.google.gwt.user.client.rpc.RemoteService;

public interface AbstractRemoteService extends RemoteService {

	void hookException() throws MyException;

}
