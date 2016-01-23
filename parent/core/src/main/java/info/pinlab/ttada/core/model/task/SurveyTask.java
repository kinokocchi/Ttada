package info.pinlab.ttada.core.model.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.TextDisplay;

/**
 * Survey like input (e.g., input field, radio button, etc)
 * 
 * @author Gabor Pinter
 *
 */
public class SurveyTask extends AbstractTask implements Iterable<SurveyTaskEntry>{
	private final List<SurveyTaskEntry> entries;
	
	
	public static class AbstractEntry implements SurveyTaskEntry{
		final CachedValue<Display> label;
		final boolean isObligatory;
		final String brief;
		
		public AbstractEntry(TextDisplay label){
			this(label, false, null);
		}
		public AbstractEntry(TextDisplay label, boolean isObligatory, String brief){
			this.label = CachedValue.getCacheWrapperForValue(label, Display.class);
			this.isObligatory = isObligatory;
			this.brief = brief;
		}
		
		public String getLabel(){
			return ((TextDisplay)label.getValue()).getText(); 
		}
		
		public boolean isObligatory(){
			return isObligatory;
		}
		
		public String getBrief(){
			if(brief == null){
				return "";
			}
			return brief;
		}
	}
	
	
	public static class DropDownMenuEntry extends AbstractEntry implements Iterable<TextDisplay>{
		private final List<TextDisplay> listItems;
		
		public DropDownMenuEntry(TextDisplay label, boolean isObligatory){
			super(label, isObligatory, "dropbdown");
			listItems = new ArrayList<TextDisplay>();
		}
		
		public void addItem(String text){
			listItems.add(new TextDisplay(text));
		}
		public void addItem(TextDisplay text){
			listItems.add(text);
		}

		@Override
		public Iterator<TextDisplay> iterator() {
			return listItems.iterator();
		}
	}
	
	
	
	public static class TextEntry extends AbstractEntry{
		public TextEntry(TextDisplay label, boolean isObligatory, String brief){
			super(label, isObligatory, brief);
		}
	}
	
	public static class UserIdEntry extends TextEntry{
		public UserIdEntry(TextDisplay label){
			super(label, true /* always obligatory */, "usrId");
		}
	}

	
	public static class SeparatorEntry extends AbstractEntry{
		public SeparatorEntry(String txt){
			super(new TextDisplay(txt));
		}
	}

	public static class CheckBoxEntry extends AbstractEntry{
		public CheckBoxEntry(TextDisplay txt, String brief){
			super(txt, false, brief);
		}
		public CheckBoxEntry(TextDisplay txt, boolean isObligatory, String brief){
			super(txt, isObligatory, brief);
		}
	}
	
	
	public SurveyTask (){
		super.isResponsible = true;
		entries = new ArrayList<SurveyTaskEntry>();
	}
	
	public SurveyTask addTextEntry(String label, boolean isObligatory, String brief){
		TextDisplay disp = new TextDisplay(label);
		return addEntry(new TextEntry(disp, isObligatory, brief));
	}

	public SurveyTask addCheckBox(String label, boolean isObligatory, String brief){
		TextDisplay disp = new TextDisplay(label);
		return addEntry(new CheckBoxEntry(disp, isObligatory, brief));
	}
	
	public SurveyTask addUsrIdEntry(String label){
		TextDisplay disp = new TextDisplay(label);
		return addEntry(new UserIdEntry(disp));
	}
	
	
	public SurveyTask addEntry(SurveyTaskEntry entry){
		if(entry!=null){
			entries.add(entry);
		}
		return this;
	}

	@Override
	public Iterator<SurveyTaskEntry> iterator() {
		return entries.iterator();
	}
	
	public List<SurveyTaskEntry> getEntries(){
		return new ArrayList<SurveyTaskEntry>(entries);
	}
	
	
}

