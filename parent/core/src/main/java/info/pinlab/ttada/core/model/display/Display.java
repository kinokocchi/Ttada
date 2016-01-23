package info.pinlab.ttada.core.model.display;

import info.pinlab.ttada.core.model.ExtendedResource;

public interface Display extends ExtendedResource<Display>{
	public int hashCode();
	public boolean equals(Object other);
	public void setBrief(String brief);
	public String getBrief();
}
