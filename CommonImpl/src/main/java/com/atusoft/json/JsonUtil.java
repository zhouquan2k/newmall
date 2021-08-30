package com.atusoft.json;


import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;


@Component
public class JsonUtil implements  com.atusoft.util.JsonUtil 
{
	public final static String LongTimeFormat="yyyy.MM.dd HH:mm:ss";
	
	public ObjectMapper mapper;
	
	public JsonUtil(){
		this.init();
	}
	
	public void init()
	{
		//f.enable(JsonParser.Feature.ALLOW_COMMENTS);
		//f.disable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
		
		
	
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		JsonFactory factory = mapper.getFactory();
		factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		//factory.enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
        //mapper.registerModule(new ObjectIdSerializerModule());
        
        
        // fastxml
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        
        
        mapper.setDateFormat(new SimpleDateFormat(LongTimeFormat));
        mapper.setVisibility(
        	    mapper.getSerializationConfig().
        	    getDefaultVisibilityChecker().
        	    withFieldVisibility(Visibility.ANY).
        	    withGetterVisibility(Visibility.NONE).
        	    withIsGetterVisibility(Visibility.NONE)
        	);
        

	}
	
	public String toJson(Object src)
	{
		try
		{
			return mapper.writeValueAsString(src);
		}
		catch (Throwable e)
		{
			throw new RuntimeException("jackson.toJson",e);
		}
	}
	
	public <T> T fromJson(String src,Class<T> cls)
	{
		try
		{
			if (src==null) return null;
			return this.mapper.readValue(src,cls);
		}
		catch (Throwable e)
		{
			throw new RuntimeException("jackson.fromJson",e);
		}
	}
	
	public Object fromJson(String src,String className)
	{
		try
		{
			if (className==null) 
			{
				return  mapper.readValue(src, Map.class);
			}
			return mapper.readValue(src,Class.forName(className));
		}
		catch (Throwable e)
		{
			throw new RuntimeException("jackson.fromJson",e);
		}
	}

	@Override
	public Object getObjectMapper() {
		return this.mapper;
	}
}


