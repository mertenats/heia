package ch.heiafr.tic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// class used for implementing a weather station
class WeatherStation implements WebResource {

	/*
	 * data members (used by Gson for formatting the JSON response)
	 */
	private String m_weatherStationLocation;
	private State m_state;
	private ArrayList<Sensor> m_sensorList;
	private final GsonBuilder m_gsonBuilder;

	/*
	 * patterns used for parsing the URIs
	 */
	private final static String STATE_ID = "state";
	private final static String TEMPERATURE_SENSOR_ID = "Temperature";
	private final static String HUMIDITY_SENSOR_ID = "Humidity";
	private final static String PRESSURE_SENSOR_ID = "Pressure";

	/*
	 * definitions representing virtual sensors
	 */
	private final static String[] SENSOR_ID_ARRAY = { TEMPERATURE_SENSOR_ID, HUMIDITY_SENSOR_ID, PRESSURE_SENSOR_ID };
	private final static String[] SENSOR_UNIT_ARRAY = { "&deg;C", "%", " hPa" };
	private final static String[] SENSOR_DEFAULT_VALUE_ARRAY = { "20", "70", "950" };

	/*
	 * constructor
	 */
	public WeatherStation(String p_weatherStationLocation, GsonBuilder p_gsonBuilder) {
		// initialize data members
		this.m_weatherStationLocation = p_weatherStationLocation.toLowerCase();
		this.m_gsonBuilder = p_gsonBuilder;

		// state
		this.m_state = new State(false);

		// sensors
		Sensor l_temperatureSensor = new Sensor(SENSOR_ID_ARRAY[0], SENSOR_UNIT_ARRAY[0],
				SENSOR_DEFAULT_VALUE_ARRAY[0]);
		Sensor l_humiditySensor = new Sensor(SENSOR_ID_ARRAY[1], SENSOR_UNIT_ARRAY[1], SENSOR_DEFAULT_VALUE_ARRAY[1]);
		Sensor l_pressureSensor = new Sensor(SENSOR_ID_ARRAY[2], SENSOR_UNIT_ARRAY[2], SENSOR_DEFAULT_VALUE_ARRAY[2]);

		ArrayList<Sensor> l_sensorList = new ArrayList<>();
		l_sensorList.add(l_temperatureSensor);
		l_sensorList.add(l_humiditySensor);
		l_sensorList.add(l_pressureSensor);

		this.m_sensorList = l_sensorList;
	}

	/*
	 * returns the index of the resource when the URI is of the correct type, -1
	 * otherwise
	 */
	public static String getLocation(String p_httpUri) {
		// remove "/CoapProxy/rest/weatherstation/" from the uri
		String l_httpUri = p_httpUri
				.replace(RestServlet.COAP_PROXY_URL_PATTERN + RestServlet.WEATHERSTATION_URL_PATTERN, "");

		// get the position of the character "/"
		int l_indexOfSeparator = l_httpUri.lastIndexOf("/");

		// test if the character "/" was present in the string
		if (l_indexOfSeparator == -1)
			return null;

		// get the location of the weather station
		String l_location = l_httpUri.substring(0, l_indexOfSeparator);
		System.out.println("WeatherStation.getLocation() : Weather station location : " + l_location);

		return l_location.toLowerCase();
	}

	// implementation of WebResource methods
	@Override
	public void writeGetResponse(HttpServletRequest p_httpRequest, HttpServletResponse p_httpResponse)
			throws IOException {
		// get the URI as a string from the HTTP request
		String l_httpUri = p_httpRequest.getRequestURI();

		// set the content type of the HTTP response
		p_httpResponse.setContentType("application/json");

		// handle the «
		// CoapProxy/rest/weatherstation/<weatherStationLocation>/<sensor>»
		// request and get the sensor object instance corresponding to the
		// request (may be null if the request is not for a sensor)
		Sensor l_sensor = getSensor(l_httpUri);
		if (l_sensor != null) {
			System.out.println("WeatherStation.writeGetResponse() : Sensor ID : " + l_sensor.getId());
			System.out.println("WeatherStation.writeGetResponse() : Sensor unit : " + l_sensor.getUnit());
			System.out.println("WeatherStation.writeGetResponse() : Sensor value : " + l_sensor.getValue());
			l_sensor.doGet(this.m_gsonBuilder, p_httpRequest, p_httpResponse);
			return;
		}

		// handle the «
		// CoapProxy/rest/weatherstation/<weatherStationLocation>/state» request
		// and get the state object instance corresponding to the request (may
		// be null if the request is not for the state)
		State l_state = getState(l_httpUri);
		if (l_state != null) {
			System.out.println("WeatherStation.writeGetResponse() : Weather station state : " + l_state.getState());
			l_state.doGet(this.m_gsonBuilder, p_httpRequest, p_httpResponse);
			return;
		}

		// handle the «CoapProxy/rest/weatherstation/<weatherStationLocation>/»
		// request
		Gson l_gson = this.m_gsonBuilder.create();
		String l_json = l_gson.toJson(this);
		p_httpResponse.getWriter().println(l_json);
		System.out.println("WeatherStation.writeGetResponse() : \n" + l_json);

		// sanity check
		String l_weatherStationLocation = getLocation(l_httpUri);
		if (!this.m_weatherStationLocation.equalsIgnoreCase(l_weatherStationLocation)) {
			//  should not get here
			throw new IOException("Invalid URI");
		}
	}

	public void handlePutRequest(HttpServletRequest p_request, HttpServletResponse p_response) throws IOException {
		// extract the data (content) from the HTTP request by using GSON and
		// the State class
		StringBuilder l_sb = new StringBuilder();
		BufferedReader l_reader = p_request.getReader();

		// browse through the request body
		String l_line;
		while ((l_line = l_reader.readLine()) != null) {
			l_sb.append(l_line);
		}
		String l_json = l_sb.toString();

		// test if the string contains "true" of "false"
		if (l_json.contains("true") || l_json.contains("false")) {
			// System.out.println("WeatherStation.handlePutRequest() : \n" +
			// l_json);
		} else {
			System.out.println("WeatherStation.handlePutRequest() : The request's body isn't formatted correctly");
			p_response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// create a state instance from the json
		Gson l_gson = new Gson();
		State l_state = l_gson.fromJson(l_json, State.class);

		// modify the mState data member based on the data content
		System.out.println("WeatherStation.handlePutRequest() : Weather station state (before PUT) : "
				+ this.getState().toString());
		this.m_state = l_state;
		System.out.println("WeatherStation.handlePutRequest() : Weather station state (after PUT) : "
				+ this.getState().toString());

		// set the appropriate status for the HTTP response
		p_response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Method for parsing the URI
	 * 
	 * @param p_httpUri
	 * @return the name of the ressource (state/temperature/humidity/pressure)
	 */
	private String getRessourceName(String p_httpUri) {
		// remove "/CoapProxy/rest/weatherstation/" from the uri
		String l_httpUri = p_httpUri
				.replace(RestServlet.COAP_PROXY_URL_PATTERN + RestServlet.WEATHERSTATION_URL_PATTERN, "");

		// get the position of the character "/"
		int l_indexOfSeparator = l_httpUri.lastIndexOf("/");

		// test if the character "/" was present in the string
		if (l_indexOfSeparator == -1)
			return null;

		// get the ressource specified in the uri
		String l_ressource = l_httpUri.substring(l_indexOfSeparator + 1);
		l_ressource.toLowerCase();
		return l_ressource;
	}

	/**
	 * Method for creating a sensor based on the HTTP URI
	 *
	 * @param request
	 *            - HTTP request
	 *
	 * @returns the instance of the object representing the sensor
	 */
	private Sensor getSensor(String p_httpUri) {
		// get the name of the desired ressource
		String l_ressource = getRessourceName(p_httpUri);

		// from the given desired ressource, it returns the right sensor
		// instance
		switch (l_ressource) {
		case TEMPERATURE_SENSOR_ID:
			return this.m_sensorList.get(0);
		case HUMIDITY_SENSOR_ID:
			return this.m_sensorList.get(1);
		case PRESSURE_SENSOR_ID:
			return this.m_sensorList.get(2);
		default:
			return null;
		}
	}

	/**
	 * Method for returning a sensor state based on the HTTP URI
	 *
	 * @param request
	 *            - HTTP request
	 *
	 * @returns the instance of the object representing the weather station
	 *          state
	 */
	private State getState(String p_httpUri) {
		// get the name of the desired ressource
		String l_ressource = getRessourceName(p_httpUri);

		if (l_ressource.equals(STATE_ID))
			return this.m_state;
		else
			return null;
	}

	// declare and define required accessor methods
	public String getWeatherStationLocation() {
		return m_weatherStationLocation;
	}

	public void setWeatherStationLocation(String p_weatherStationLocation) {
		this.m_weatherStationLocation = p_weatherStationLocation;
	}

	public State getState() {
		return m_state;
	}

	public void setState(State p_state) {
		this.m_state = p_state;
	}

	public ArrayList<Sensor> getSensorList() {
		return m_sensorList;
	}

	public void setSensorList(ArrayList<Sensor> p_sensorList) {
		this.m_sensorList = p_sensorList;
	}

	public GsonBuilder getGsonBuilder() {
		return m_gsonBuilder;
	}
}
