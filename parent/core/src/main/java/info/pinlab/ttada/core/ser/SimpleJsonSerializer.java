package info.pinlab.ttada.core.ser;



public interface SimpleJsonSerializer {
	public String toJson(Object obj);
	public String toJson(Object obj, Class<?> clazz);
	public <T> T fromJson(String json, Class<T> clazz);
}

