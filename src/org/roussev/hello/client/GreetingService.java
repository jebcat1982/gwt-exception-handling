package org.roussev.hello.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends AbstractRemoteService {

  String greetServer(String name);

}
