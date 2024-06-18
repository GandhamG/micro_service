package com.oito.auth.jpa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

import com.oito.auth.common.Constants;
import com.oito.auth.common.enumeration.CommunicationChannel;
import com.oito.auth.util.UserUtils;

public class CommunicationChannelConverter implements AttributeConverter<Set<CommunicationChannel>, String> {

	@Override
	public String convertToDatabaseColumn(final Set<CommunicationChannel> channelSet) {
		return UserUtils.joinCommunicationChannel(channelSet);
	}

	@Override
	public Set<CommunicationChannel> convertToEntityAttribute(final String dbData) {
		if (StringUtils.isBlank(dbData)) {
			// Return HashSet instead of CollectonUtils.emptySet() as there is
			// UnsupportedOperationException during update
			return new HashSet<>(0);
		}
		return Arrays.stream(StringUtils.split(dbData, Constants.SERPARATOR_COMMA)).map(CommunicationChannel::valueOf)
				.collect(Collectors.toSet());
	}

}
