package org.roussev.hello.server;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;

import org.roussev.hello.client.AbstractRemoteService;
import org.roussev.hello.client.MyException;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.UnexpectedException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;

public class AbstractRemoteServiceServlet extends RemoteServiceServlet
		implements AbstractRemoteService {

	@Override
	public String processCall(String payload) throws SerializationException {
		try {
			RPCRequest rpcRequest = RPC.decodeRequest(payload, this.getClass(),
					this);
			return handleProcessCall(this, rpcRequest);
		} catch (IncompatibleRemoteServiceException ex) {
			log("Incompatible error was thrown while processing this call.", ex);
			return RPC.encodeResponseForFailure(null, ex);
		}
	}

	@Override
	protected void doUnexpectedFailure(Throwable e) {
		discoverAndWriteError(e, e);
	}

	/**
	 * This method reson vivre is to hook the recognized MyException type into
	 * GWT Serialization policy
	 */
	@Override
	public void hookException() throws MyException {
		// Blank
	}

	private String handleProcessCall(Object target, RPCRequest rpcRequest)
			throws SerializationException {

		Method serviceMethod = rpcRequest.getMethod();
		Object[] params = rpcRequest.getParameters();
		SerializationPolicy serializationPolicy = rpcRequest
				.getSerializationPolicy();
		try {
			return RPC.invokeAndEncodeResponse(target, serviceMethod, params,
					serializationPolicy);
		} catch (UnexpectedException ex) {
			Throwable cause = ex.getCause();
			if (cause.getClass().equals(MyException.class)) {
				throw new MyException();
			}
			return encodeResponse(cause.getClass(), cause, true,
					serializationPolicy);
		}
	}

	private void discoverAndWriteError(Throwable masterError, Throwable e) {
		if (e == null) {
			writeError(new UnexpectedException(masterError.getMessage(),
					masterError));
			return;
		}

		Throwable cause = e.getCause();
		if (cause == null) {
			writeError(new UnexpectedException(masterError.getMessage(),
					masterError));
			return;
		}

		if (cause.getClass().equals(MyException.class)) {
			writeError(new MyException());
			return;
		} else {
			// continue with recursion cause discovery
			discoverAndWriteError(masterError, cause);
		}
	}

	/**
	 * HTTP code borrowed from com.google.gwt.user.server.rpc.RPCServletUtils
	 */
	private void writeError(Throwable cause) {

		// Send SC_OK/200 status, serialize the gwt error, flush it to response,
		// and let the client deal with the business error

		HttpServletResponse resp = getThreadLocalResponse();
		try {
			resp.setContentType("text/plain");
			resp.setStatus(HttpServletResponse.SC_OK);
			String excMsg = RPC.encodeResponseForFailure(null, cause);
			resp.getWriter().write(excMsg);

		} catch (IOException e) {
			log("Failed to send failure to client", e);

		} catch (SerializationException e) {
			log("Failed to send failure to client", e);
		}
	}

	/**
	 * Borrowed from RPC.java Returns a string that encodes the results of an
	 * RPC call. Private overload that takes a flag signaling the preamble of
	 * the response payload.
	 */
	private static String encodeResponse(Class<?> responseClass, Object object,
			boolean wasThrown, SerializationPolicy serializationPolicy)
			throws SerializationException {

		ServerSerializationStreamWriter stream = new ServerSerializationStreamWriter(
				serializationPolicy);

		stream.prepareToWrite();
		if (responseClass != void.class) {
			stream.serializeValue(object, responseClass);
		}

		String bufferStr = (wasThrown ? "//EX" : "//OK") + stream.toString();
		return bufferStr;
	}   

}