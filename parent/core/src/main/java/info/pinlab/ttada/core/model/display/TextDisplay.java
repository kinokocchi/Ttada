package info.pinlab.ttada.core.model.display;

import info.pinlab.utils.HashCodeUtil;

import java.awt.Font;

public class TextDisplay extends AbstractDisplay {
	private final String text;
	private Font font = null;
	private int fontSz = 10;
	
	public TextDisplay(String text){
		if(text==null){
			this.text = "";
		}else{
			this.text = text;
		}
	}
	
	public String getText(){
		return text;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof TextDisplay))
			return false;

		TextDisplay other = (TextDisplay)obj;
		if(!this.text.equals(other.text))
			return false;

		if(this.font!= null){
			if(!this.font.equals(other.font))
				return false;
		}else{
			if(other.font!=null)
				return false;
		}
		return true; 
	}
	
	public void setFont(Font fontName){
		this.font = fontName;
	}
	
	public Font getFont(){
		return font;
	}
	
	public void setFontSize(int sz){
		this.fontSz = sz;
	}
	public int getFontSize(){
		return fontSz;
	}
	
	
	@Override
	public String toString(){
		return "TextDisplay@" + this.hashCode() +"_'"+ this.text + "'";
	}
	
	
	@Override
	public int hashCode(){
		if(hash!=0) //-- lazy initialization;
			return hash;
		hash = 3312;
		hash = HashCodeUtil.hash(hash, text);
		hash = HashCodeUtil.hash(hash, font);
		return hash;
	}
}
