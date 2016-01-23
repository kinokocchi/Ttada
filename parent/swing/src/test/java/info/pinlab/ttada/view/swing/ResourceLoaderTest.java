package info.pinlab.ttada.view.swing;

import static org.junit.Assert.assertTrue;
import info.pinlab.ttada.view.swing.ResourceLoader.IconType;

import org.junit.Test;

public class ResourceLoaderTest {

	
	
	@Test
	public void iconLOadTest(){
		assertTrue(ResourceLoader.getIcon(IconType.PLAY,  48)  != null);
		assertTrue(ResourceLoader.getIcon(IconType.PAUSE, 48) != null);
		assertTrue(ResourceLoader.getIcon(IconType.STOP,  48) != null);
		assertTrue(ResourceLoader.getIcon(IconType.REC,   48) != null);
		
		assertTrue(ResourceLoader.getIcon(IconType.PLAY,  32) != null);
		assertTrue(ResourceLoader.getIcon(IconType.PAUSE, 32) != null);
		assertTrue(ResourceLoader.getIcon(IconType.STOP,  32) != null);
		assertTrue(ResourceLoader.getIcon(IconType.REC,   32) != null);
	}
	
}
