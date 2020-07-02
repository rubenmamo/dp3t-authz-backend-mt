package org.dpppt.malta.backend.sdk.authz.ws.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.ws.model.CovidCodeResponseModel;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CovidCodeMapper {

	CovidCodeMapper INSTANCE = Mappers.getMapper( CovidCodeMapper.class );
	
    default String dateToString(Date date) {    	
    	return DateTimeFormatter.ISO_LOCAL_DATE.format(date.toInstant());        
    }

    default String instantToString(Instant date) {
    	if (null == date) return null;    	
    	
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault()).format(date);        
    }

    default String uuidToString(UUID uuid) {
    	
        return uuid.toString();        
    }
    
    
    CovidCodeResponseModel covidCodeToResponseModel(CovidCode covidCode);
    
}
