package ch.heiafr.tic;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

interface WebResource {
	/*
	 * method for writing the response to GET requests
	 */
	public abstract void writeGetResponse(String resourcePath, HttpServletResponse response)
			throws IOException, ServletException;

	/*
	 * method for handling PUT requests
	 */
	public abstract void handlePutRequest(String resourcePath, String requestContent, HttpServletResponse response)
			throws IOException, ServletException;

	/*
	 * method for getting the serialized version of the web resource
	 */
	//public abstract void serialize(ServletOutputStream outputStream) throws IOException, ServletException;
	public abstract String serialize();
}
