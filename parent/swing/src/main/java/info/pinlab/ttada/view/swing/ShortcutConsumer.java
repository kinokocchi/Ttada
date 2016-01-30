package info.pinlab.ttada.view.swing;

import java.awt.event.KeyListener;
import java.util.Set;

public interface ShortcutConsumer extends KeyListener{
	public Set<Integer> getShortcutKeys();
}
