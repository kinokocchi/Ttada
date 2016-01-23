package info.pinlab.ttada.core.model.display;

import info.pinlab.ttada.core.AbstractTestResourceProvider;
import info.pinlab.ttada.core.StringTestResource;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class TextDisplayTestResource extends AbstractTestResourceProvider<TextDisplay>{
//	static List<TextDisplay> displays;
	
	public TextDisplayTestResource() {
		super(TextDisplay.class);
		for(String label : new StringTestResource().getResources()){
			super.addResource(new TextDisplay(label));
		}
		
		GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
		int i = 0;
		int NO_MORE_FONTS_THAN = 20;
		NO_MORE_FONTS_THAN--;
		for (Font font : graphics.getAllFonts()){
			i++;
			TextDisplay txt = new TextDisplay(font.getFontName());
			txt.setFont(font);
			
			super.addResource(txt);
			if(i>NO_MORE_FONTS_THAN){
				break;
			}
		}
		
	}

}

