package org.roussev.hello.server;

import org.roussev.hello.client.GreetingService;
import org.roussev.hello.client.MyException;

public class GreetingServiceImpl extends AbstractRemoteServiceServlet implements
    GreetingService {

  public String greetServer(String input) {
    { // let's just throw an error for demonstration
      throw new MyException();
    }
  }
}
