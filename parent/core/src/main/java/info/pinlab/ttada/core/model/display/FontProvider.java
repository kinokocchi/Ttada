package info.pinlab.ttada.core.model.display;

import java.awt.Font;

public interface FontProvider {
	public Font getFont();
	public String getFontName();
	public float getFontSize();
	public void setFontsize(float sz);
}
