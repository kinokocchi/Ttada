package info.pinlab.ttada.core.model.display;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextDisplayTest {

	@Test
	public void test(){
		for(TextDisplay orig : new TextDisplayTestResource().getResources()){
			
			
			assertTrue(orig!=null);
			assertTrue(orig.equals(orig));
		}
	}

	
}
