package info.pinlab.ttada.view.swing;

import static org.junit.Assert.assertTrue;

import java.awt.Font;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractTaskPanelTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	
	@Test
	public void testGetIpaFont(){
		Font ipaFont = AbstractTaskPanel.getIpaFont();
		assertTrue(ipaFont!=null);
//		int sz = 12;
//		MultichoiceTask mtask = new MultichoiceTask();
//		mtask.addDisplay("IPA font testing!");
//		mtask.addDisplay(new IpaDisplay("ɪaɪɜːəɑː", (int)(sz*0.9)));
//		mtask.addChoice(new IpaDisplay("ɑː", sz));
//		mtask.addChoice(new IpaDisplay("ɜː", sz));
//		mtask.addChoice(new IpaDisplay("ə", sz));
//		
//		MultichoiceTaskPanel panel = new MultichoiceTaskPanel();
//		panel.setTask(mtask);
		
	}

}
