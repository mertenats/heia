package ch.heiafr.tic;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

interface WebResource {
	/*
	 * method for writing the response to GET requests
	 */
	public abstract void writeGetResponse(HttpServletRequest request, HttpServletResponse response) throws IOException;

	/*
	 * method for handling PUT requests
	 */
	public abstract void handlePutRequest(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
