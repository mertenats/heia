package ch.heiafr.tic;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
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
			httpResponse.setStatus(/*convertCoapResponseCode(response)*/HttpServletResponse.SC_OK);
			httpResponse.setContentType("application/json");
			httpResponse.getWriter().println(response.getResponseText());
		} else {
			System.out.println("CoapProxy.doGet() : ERROR");
			httpResponse.setStatus(convertCoapResponseCode(response));
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
			httpResponse.setStatus(/*convertCoapResponseCode(response)*/HttpServletResponse.SC_OK);
		} else {
			System.out.println("CoapProxy.doPut() : ERROR --> COAP request " + coapURI);
			httpResponse.setStatus(convertCoapResponseCode(response));
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

	public static void doObserve(final URI coapURI, final HttpServletResponse httpResponse,
			HttpServletRequest httpRequest) {
		// create a Coap client to make a request to the URI
		if (coapURI == null) {
			return;
		}

		// add the request to the waiting list and return
		final AsyncContext asyncContext = httpRequest.startAsync(httpRequest, httpResponse);
		asyncContext.setTimeout(0);

		asyncContext.addListener(new AsyncListener() {
			@Override
			public void onStartAsync(AsyncEvent event) throws IOException {
				System.out.println("CoapProxy.addToWaitingList() : onStartAsync() : event: " + event.toString());
			}

			@Override
			public void onComplete(AsyncEvent event) throws IOException {
				System.out.println("CoapProxy.addToWaitingList() : onComplete()");
			}

			@Override
			public void onTimeout(AsyncEvent event) throws IOException {
				System.out.println("CoapProxy.addToWaitingList() : onTimeout() : event: " + event.toString());
				asyncContext.complete();
				// relation.proactiveCancel();
			}

			@Override
			public void onError(AsyncEvent event) throws IOException {
				System.out.println("CoapProxy.addToWaitingList() : onError() : event: " + event.toString());
				asyncContext.complete();
				// relation.proactiveCancel();
			}
		});

		CoapClient client = new CoapClient(coapURI);
		CoapObserveRelation relation = client.observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				try {
					((HttpServletResponse) asyncContext.getResponse()).setStatus(HttpServletResponse.SC_OK);
					asyncContext.getResponse().setContentType("text/event-stream, charset=UTF-8");
					((HttpServletResponse) asyncContext.getResponse()).getWriter()
							.write("data:" + response.getResponseText() + "\n\n");
					asyncContext.getResponse().getWriter().flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(response.getResponseText());
			}

			@Override
			public void onError() {
				((HttpServletResponse) asyncContext.getResponse()).setStatus(HttpServletResponse.SC_NOT_FOUND);
				asyncContext.complete();
			}
		});
	}

	private static int convertCoapResponseCode(CoapResponse coapResponse) {
		switch (coapResponse.getCode()) {
		case VALID:
			return HttpServletResponse.SC_OK;
		case CREATED:
			return HttpServletResponse.SC_CREATED;
		case DELETED:
			return HttpServletResponse.SC_NOT_MODIFIED;
		case BAD_REQUEST:
			return HttpServletResponse.SC_BAD_REQUEST;
		case NOT_FOUND:
			return HttpServletResponse.SC_NOT_FOUND;
		case FORBIDDEN:
			return HttpServletResponse.SC_FORBIDDEN;
		case NOT_ACCEPTABLE:
			return HttpServletResponse.SC_NOT_ACCEPTABLE;
		case PRECONDITION_FAILED:
			return HttpServletResponse.SC_PRECONDITION_FAILED;
		case REQUEST_ENTITY_TOO_LARGE:
			return HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;
		case UNSUPPORTED_CONTENT_FORMAT:
			return HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
		case INTERNAL_SERVER_ERROR:
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		case NOT_IMPLEMENTED:
			return HttpServletResponse.SC_NOT_IMPLEMENTED;
		case PROXY_NOT_SUPPORTED:
		case BAD_GATEWAY:
			return HttpServletResponse.SC_BAD_GATEWAY;
		case SERVICE_UNAVAILABLE:
			return HttpServletResponse.SC_SERVICE_UNAVAILABLE;
		default:
			break;
		}
		return HttpServletResponse.SC_BAD_REQUEST;
	}
}
