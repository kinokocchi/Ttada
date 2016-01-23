package info.pinlab.ttada.core.model.display;

import info.pinlab.utils.HashCodeUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class TextInputDisplay extends AbstractDisplay implements Iterable<TextDisplay>{
	private final List<TextDisplay> itemLabels;
	
	public TextInputDisplay(List<TextDisplay> items){
		itemLabels = new ArrayList<TextDisplay>();
		for(TextDisplay item: items){
			itemLabels.add(item);
		}
	}
	
	public TextInputDisplay(TextDisplay item){
		itemLabels = new ArrayList<TextDisplay>();
		itemLabels.add(item);
	}

	
	
	
	
	@Override
	public int hashCode(){
		if(hash!=0) //-- lazy initialization;
			return hash;
		hash = 2719;
		
		for(TextDisplay disp : itemLabels){
			hash = HashCodeUtil.hash(hash, disp.hashCode());
		}
		return hash;
	}

	@Override
	public Iterator<TextDisplay> iterator() {
		return itemLabels.iterator();
	}
	
}
