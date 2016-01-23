package info.pinlab.ttada.view.swing;

import java.awt.GridBagConstraints;

public class GbcFactory {
	public static GridBagConstraints getFillBoth(){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		return gbc;
	}
	public static GridBagConstraints getRow(){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		return gbc;
	}
	public static GridBagConstraints getSimple(int gridx, int gridy){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		return gbc;
	}
}
