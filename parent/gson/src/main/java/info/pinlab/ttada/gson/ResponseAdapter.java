package info.pinlab.ttada.gson;

import info.pinlab.ttada.core.model.ExtendedResource;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseHeader;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ResponseAdapter implements 
JsonDeserializer<Response>,
JsonSerializer<Response>{
	
	@Override
	public JsonElement serialize(Response resp, Type type,
			JsonSerializationContext context){
		
		JsonElement elem1 = context.serialize(resp.getHeader()); 
				
		ResponseContent cont = resp.getContent();
		JsonElement elem2 = context.serialize(cont, ExtendedResource.class);
		
//		System.out.println(elem1.toString());
//		System.out.println(elem2.toString());
		
		JsonObject jobj = new JsonObject();
		jobj.add("header", elem1);
		jobj.add("content", elem2);
		
		return jobj;
	}

	@Override
	public Response deserialize(JsonElement elem, Type type,
			JsonDeserializationContext context) throws JsonParseException{
		
		JsonObject jObj = elem.getAsJsonObject();
//		System.out.println(jObj.get("content").isJsonPrimitive());
//		System.out.println(jObj.get("content").isJsonObject());
//		System.out.println(jObj.get("content").isJsonArray());
		
		JsonObject  contAsObj = jObj.getAsJsonObject("content");
//		JsonObject  contAsObj = jObj.get("content");
//		SimpleJsonAdapter.this.gson.fromJson(contAsObj.toString(), ResponseContent.class);
		
//		ResponseContent cont =  SimpleJsonAdapter.this.fromJson(ResponseContent.class, contAsObj.toString());
		
		ResponseContent cont = context.deserialize(contAsObj, ExtendedResource.class);
		jObj.remove("content");
		
		ResponseHeader header = context.deserialize(jObj.get("header"), ResponseHeader.class);
		return new Response(header, cont);
//		final String hdrJson = 
//		context.deserialize(jObj.get("header"), ResponseHeader.class);
//		
//		ResponseHeader hdr = context.deserialize(hdrJson, ResponseHeader.class);
//		return null;
	}
	
}