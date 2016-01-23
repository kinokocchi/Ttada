package info.pinlab.ttada.cache.disk;

import java.util.List;

import info.pinlab.ttada.core.control.EnrollController;

public interface LocalEnrollController extends EnrollController {

	public void addSaveHook(LocalSaveHook manager);
	public List<LocalSaveHook> getSaveHooks();
}
