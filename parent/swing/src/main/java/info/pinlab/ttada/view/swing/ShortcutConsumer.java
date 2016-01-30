package info.pinlab.ttada.view.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;

/**
 * Shortcuts captured by {@link PlayerTopPanel} (except for EntryTask). <br> 
 * Child widgets can delegate their shortcut through this interface.
 * 
 * @author Gabor Pinter
 *
 */
public interface ShortcutConsumer extends KeyListener{
	/**
	 * 
	 * 
	 * @return list of shortcuts. Integers are for keycodes. <br>E.g. ENTER is {@link KeyEvent.VK_ENTER}. 
	 */
	public Set<Integer> getShortcutKeys();
}
