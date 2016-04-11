package ch.heiafr.tic;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// class representing each sensor available on the weather station
class Sensor {
	// data members
	private String m_id;
	private String m_unit;
	private String m_value;

	public Sensor(String p_sensorId, String p_unit, String p_value) {
		// initialize the data members
		this.m_id = p_sensorId;
		this.m_unit = p_unit;
		this.m_value = p_value;
	}

	/**
	 * method called upon GET on url
	 * http://localhost:8080/CoapProxy/rest/weatherstation/<location>/<sensor>
	 *
	 * @param request
	 *            - HTTP request
	 * @param response
	 *            - HTTP response
	 */
	protected void doGet(GsonBuilder p_gsonBuilder, HttpServletRequest p_httpRequest,
			HttpServletResponse p_httpResponse) throws IOException {
		// implement serialization using gsonBuilder
		Gson l_gson = p_gsonBuilder.create();
		String l_json = l_gson.toJson(this);
		p_httpResponse.getWriter().println(l_json);
	}

	// declare and define required accessor methods
	public String getId() {
		return m_id;
	}

	public void setId(String p_id) {
		this.m_id = p_id;
	}

	public String getUnit() {
		return m_unit;
	}

	public void setUnit(String p_unit) {
		this.m_unit = p_unit;
	}

	public String getValue() {
		return m_value;
	}

	public void setValue(String p_value) {
		this.m_value = p_value;
	}
}