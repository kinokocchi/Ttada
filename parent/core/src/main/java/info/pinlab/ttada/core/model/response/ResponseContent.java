package info.pinlab.ttada.core.model.response;

import info.pinlab.ttada.core.model.ExtendedResource;

public interface ResponseContent extends ExtendedResource<ResponseContent>{
	public long getTimeStamp();
	public long getResponseTime();
	public String getBrief();
}
