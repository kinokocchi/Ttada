package info.pinlab.ttada.view.swing;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResourceLoader{
	public static Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);
	public enum IconType {PLAY, STOP, PAUSE, REC};
	
	private static final Map<IconType, String> iconMap = new HashMap<ResourceLoader.IconType, String>();
	private static final Map<String, String> fontMap = new HashMap<String, String>();
	private static final Map<String, Icon> cache = new HashMap<String, Icon>();

	
	static {
		iconMap.put(IconType.PLAY, "media-playback-start");	
		iconMap.put(IconType.STOP, "media-playback-stop");	
		iconMap.put(IconType.PAUSE, "media-playback-pause");	
		iconMap.put(IconType.REC, "media-playback-rec");	
		
		fontMap.put("ubuntu", "Ubuntu-M.ttf");
	}

	
	public static Icon getIcon(IconType t, int size){
		String iconFileName =  iconMap.get(t)+"_"+size+"x"+size+".png";
		Icon icon = cache.get(iconFileName);
		if(icon==null){
			try {
				InputStream is = ResourceLoader.class.getResourceAsStream(iconFileName);
				if(is==null){
					is = ResourceLoader.class.getClassLoader().getResourceAsStream(iconFileName);
				}
				if (is==null){
					LOG.error("Can't load ICON  '"+ iconFileName + "'");
					return null;
				}
				LOG.debug("Loading ICON  '"+ iconFileName + "'");
				assert(is!=null);
				icon = new ImageIcon(ImageIO.read(is));
				cache.put(iconFileName, icon);
			} catch (IOException ignore) {
				ignore.printStackTrace();
				LOG.error("Can't load ICON  '"+ iconFileName + "'");
				return null;
			}
		}
		return icon;
	}
	
	
	public static Font getFont(String name, float size){
		String font = name.toLowerCase();
		if(fontMap.containsKey(font)){
			font = fontMap.get(font);
		}else{
			if (!font.endsWith(".ttf"))
				font = font + ".ttf";
		}
		
		InputStream is = ResourceLoader.class.getResourceAsStream(font);
//		InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(iconFileName);
		if(is==null){
			LOG.error("Couldn't load font '" + font + "'");
			return null;
		}else{
			LOG.debug("Load font '" + font + "'");
			try {
				Font dynFont = Font.createFont(Font.TRUETYPE_FONT,is);
				return dynFont.deriveFont(size);
			} catch (FontFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		throw new IllegalArgumentException("Can't create font '" + name + "'");
	}
	
}
