package info.pinlab.ttada.cache.disk;

import java.io.File;

import info.pinlab.ttada.core.model.response.Response;

/**
 *<p> Interface for special save managers. For special tasks </p>
 *
 * <p> Contract: implementing classes must have a   public (File path) constructor!!! 
 * 
 * <br>
 * 
 * Use {@link AbstractSaveHook } for subclassing.
 * </p>
 * 
 * 
 * @author Gabor Pinter
 *
 */
public interface LocalSaveHook{

	/**
	 * To clean up at the end
	 */
	public void save(Response response);
	public LocalSaveHook relocate(File newAbsPath);
	public File getRootPath();
	
}
