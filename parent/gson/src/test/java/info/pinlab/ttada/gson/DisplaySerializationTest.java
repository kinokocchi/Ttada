package info.pinlab.ttada.gson;

import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.display.TextDisplayTestResource;

import org.junit.Test;

public class DisplaySerializationTest {

	@Test
	public void textDisplaySerializationTest() {
		for(TextDisplay disp : new TextDisplayTestResource().getResources()){
			SerializerUtil.serializeAndCompare(disp, TextDisplay.class);
			SerializerUtil.serializeAndCompare(disp, Display.class);
		}
	}
}


