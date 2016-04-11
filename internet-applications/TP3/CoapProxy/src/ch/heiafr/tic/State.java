package ch.heiafr.tic;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// class used for storing a boolean state
class State {

	// data members
	private boolean m_state;

	State(boolean p_state) {
		// initialize the data members
		this.m_state = p_state;
	}

	/**
	 * method called upon GET on url
	 * http://localhost:8080/CoapProxy/rest/weatherstation/<location>/state
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
	public boolean getState() {
		return m_state;
	}

	public void setState(boolean p_state) {
		this.m_state = p_state;
	}

	@Override
	public String toString() {
		return "" + this.m_state;
	}
}
