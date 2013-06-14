package org.roussev.hello.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MyException extends RuntimeException implements IsSerializable {

  private static final long serialVersionUID = -2134223926531237172L;

  public MyException() {
    super();
  }
  
}
