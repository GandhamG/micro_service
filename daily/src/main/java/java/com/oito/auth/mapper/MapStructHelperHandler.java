package com.oito.auth.mapper;

import java.util.Map;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.common.json.JsonHandler;

@Component
public class MapStructHelperHandler {

	@Autowired
	private JsonHandler jsonHandler;

	@Named("ObjectToJsonStr")
	public String toJSON(final Map<String, Object> obj) {
		return jsonHandler.toJSON(obj);
	}

	@Named("JsonStrToObject")
	public Map<String, Object> fromJSON(final String json) {
		return jsonHandler.fromJSON(json, Map.class);
	}

}
