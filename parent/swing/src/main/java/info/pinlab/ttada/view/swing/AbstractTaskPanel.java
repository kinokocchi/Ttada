package info.pinlab.ttada.view.swing;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.ttada.core.control.AudioPlayController;
import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.model.ExtendedResource;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.FontProvider;
import info.pinlab.ttada.core.model.display.InstructionTextDisplay;
import info.pinlab.ttada.core.model.display.IpaDisplay;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.display.TextInputDisplay;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.view.PlayerTopView;
import info.pinlab.ttada.view.swing.audio.AudioPlayerButton;

/**
 * 
 *  __________________
 * |                  |
 * |     Display      |
 * |                  |
 * |~~~~~~~~~~~~~~~~~~|
 * |                  |
 * |     Response     |
 * |__________________|
 * 
 * 
 * 
 * @author Gabor Pinter
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractTaskPanel extends JPanel 
			implements TaskViewPanel, KeyListener{
	public static Logger LOG = LoggerFactory.getLogger(AbstractTaskPanel.class);
	
	private static String ipaFontFileName = "DoulosSILCompact-R.ttf";
	private static Font ipaFont = null;
	
	private GridBagConstraints gbcTop;
	private GridBagConstraints gbcBottom ;
	TaskController taskController = null;
	private boolean isUnseen = true;
	long displayT = -1;
	AudioPlayerView audioBtn = null;
	AudioPlayController audioPlayerController = null;
	
	PlayerTopView topView = null;
	
	
	//-- to request focus when visible
	JComponent defaultFocusComp = null;

	
	Map<ExtendedResource<?>, Object> modelViewMap = new HashMap<ExtendedResource<?>, Object>();
	
	public AbstractTaskPanel(){
		setLayout(new GridBagLayout());
		gbcTop = GbcFactory.getFillBoth();
		gbcTop.gridy = 0;
		gbcBottom = GbcFactory.getRow();
		gbcBottom.ipady = 0;
		gbcBottom.gridy = 1;
//		addComponentListener(this);
		
		setFocusTraversalKeysEnabled(false);
		//-- keep focus on top component
		setFocusable(false);
	}
	
	public void setTopPanel(Component panel){
		this.add(panel, gbcTop);
	}
	
	
	
	public JFrame getTopFrame(Container comp){
		if(comp==null){
			return null;
		}
		if(comp instanceof JFrame){
			return (JFrame)comp;
		}else{
			return getTopFrame(comp.getParent());
		}
	}
		

	public void setBottomPanel(Component panel, GridBagConstraints gbc){
		this.add(panel, gbc);
	}
	
	public void setBottomPanel(Component panel){
		setBottomPanel(panel, gbcBottom);
	}
	
	@Override
	public JPanel getPanel() {
		return this;
	}
	
	
	static public Font getIpaFont(){
		if(ipaFont==null){
			LOG.info("Loading IPA font");
			InputStream is = AbstractTaskPanel.class.getResourceAsStream(ipaFontFileName);
			if(is!=null){
				try {
					ipaFont = Font.createFont(Font.TRUETYPE_FONT, is);
				} catch (FontFormatException e) {
					LOG.error("Can't load font '" + ipaFontFileName +"': " + e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					LOG.error("Can't find font '" + ipaFontFileName +"': " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return ipaFont;
	}
	
	
	
	@Override
	public void setTaskController(TaskController controller){
		taskController = controller;
		
		//-- setup audio play controller, if any
//		if(audioBtn !=null){
//			if(controller instanceof AudioPlayerListener){
//				audioBtn.setPlayActionListener((AudioPlayerListener)controller);
//			}
//			if( controller instanceof AudioPlayController){
//				((AudioPlayController)controller).setAudioPlayerView(audioBtn);
//			}
//		}
	}
	
	/**
	 * Setting the top-panel of the window for displays
	 */
	@Override
	public void setTask(Task task){
		modelViewMap.put(task, this);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		int gridy = 0;
		
		boolean hasInstruction = false;
		for(Display disp : task.getDisplays()){
			if(disp instanceof InstructionTextDisplay){
				InstructionTextDisplay topText = (InstructionTextDisplay)disp;
				JLabel label = new JLabel(topText.getText());
//				label.setBackground(Color.yellow);
				GridBagConstraints gbc = GbcFactory.getFillBoth();
				
				label.setOpaque(true);
				label.setHorizontalAlignment(SwingConstants.LEFT);
				label.setVerticalAlignment(SwingConstants.TOP);
				
				gbc.gridy = gridy++;
				gbc.weighty = 1.0;
				panel.add(label, gbc);
				modelViewMap.put(disp, label);
				hasInstruction  = true;
			}
		}
		
		
		for(Display disp : task.getDisplays()){
			GridBagConstraints gbc = GbcFactory.getFillBoth();
			if(disp instanceof TextDisplay){
				if(disp instanceof InstructionTextDisplay){
					//-- already covered above!
					//TODO: this may hide instructions if x > 2
					continue;
				}
				JLabel label = new JLabel(((TextDisplay)disp).getText());
				
				if(disp instanceof IpaDisplay){
					Font ipa = getIpaFont();
					if(ipa!=null){
						ipa = ipa.deriveFont((float)((IpaDisplay)disp).getFontSize() );
						label.setFont(ipa);
					}else{
					}
				}

				
				if(disp instanceof FontProvider){
					Font customFont = ((FontProvider)disp).getFont();
					String fontName = ((FontProvider)disp).getFontName();
					if(customFont == null){
						LOG.error("Can't set font '" + fontName  + "'");
					}else{
						LOG.debug("Setting font '" + fontName  + "'");
						customFont = customFont.deriveFont(((FontProvider)disp).getFontSize());
						label.setFont(customFont);
					}
				}
				label.setOpaque(true);
//				label.setBackground(Color.yellow);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				gbc.gridy = gridy++;
				gbc.weighty = 0.0;
				panel.add(label, gbc);
				modelViewMap.put(disp, label);
			}else if(disp instanceof AudioDisplay){
				gbc.weighty = 1.0;
				JPanel audioBtnContainerPanel = new JPanel();
				audioBtnContainerPanel.setLayout(new GridBagLayout());
				GridBagConstraints gbc2 = new GridBagConstraints();
				audioBtn = new AudioPlayerButton();
				audioBtnContainerPanel.add((AudioPlayerButton)audioBtn, gbc2);
				gbc.gridy = gridy++;
				panel.add(audioBtnContainerPanel, gbc);
				
				modelViewMap.put(disp, audioBtn);
				//-- setup bindings for the button:
				//TODO: cleanup: this should be unecessary, but check!
				if(taskController!=null){
//					setTaskController(taskController);
				}
			}else if(disp instanceof TextInputDisplay){
				TextInputDisplay txtInput = (TextInputDisplay)disp;
				
				JPanel itemPanel =  new JPanel();
				itemPanel.setLayout(new GridBagLayout());
				GridBagConstraints gbcForItem = new GridBagConstraints();
				GridBagConstraints gbcForField = new GridBagConstraints();
				gbcForItem.gridy  = -1;
				gbcForField.gridy = -1;
				gbcForItem.gridx = 0;
				gbcForField.gridx = 1;
				
				gbcForItem.anchor = GridBagConstraints.LINE_START;
				gbcForItem.insets = new Insets(0, 10, 0, 10);
				gbcForItem.fill = GridBagConstraints.NONE;
				gbcForItem.weighty = 0.0;
				
				gbcForField.fill = GridBagConstraints.HORIZONTAL;
				gbcForField.weightx = 1.0;
				gbcForField.weighty = 0.0;
				gbcForField.ipadx = 10;
				gbcForField.insets = new Insets(0, 10, 0, 10);
				
				
				for(TextDisplay txtItem : txtInput){
					gbcForItem.gridy++;
					gbcForField.gridy++;
					
					itemPanel.add(new JLabel(txtItem.getText()), gbcForItem);
					itemPanel.add(new JTextField(), gbcForField);
				}
				gbcForItem.gridy++;
				gbcForItem.gridwidth = 2;
				gbcForItem.weighty = 1.0;
				gbcForItem.fill = GridBagConstraints.BOTH;
				JLabel filler = new JLabel("");
				itemPanel.add(filler , gbcForItem);
				
				gbc.gridy = gridy++;
				panel.add(itemPanel, gbc);
				modelViewMap.put(disp, itemPanel);
				
			}else{
				throw new IllegalArgumentException("No gui for display type " + disp.getClass());
			}
			
			
		}
		
		if(hasInstruction){ //--add bottom padding as well
			JLabel label = new JLabel(" ");
//			label.setBackground(Color.yellow);
			GridBagConstraints gbc = GbcFactory.getFillBoth();
			
			label.setOpaque(true);
			
			gbc.gridy = gridy++;
			gbc.weighty = 1.0;
			panel.add(label, gbc);
		}
		
		setTopPanel(panel);
	}

	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(displayT<0){
        	setEnabled(true);
        	displayT = System.currentTimeMillis();
        }

        if(isUnseen && taskController!=null){
        	taskController.onViewVisible();
        	isUnseen=false;
        }

        //-- setting correct focus! --//
        if(defaultFocusComp!=null){
        	defaultFocusComp.requestFocus();
        }
	}
	
	@Override
	public void setDefaultFocus(JComponent comp) {
		defaultFocusComp = comp;
	}
	
	public Map<ExtendedResource<?>, Object> getModelViewMap(){
		return modelViewMap;
	}
	
	
	@Override
	public void setTop(PlayerTopView topView) {
		this.topView = topView;
	}
	
	
	//-- the following shortcuts can be overriden in extended components
	@Override
	public void keyReleased(KeyEvent ignore) {
	}
	@Override
	public void keyTyped(KeyEvent  ignore) {
	}

}





