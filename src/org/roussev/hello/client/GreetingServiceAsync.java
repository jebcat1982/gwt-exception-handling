package org.roussev.hello.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync extends AbstractRemoteServiceAsync {

  void greetServer(String input, AsyncCallback<String> callback);

}
