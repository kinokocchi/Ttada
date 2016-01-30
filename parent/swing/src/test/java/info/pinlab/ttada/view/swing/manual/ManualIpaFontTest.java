package info.pinlab.ttada.view.swing.manual;

import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.IpaDisplay;
import info.pinlab.ttada.view.swing.TopPanel;

public class ManualIpaFontTest {

	
	
	
	
	public static void main(String[] args) {
		TopPanel panel = new TopPanel();
		panel.setLabel("Minimal test");
		
		int sz = 32;
		MultichoiceTask mtask = new MultichoiceTask();
		mtask.addDisplay("IPA font testing!");
		mtask.addDisplay(new IpaDisplay("ɪaɪɜːəɑː", (int)(sz*0.9)));
		mtask.addChoice(new IpaDisplay("ɑː", sz));
		mtask.addChoice(new IpaDisplay("ɪ", sz));
		mtask.addChoice(new IpaDisplay("aɪ", sz));
		mtask.addChoice(new IpaDisplay("ɜː", sz));
		mtask.addChoice(new IpaDisplay("ə", sz));
		
		
		panel.setTaskView(mtask);
		panel.startGui();
		
	}

}
