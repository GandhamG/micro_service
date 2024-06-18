package com.oito.auth.json;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.oito.auth.common.to.UserTypeTO;

public class UserTypeTOSerializer extends JsonSerializer<Set<UserTypeTO>> {
	@Override
	public void serialize(final Set<UserTypeTO> userTOSet, final JsonGenerator arg1, final SerializerProvider arg2)
			throws IOException {
		arg1.writeObject(userTOSet.stream().filter(UserTypeTO::isEnabled).map(type -> type.getType().toString())
				.collect(Collectors.toSet()));
	}
}