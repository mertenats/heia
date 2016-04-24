package ch.heiafr.tic;
/*
 C10-16
   sudo route add -inet6 2004::29C:FDff:feA0:5696 2001:620:40b:1030:ba27:ebff:febb:bf56
 C10-22
   sudo route add -inet6 2004::227:CDff:fe41:449E 2001:620:40b:1030:ba27:ebff:fe5c:d8ae
 C10-12
   sudo route add -inet6 2004::247:C0ff:feC5:3E3D 2001:620:40b:1030:ba27:ebff:feda:d5c
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/rest/weatherstation/*" })
public class RestServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * patterns used for parsing the URIs
	 */
	public final static String COAP_PROXY_URL_PATTERN = "/TP4_CoapProxy";
	public final static String REST_URL_PATTERN = "/rest";
	public final static String WEATHERSTATION_URL_PATTERN = "/weatherstation";

	// data members
	// weather station instances (the key of the map is the location)
	private HashMap<String, WebResource> mWeatherStationMap;

	public RestServlet() {
		// create all existing weather stations
		mWeatherStationMap = getWeatherStationMap();
	}

	/*
	 * returns the list of devices
	 */
	private static HashMap<String, WebResource> getWeatherStationMap() {
		// should be done using discovery on he network
		// here we build a static list
		HashMap<String, WebResource> weatherStationMap = new HashMap<String, WebResource>();

		// create the weather stations
		weatherStationMap.put("c10-12", new WeatherStation("C10-12", "2004::247:C0ff:feC5:3E3D"));
		weatherStationMap.put("c10-16", new WeatherStation("C10-16", "2004::29c:fdff:fea0:5696"));
		weatherStationMap.put("c10-22", new WeatherStation("C10-22", "2004::227:CDff:fe41:449E"));

		return weatherStationMap;
	}

	/**
	 * method called upon GET on url
	 * http://localhost:8080/CoapProxy/rest/weatherstation/*
	 *
	 * @param httpRequest
	 *            - HTTP request
	 * @param httpResponse
	 *            - HTTP response
	 */
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws ServletException, IOException {
		String httpUri = httpRequest.getRequestURI();
		System.out.println("RestServert.doGet() : HTTP URI: " + httpUri);

		if (httpUri.equalsIgnoreCase(COAP_PROXY_URL_PATTERN + REST_URL_PATTERN + WEATHERSTATION_URL_PATTERN) || httpUri
				.equalsIgnoreCase(COAP_PROXY_URL_PATTERN + REST_URL_PATTERN + WEATHERSTATION_URL_PATTERN + "/")) {
			// deliver the list of weather stations
			// retrieve all the weather stations and compute the final json
			String json = "{\"mWeatherStationMap\":{";
			int mapSize = mWeatherStationMap.size();

			for (WebResource station : mWeatherStationMap.values()) {
				// get the payload of the station
				String payload = station.serialize();
				if (payload == null) {
					return;
				}

				// we musn't add a "," after the last weather station's
				// occurence
				if (mapSize == 1) {
					json += payload;
				} else {
					json += payload;
					json += ",";
				}
				mapSize--;
			}
			json += "}}";

			httpResponse.getWriter().println(json);
			httpResponse.setContentType("application/json");
			httpResponse.setStatus(httpResponse.SC_OK);
		} else {
			WebResource webResource = parseRequest(httpUri);
			if (webResource == null) {
				httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			webResource.writeGetResponse(getResourcePath(httpRequest), httpResponse);
		}
	}

	/**
	 * method called upon PUT on url
	 * http://localhost:8080/CoapProxy/rest/weatherstation/*
	 *
	 * @param httpRequest
	 *            - HTTP request
	 * @param httpResponse
	 *            - HTTP response
	 */
	protected void doPut(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws ServletException, IOException {
		String httpUri = httpRequest.getRequestURI();

		WebResource webResource = parseRequest(httpUri);
		if (webResource == null) {
			httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// get the request content
		// BufferedReader reader = httpRequest.getReader();
		// String requestContent = reader.readLine();

		// extract the data (content) from the HTTP request
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = httpRequest.getReader();

		// browse through the request body
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		webResource.handlePutRequest(getResourcePath(httpRequest), sb.toString(), httpResponse);
	}

	/**
	 * Method for parsing the URI
	 *
	 * @param request
	 *            - HTTP request
	 *
	 * @returns the instance of the object representing the web resource
	 */
	private WebResource parseRequest(String httpUri) {
		// extract the web resource
		String weatherStationLocation = WeatherStation.getLocation(httpUri);
		if (weatherStationLocation != "") {
			if (mWeatherStationMap.containsKey(weatherStationLocation)) {
				return mWeatherStationMap.get(weatherStationLocation);
			}
			return null;
		}

		return null;
	}

	/*
	 * Method returning the resource path
	 */
	private String getResourcePath(HttpServletRequest httpRequest) {
		String resourcePath = httpRequest.getRequestURI().replace(
				COAP_PROXY_URL_PATTERN + REST_URL_PATTERN + WEATHERSTATION_URL_PATTERN, WEATHERSTATION_URL_PATTERN);

		return resourcePath;
	}
}
