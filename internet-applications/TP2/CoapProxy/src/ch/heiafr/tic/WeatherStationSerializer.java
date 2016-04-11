package ch.heiafr.tic;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

class WeatherStationSerializer implements JsonSerializer<WeatherStation> {
	@Override
	public JsonElement serialize(final WeatherStation p_weatherStation, final Type p_typeOfSrc,
			final JsonSerializationContext p_context) {
		// serialize the WeatherStation instance by adding the
		// corresponding properties and data
		// to the jsonObject and by querying the the weather station about its
		// state and about its sensors

		// THX to http://www.javacreed.com/gson-serialiser-example/

		JsonObject l_jsonObject = new JsonObject();

		//l_jsonObject.addProperty("m_state", p_weatherStation.getState().toString());
		l_jsonObject.addProperty("m_state", p_weatherStation.getState().getState());

		JsonElement l_jsonSensors = p_context.serialize(p_weatherStation.getSensorList());
		l_jsonObject.add("m_sensorList", l_jsonSensors);
		
		return l_jsonObject;
	}
}
