package ch.heiafr.tic;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet(urlPatterns = { "/rest/weatherstation/*" })
public class RestServlet extends HttpServlet {

	// data members
	// weather station instances (the key of the map is the location)
	private HashMap<String, WebResource> m_weatherStationMap;
	// mGsonBuilder must not be serialized (so declare it as transient)
	static transient GsonBuilder m_gsonBuilder = null;

	private static final long serialVersionUID = 1L;

	private static final String WEATHER_STATION_INITIAL_LOCATIONS[] = { "c10-16", "c10-21" };

	/*
	 * patterns used for parsing the URIs
	 */
	public final static String COAP_PROXY_URL_PATTERN = "/TP3_CoapProxy/";
	public final static String WEATHERSTATION_URL_PATTERN = "rest/weatherstation/";

	public RestServlet() {
		// configure GSON
		m_gsonBuilder = new GsonBuilder();
		// set the appropriate serializer for the WeatherStation class
		m_gsonBuilder.registerTypeAdapter(WeatherStation.class, new WeatherStationSerializer());
		m_gsonBuilder.setPrettyPrinting();

		// create all existing weather stations
		m_weatherStationMap = getWeatherStationMap();
	}

	/*
	 * returns the list of devices
	 */
	private static HashMap<String, WebResource> getWeatherStationMap() {
		// deliver weather stations at location "C10-16" and "C10-22"
		WeatherStation l_weatherStation1 = new WeatherStation(WEATHER_STATION_INITIAL_LOCATIONS[0], m_gsonBuilder, "1", "55", "950");
		WeatherStation l_weatherStation2 = new WeatherStation(WEATHER_STATION_INITIAL_LOCATIONS[1], m_gsonBuilder, "35", "75", "899");
		WeatherStation l_weatherStation3 = new WeatherStation("c00-22", m_gsonBuilder, "16", "25", "970");

		// create an HashMap with all weather stations instance and return it
		HashMap<String, WebResource> l_weatherStationMap = new HashMap<>();
		l_weatherStationMap.put(l_weatherStation1.getWeatherStationLocation(), l_weatherStation1);
		l_weatherStationMap.put(l_weatherStation2.getWeatherStationLocation(), l_weatherStation2);
		l_weatherStationMap.put(l_weatherStation3.getWeatherStationLocation(), l_weatherStation3);

		return l_weatherStationMap;
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
	protected void doGet(HttpServletRequest p_httpRequest, HttpServletResponse p_httpResponse)
			throws ServletException, IOException {
		// get the URI
		String l_httpUri = p_httpRequest.getRequestURI();

		// handle first the « CoapProxy/rest/weatherstation/» request
		if (l_httpUri.equals(COAP_PROXY_URL_PATTERN + WEATHERSTATION_URL_PATTERN)) {
			// deliver the serialized list of weather stations
			@SuppressWarnings("static-access")
			Gson l_gson = this.m_gsonBuilder.create();
			String l_json = l_gson.toJson(this);
			p_httpResponse.getWriter().println(l_json);
			System.out.println("RestServlet.doGet() : \n" + l_json);
		} else {
			// handle other requests by first getting the web resource
			// corresponding to the request
			// and then calling the appropriate method on the WebResource
			// instance
			WebResource l_webResource = parseRequest(l_httpUri);
			if (l_webResource == null) {
				// handle this case properly
				p_httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				System.out.println("RestServlet.doGet() : No object instance for this URI");
				return;
			}
			// call the appropriate method on the WebResource instance
			l_webResource.writeGetResponse(p_httpRequest, p_httpResponse);
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
	protected void doPut(HttpServletRequest p_httpRequest, HttpServletResponse p_httpResponse)
			throws ServletException, IOException {
		// get the URI
		String l_httpUri = p_httpRequest.getRequestURI();

		// handle the • « CoapProxy/rest/
		// weatherstation/<weatherStationLocation>/state» request
		WebResource l_webResource = parseRequest(l_httpUri);
		if (l_webResource == null) {
			// handle this case properly
			System.out.println("RestServlet.doPut() : No object instance for this URI");
			p_httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		// call the appropriate method on the WebResource instance
		l_webResource.handlePutRequest(p_httpRequest, p_httpResponse);
	}

	/**
	 * Method for parsing the URI and returning the corresponding WebResource
	 * instance
	 *
	 * @param request
	 *            - HTTP request
	 *
	 * @returns the instance of the object representing the WebResource instance
	 */
	private WebResource parseRequest(String p_httpUri) {
		// remove "/CoapProxy/rest/weatherstation/" from the uri
		String l_httpUri = p_httpUri.replace(COAP_PROXY_URL_PATTERN + WEATHERSTATION_URL_PATTERN, "");

		// get the position of the character "/"
		int l_indexOfSeparator = l_httpUri.lastIndexOf("/");

		// if the character "/" isn't present in the string
		if (l_indexOfSeparator == -1)
			return null;

		// get the location of the weather station
		String l_location = l_httpUri.substring(0, l_indexOfSeparator).toLowerCase();

		if (this.m_weatherStationMap.containsKey(l_location) == false) {
			System.out.println("RestServlet.parseRequest() : No weather station for this location");
			return null;
		} else {
			// return the instance of the weather station
			return this.m_weatherStationMap.get(l_location);
		}
	}
}
