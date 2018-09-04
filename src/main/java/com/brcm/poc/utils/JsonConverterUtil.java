package com.brcm.poc.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverterUtil {

	
	private volatile static ObjectMapper objectMapper = new ObjectMapper();
	static {
		// DO NOT INCLUDE NULLS in response.
		objectMapper = objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//df.setTimeZone(TimeZone.getTimeZone("PST"));//calfornia
		objectMapper.setDateFormat(df);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}


	/**
	 * Converts JSON to corresponding java object.
	 * @param json  The JSON payload.
	 * @param T The generic input object 
	 * @return The generic input object
	 */
	public static <T> T readFromString(final String json, final Class<T> T) {
		T object = null;
		try {
			object = objectMapper.readValue(json, T);
		} catch (JsonParseException e) {
			//LOGGER.error("JsonParseException occured in readFromString()", e);
		} catch (JsonMappingException e) {
			//LOGGER.error("JsonMappingException occured in readFromString()", e);
		} catch (IOException e) {
			//LOGGER.error("IOException occured in readFromString()", e);
		}
		return object;
	}

	/**
	 * Converts Json Array of Objects  to Java List Objects
	 * @param json
	 * @param typeRef
	 * @return
	 */
	public static <T> List<T> readFromStringArray(final String json, final TypeReference<List<T>> typeRef) {
		List<T> object = null;
		try {
			object = objectMapper.readValue(json, typeRef);
		} catch (JsonParseException e) {
		//	LOGGER.error("JsonParseException occured in readFromString()", e);
		} catch (JsonMappingException e) {
			//LOGGER.error("JsonMappingException occured in readFromString()", e);
		} catch (IOException e) {
			//LOGGER.error("IOException occured in readFromString()", e);
		}
		return object;
	}	
	/**
	 * Converts Java object to JSON payload.
	 * @param object The java object to be converted.
	 * @param prettyPrint Indicates if the JSON format is required.
	 * @return The JSON payload
	 */
	public static String writeToString(final Object object, final boolean prettyPrint){
		String outstr = "";
		try {
			if (prettyPrint) {
				outstr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
			} else {
				outstr = objectMapper.writeValueAsString(object);
			}
		}
		catch (Exception e) {
			//LOGGER.error("Exception occured in writeToString()", e);
		}
		return outstr;
	}

	/**
	 * Converts Java object to JSON payload.
	 * @param object The java object to be converted.
	 * @return The JSON payload
	 */

	public static String writeToString(final Object object){
		return writeToString(object, false);
	}
	
}
