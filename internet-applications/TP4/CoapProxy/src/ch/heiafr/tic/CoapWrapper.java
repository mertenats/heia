package ch.heiafr.tic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

class CoapWrapper {
	/*
	 * patterns used for handling URIs
	 */
	private final static String COAP_SCHEME = "coap";
	private final static int COAP_PORT = 5683;
	private final static String WELL_KNOWN_ID = "/.well-known";

	/**
	 * method for performing a GET COAP request
	 *
	 * @param coapUri
	 *            - Uri for the coap request
	 * @param httpResponse
	 *            - Response written in a HttpServletResponse instance
	 * @throws IOException
	 */
	public static void doGet(URI coapURI, HttpServletResponse httpResponse) throws ServletException, IOException {
		// create a Coap client to make a request to the URI
		if (coapURI == null) {
			return;
		}
		
		CoapClient client = new CoapClient(coapURI);
		CoapResponse response = client.get(MediaTypeRegistry.APPLICATION_JSON);

		// if the Coap request succeed, write the response into the HTTP
		// response
		if (response.isSuccess()) {
			System.out.println("CoapProxy.doGet() : " + coapURI);
			httpResponse.setStatus(HttpServletResponse.SC_OK);
			httpResponse.setContentType("application/json");
			httpResponse.getWriter().println(response.getResponseText());
		} else {
			System.out.println("CoapProxy.doGet() : ERROR");
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * method for performing a GET COAP request
	 *
	 * @param coapUri
	 *            - Uri for the coap request
	 * @param returns
	 *            the Coap response
	 */
	public static String doGet(String coapURI) {
		// create a Coap client to make a request to the URI
		if (coapURI == null) {
			return null;
		}

		CoapClient client = new CoapClient(coapURI);
		CoapResponse response = client.get(MediaTypeRegistry.APPLICATION_JSON);

		// if the Coap request succeed, return the payload
		if (response.isSuccess()) {
			System.out.println("CoapProxy.doGet() : " + coapURI);
			return response.getResponseText();
		} else {
			System.out.println("CoapProxy.doGet() : ERROR " + coapURI);
		}
		return null;
	}

	/**
	 * method for performing a PUT COAP request
	 *
	 * @param coapUri
	 *            - Uri for the coap request
	 * @param httpResponse
	 *            - Response written in a HttpServletResponse instance
	 */
	public static void doPut(URI coapURI, String requestContent, HttpServletResponse httpResponse)
			throws ServletException {
		// test if the string contains "true" of "false"
		// create a valid json for the state
		String payload = null;
		if (requestContent.contains("true") && requestContent.contains("mState")) {
			payload = "{\"m_state\": \"true\"}";
		} else if (requestContent.contains("false") && requestContent.contains("mState")) {
			payload = "{\"m_state\": \"false\"}";
		} else {
			System.out.println("CoapProxy.doPut() : ERROR --> JSON not valid " + requestContent);
		}

		// create a Coap client to make a PUT request to the URI
		if (coapURI == null || payload == null) {
			return;
		}

		CoapClient client = new CoapClient(coapURI);
		CoapResponse response = client.put(payload, MediaTypeRegistry.APPLICATION_JSON);

		// if the Coap request succeed, write the response into the HTTP
		// response
		if (response.isSuccess()) {
			System.out.println("CoapProxy.doPut() : " + coapURI);
			httpResponse.setStatus(HttpServletResponse.SC_OK);
		} else {
			System.out.println("CoapProxy.doPut() : ERROR --> COAP request " + coapURI);
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * method called for discovering all resources available on a specific
	 * device
	 *
	 * @param coapUri
	 *            - Uri for the coap server returns a list of URIs with all URIs
	 *            available on the server based on the response received from
	 *            the response obtained to a request to /.well-known/core
	 * @throws URISyntaxException
	 */
	public static ArrayList<URI> discoverResources(String coapServerHostAddress) {
		// create a COAP client and discover the resources proposed by the
		// station
		CoapClient client = new CoapClient(COAP_SCHEME + "://[" + coapServerHostAddress + "]:" + COAP_PORT);
		Set<WebLink> coapResources = client
				.discover(/*
				 * COAP_SCHEME + "://[" + coapServerHostAddress +
				 * "]:" + COAP_PORT
				 */);
		ArrayList<URI> resourceURIList = new ArrayList<>();

		if (coapResources != null) {
			// example result: </weatherstation/humidity> null obs
			// leave it out the results that contain "/.well-known"
			// remove the characters "<" and ">" and add the resource to the
			// list
			for (WebLink coapResource : coapResources) {
				if (!coapResource.toString().contains(WELL_KNOWN_ID)) {
					int lastIndexOfChar = coapResource.toString().lastIndexOf(">");
					try {
						resourceURIList.add(new URI(COAP_SCHEME + "://[" + coapServerHostAddress + "]:" + COAP_PORT
								+ coapResource.toString().substring(1, lastIndexOfChar)));
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.out.println("CoapProxy.discoverResources() : ERROR");
		}

		System.out.println("CoapProxy.discoverResources() : " + resourceURIList.toString());

		return resourceURIList;
	}
}
