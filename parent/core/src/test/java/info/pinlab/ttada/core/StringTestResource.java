package info.pinlab.ttada.core;

import java.util.ArrayList;
import java.util.List;

public class StringTestResource extends AbstractTestResourceProvider<String>{
	private static List<String> labels = new ArrayList<String>();
	static{
		labels.add("word");
		labels.add("two words");
		labels.add("i can try to write a whole sentence here ");
		labels.add("日本語");
		labels.add("이것이 내 샌드위치 아니다");
		labels.add("<html><h1>HTML test</h1></html>");
		labels.add("");
		labels.add("  spaces and	tabs	 ");
		labels.add("!#$%&'()=~|`{}[]/*");
		labels.add("mixed 日本語 and 아니다");
	}
	
	
	public StringTestResource(){
		super(String.class);
		for(String label : labels){
			super.addResource(label);
		}
	}
}
