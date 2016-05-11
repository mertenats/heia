package ch.heiafr.tic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// class used for implementing a weather station
class WeatherStation implements WebResource {
	/*
	 * data members
	 */
	private String mWeatherStationLocation;
	private String mCoapServerHostAddress;
	// URIs for all resources discovered on the COAP server
	// this includes
	private ArrayList<URI> mResourceURIList;

	private static final String TEMPERATURE_ID = "temperature";
	private static final String HUMIDITY_ID = "humidity";
	private static final String PRESSURE_ID = "pressure";
	private static final String STATE_ID = "state";
	private static final String WEATHERSTATION_ID = "weatherstation";

	// coap://[2004::29c:fdff:fea0:5696]:5683/weatherstation
	private static final String PROTOCOL = "coap://[";
	private static final String PORT = "]:5683/";

	/*
	 * constructor
	 */
	public WeatherStation(String weatherStationLocation, String coapServerHostAddress) {
		mWeatherStationLocation = weatherStationLocation;
		mCoapServerHostAddress = coapServerHostAddress;

		mResourceURIList = CoapWrapper.discoverResources(mCoapServerHostAddress);
	}

	/*
	 * returns the index of the resource when the URI is of the LED type, null
	 * otherwise
	 */
	public static String getLocation(String resourcePath) {
		// remove "/CoapProxy/rest/weatherstation/" from the uri
		String httpUri = resourcePath.replace(RestServlet.COAP_PROXY_URL_PATTERN + RestServlet.REST_URL_PATTERN
				+ RestServlet.WEATHERSTATION_URL_PATTERN + "/", "");

		// get the position of the character "/"
		int indexOfSeparator = httpUri.lastIndexOf("/");

		// test if the character "/" was present in the string
		if (indexOfSeparator == -1) {
			// do nothing...
		} else {
			httpUri = httpUri.substring(0, indexOfSeparator);
		}

		// get the location of the weather station
		System.out.println("WeatherStation.getLocation() : location: " + httpUri);

		return httpUri.toLowerCase();
	}

	// implementation of WebResource methods
	@Override
	public void writeGetResponse(String resourcePath, HttpServletResponse httpResponse)
			throws IOException, ServletException {
		System.out.println("WeatherStation.writeGetResponse()");
		URI coapUri = getCOAPUri(resourcePath);
		if (coapUri == null) {
			return;
		}

		CoapWrapper.doGet(coapUri, httpResponse);
	}

	public void handlePutRequest(String resourcePath, String requestContent, HttpServletResponse httpResponse)
			throws IOException, ServletException {
		System.out.println("WeatherStation.handlePutRequest()");
		URI coapUri = getCOAPUri(resourcePath);
		if (coapUri == null) {
			return;
		}

		CoapWrapper.doPut(coapUri, requestContent, httpResponse);
	}

	public String serialize() {
		String payload = CoapWrapper.doGet(PROTOCOL + mCoapServerHostAddress + PORT + WEATHERSTATION_ID);
		if (payload == null) {
			System.out.println("WeatherStation.serialize() : payload is null");
			return null;
		}

		// add "c10-16": to return a valid json
		String json = "\"" + mWeatherStationLocation + "\":";
		json += payload;

		return json;
	}

	/*
	 * returns the COAP URI based on the resource path
	 */
	private URI getCOAPUri(String resourcePath) {
		resourcePath = resourcePath.toLowerCase();
		System.out.println("WeatherStation.getCOAPUri() : resourcePath: " + resourcePath);

		if (resourcePath.contains(RestServlet.WEATHERSTATION_URL_PATTERN + "/")) {
			// remove /weatherstation/
			resourcePath = resourcePath.replace(RestServlet.WEATHERSTATION_URL_PATTERN + "/", "");
			System.out.println("WeatherStation.getCOAPUri() : resourcePath cut: " + resourcePath);

			String uri = null;
			// compute the COAP URI from the desired resource
			if (resourcePath.equals(mWeatherStationLocation.toLowerCase() + "/" + TEMPERATURE_ID)) {
				uri = PROTOCOL + mCoapServerHostAddress + PORT + WEATHERSTATION_ID + "/" + TEMPERATURE_ID;
			} else if (resourcePath.equals(mWeatherStationLocation.toLowerCase() + "/" + HUMIDITY_ID)) {
				uri = PROTOCOL + mCoapServerHostAddress + PORT + WEATHERSTATION_ID + "/" + HUMIDITY_ID;
			} else if (resourcePath.equals(mWeatherStationLocation.toLowerCase() + "/" + PRESSURE_ID)) {
				uri = PROTOCOL + mCoapServerHostAddress + PORT + WEATHERSTATION_ID + "/" + PRESSURE_ID;
			} else if (resourcePath.equals(mWeatherStationLocation.toLowerCase() + "/" + STATE_ID)) {
				uri = PROTOCOL + mCoapServerHostAddress + PORT + WEATHERSTATION_ID + "/" + STATE_ID;
			} else if (resourcePath.equals(mWeatherStationLocation.toLowerCase() + "/")) {
				uri = PROTOCOL + mCoapServerHostAddress + PORT + WEATHERSTATION_ID;
			} else if (resourcePath.equals(mWeatherStationLocation.toLowerCase())) {
				uri = PROTOCOL + mCoapServerHostAddress + PORT + WEATHERSTATION_ID;
			}
			System.out.println("WeatherStation.getCOAPUri() : uri: " + uri);

			if (uri != null) {
				URI coapURI = null;
				try {
					coapURI = new URI(uri);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

				// test if the COAP URI has been previously discovered
				if (mResourceURIList.contains(coapURI)) {
					return coapURI;
				}
			}
		}
		return null;
	}

	@Override
	public void writeObserveResponse(String resourcePath, HttpServletResponse httpResponse,
			HttpServletRequest httpRequest) {
		System.out.println("WeatherStation.writeObserveResponse()");

		URI coapUri = getCOAPUri(resourcePath);
		if (coapUri == null) {
			return;
		}

		CoapWrapper.doObserve(coapUri, httpResponse, httpRequest);
	}
}
