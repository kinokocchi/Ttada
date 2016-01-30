package info.pinlab.ttada.view.swing;


import info.pinlab.ttada.core.control.EnrollReqListener;
import info.pinlab.ttada.core.control.SessionController;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.view.EnrollView;
import info.pinlab.ttada.core.view.EnrollViewFactory;
import info.pinlab.ttada.core.view.NavigatorView;
import info.pinlab.ttada.core.view.PlayerTopView;
import info.pinlab.ttada.core.view.TaskView;
import info.pinlab.ttada.core.view.UserInteractionView;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopPanel implements 	PlayerTopView, WindowListener, WindowFocusListener, KeyListener, 
										UserInteractionView, EnrollViewFactory{
	public static Logger LOG = LoggerFactory.getLogger(TopPanel.class);

	private final JFrame frame;
	private final Container contentPane;
	final Map<EnrollView, JFrame> enrollViewFrames = new HashMap<EnrollView, JFrame>();  

	final Map<Integer, KeyListener> shortcutListenerMapForComponents = new HashMap<Integer, KeyListener>();
	final Map<Integer, KeyListener> shortcutListenerMapForTop = new HashMap<Integer, KeyListener>();

	private final TopNavigationPanel naviPanel ;
	private final GridBagConstraints dispPanelGBC;
	
	private SessionController control = null;

	
	class MouseClickListListener extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e){
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
//				System.out.println("1 " + e);
				break;
			case MouseEvent.BUTTON2:
//				System.out.println("2 " + e);
				break;
			case MouseEvent.BUTTON3: //-- RIGHT CLICK
				PopUpMenu pop = new PopUpMenu();
				pop.show(e.getComponent(), e.getX(), e.getY());
				break;
			default:
				break;
			}
	    }
	}

	
	@SuppressWarnings("serial")
	private class PopUpMenu extends JPopupMenu implements ActionListener{
		final JMenuItem sndMenu;
		final JMenuItem fontMenu;
		final JMenuItem remoteMenu;
		
		final JDialog sndDialog; 
		
		PopUpMenu(){
			super();
			sndMenu = new JMenuItem("Sound settings");
			fontMenu = new JMenuItem("Font settings");
			remoteMenu = new JMenuItem("Remote settings");

			add(sndMenu);
			add(remoteMenu);
			add(fontMenu);

			sndMenu.addActionListener(this);
			fontMenu.addActionListener(this);
			remoteMenu.addActionListener(this);

			sndDialog = new JDialog(TopPanel.this.frame, "Sound settings", true);
			//TODO: audio
//			sndDialog.getContentPane().add(audioDevicePanel);
//			sndDialog.setSize(150,450);
		}
		@Override
		public void actionPerformed(ActionEvent e){
			
			if(sndMenu.equals(e.getSource())){
				Point cursor = MouseInfo.getPointerInfo().getLocation();
				sndDialog.setBounds(cursor.x, cursor.y, 120, 550);
				sndDialog.setVisible(true);
				return;
			}
		}
	}
	
	static public void setNimbusLF(){
		try{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					LOG.info("Look and feel is set to " + info.getName());
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			LOG.error("Failed to set look and feel to Nimbus!");
		}
	}
	
	

	
	private class InitGuiComonentsOnEDT{
		private JFrame frame_;
		private Container contentPane_;

		private TopNavigationPanel naviPanel_ ;
//		private JPanel displayPanel_;
		private GridBagConstraints dispPanelGBC_;
//		private JPanel responsePanel_;

		private InitGuiComonentsOnEDT() throws InterruptedException, InvocationTargetException{
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					frame_ = new JFrame();
					frame_.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					frame_.setAlwaysOnTop(true);
					//-- add focus and never release
					frame_.addKeyListener(TopPanel.this);
					frame_.setFocusTraversalKeysEnabled(false);
					frame_.setAutoRequestFocus(true);
					frame_.requestFocus();
					//-- populate:
					contentPane_ =  frame_.getContentPane();
					contentPane_.setLayout(new GridBagLayout());

					contentPane_.setFocusTraversalKeysEnabled(false);
					contentPane_.setFocusable(false);
					
					frame_.addWindowListener(TopPanel.this);
					frame_.addMouseListener(new MouseClickListListener());
//					frame_.addWindowFocusListener(PlayerTopPanel.this);

					naviPanel_ = new TopNavigationPanel();
					GridBagConstraints gbc = GbcFactory.getRow();
					gbc.gridx = 0;
					gbc.gridy = 0;
					contentPane_.add(naviPanel_.getPanel(), gbc);

//					displayPanel_ = new JPanel();
					dispPanelGBC_ = GbcFactory.getFillBoth();
					dispPanelGBC_.gridy = 1;
//					contentPane_.add(displayPanel_, dispPanelGBC_);

//					responsePanel_ = new JPanel();
//					responsePanelGBC_ = GbcFactory.getRow();
//					responsePanelGBC_.gridy = 2;
//					contentPane_.add(responsePanel_, responsePanelGBC_);
				}
			});
		}
	}
	
	
	
	public TopPanel(){
		//-- set nimbus on a non Swing thread 
		setNimbusLF();

		
		//-- start gui on Swing thread! 
		InitGuiComonentsOnEDT comp = null;
		try {
			comp = new InitGuiComonentsOnEDT();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		this.frame = comp.frame_;
		this.contentPane = comp.contentPane_;
		this.naviPanel = comp.naviPanel_;
	
		for(Integer shortcut: naviPanel.getShortcutKeys()){
			shortcutListenerMapForTop.put(shortcut, naviPanel);
		}
		
		this.dispPanelGBC = comp.dispPanelGBC_;
	}

	
	
	

	@Override
	public void setTaskWindowVisible(boolean b){
		frame.setVisible(b);
//		frame.setAlwaysOnTop(false);
//		frame.setSize(0, 0);
//		frame.setLocation(0, 0);
	}

	
	
	@Override
	public void setLabel(String label) {
		frame.setTitle(label);
	}
	

	@Override
	public void startGui(){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(control==null){
					LOG.warn("No control object is set for this session!");
				}
				frame.setSize(500,450);
				frame.setVisible(true);
			}
		});
	}

	
	

	/**
	 * Clear everything except for navigation
	 */
	private void clearView(){
		for(Component comp : contentPane.getComponents()){
			if(!comp.equals(naviPanel)){
				contentPane.remove(comp);
			}
		}
	}
	
	
	
	
	@Override
	public void setTaskView(final TaskView view){
		if(!(view instanceof TaskViewPanel)){
//			LOG.error("Not a TaskViewPanel in setter! '" + view.getClass() + "' can't be set for panel!");
			if(view == null){
				throw new IllegalArgumentException("NULL received as TaskViewPanel in view setter! '" );
			}else{
				throw new IllegalArgumentException("Not a TaskViewPanel in setter! '" + view.getClass() + "' can't be set for panel!");
			}
		}
		
		//-- reset shortcuts
		shortcutListenerMapForComponents.clear();
		if(view instanceof ShortcutConsumer){
			ShortcutConsumer shortcutConsumer = (ShortcutConsumer)view;
			for(Integer shortcut : shortcutConsumer.getShortcutKeys()){
				shortcutListenerMapForComponents.put(shortcut, shortcutConsumer);
			}
		}
		for(Object obj :view.getModelViewMap().values()){
			if(obj instanceof ShortcutConsumer){
				ShortcutConsumer shortcutConsumer = (ShortcutConsumer)obj;
				for(Integer key: shortcutConsumer.getShortcutKeys()){
					shortcutListenerMapForComponents.put(key, shortcutConsumer);
				}
			}
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				clearView();
				contentPane.add(((TaskViewPanel)view).getPanel(), dispPanelGBC);
				contentPane.validate();
				contentPane.repaint();
				frame.requestFocus();
			}
		});
	}
	
	
	
	/**
	 * 
	 * Dynamically initiates view  
	 */
	private static class ViewSetter implements Runnable{
		TaskView view;
		final Task task;
		Class<?> panelClass = null;
		
		ViewSetter(Task task){
			this.task = task;
			
			String panelName = this.getClass().getPackage().getName()
					+ "." 
					+task.getClass().getSimpleName() + "Panel";
			try {
				//-- dynamically create task view
				panelClass = Class.forName(panelName);
			} catch (ClassNotFoundException e) {
				String err = "No such class available as '"+ panelClass +"'";
				LOG.error(err);
				e.printStackTrace();
				throw new IllegalStateException(err);
			}
		}
		
		public void run() {
			LOG.debug("Dynamically creating view: '" + panelClass +"'");
			try {
				view = (TaskView) panelClass.newInstance();
			} catch (InstantiationException e) {
				LOG.error("Can't instantiate class '"+ panelClass +"'");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			//-- after getting view instance
			if(view!=null){
				view.setTask(task);				
			}else{
				throw new IllegalStateException("No view for task '" + task.getClass()+"'");
			}
		}
	}
	
	
	
	@Override
	public TaskView setTaskView(final Task task){
		ViewSetter vs = new ViewSetter(task);
		if(SwingUtilities.isEventDispatchThread()){
			vs.run();
		}else{
			try {
				SwingUtilities.invokeAndWait(vs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		setTaskView(vs.view);
		return vs.view;
	}

	
	@Override
	public void windowClosing(WindowEvent e){
		if(control!=null){
			new Thread(new Runnable() {
				@Override
				public void run() {
					control.reqCloseWindow();
				}
			}).start();
		}else{
			LOG.warn("No session controller is set!");
			if(showCloseConfirmDialog()){
				this.dispose();
			}
		}
	}
	
	
	@Override
	public void setResponse(Response resp) {	}
	@Override
	public void windowOpened(WindowEvent e) {	}
	@Override
	public void windowClosed(WindowEvent e) {	}
	@Override
	public void windowIconified(WindowEvent e) {	}
	@Override
	public void windowDeiconified(WindowEvent e) {	}
	@Override
	public void windowActivated(WindowEvent e) {	}
	@Override
	public void windowDeactivated(WindowEvent e) {	}

	@Override
	public void setSessionController(SessionController session) {
		if(session!=null){
			control = session;
			session.setTopView(this);
		}

		
	}


	@Override
	public NavigatorView getNaviView(){
		return naviPanel;
	}


	@Override
	public boolean showCloseConfirmDialog() {
		int quit = JOptionPane.showOptionDialog(
				frame, "Do you want stop?", "" +
						"Exiting program..", 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, new String[]{"yes", "no"}, 1); 
		if(quit==0)
			return true;
		return false;
	}


	@Override
	public void dispose(){
		shortcutListenerMapForComponents.clear();
		shortcutListenerMapForTop.clear();
		SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame.dispose();
					
					for(EnrollView view : enrollViewFrames.keySet()){
						JFrame frame = enrollViewFrames.get(view);
						frame.setVisible(false);
						frame.dispose(); 
						view.dispose();
					}
				}
			});
	}


	@Override
	public int showAudioDevSelector(List<String> deviceNames){
		//TODO: implement this!
		return -1;
	}

	
	@Override
	public void windowGainedFocus(WindowEvent arg0){
//		Container parent = frame;
//			for(Component comp : parent.getComponents()){
//				System.out.println(comp);
//			}
//		System.out.println(" GOT FOCUS ! " + SwingUtilities.isEventDispatchThread());
//		traverseComps(frame.getContentPane());
	}

	

	@Override
	public void windowLostFocus(WindowEvent arg0) {}


	@Override
	public EnrollView buildEnrollView() {
		return buildEnrollView(null /* null listener */);
	}
	@Override
	public EnrollView buildEnrollView(final EnrollReqListener enrollListener) {
		final EnrollView view = new EnrollViewPanel();
		
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				final JFrame frame = new JFrame("Progress monitor");
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				frame.setSize(400, 300);
				frame.setLocation(100, 100);
				frame.getContentPane().add(((HasPanel)view).getPanel());
				
				frame.addWindowListener(new WindowListener() {
					@Override public void windowOpened(WindowEvent arg0) {	}
					@Override public void windowIconified(WindowEvent arg0) {	}
					@Override public void windowDeiconified(WindowEvent arg0) {	}
					@Override public void windowDeactivated(WindowEvent arg0) {		}
					@Override public void windowClosed(WindowEvent arg0) {			}
					@Override public void windowActivated(WindowEvent arg0) {			}
					@Override public void windowClosing(WindowEvent arg0){
						int quit = JOptionPane.showOptionDialog(
								frame, "Do you want stop enrolling?", "" +
										"Exit enroll?", 
										JOptionPane.YES_NO_OPTION, 
										JOptionPane.QUESTION_MESSAGE, 
										null, new String[]{"yes", "no"}, 1); 
						if(quit==0){
							if(enrollListener!=null){
								enrollListener.reqExitEnroll();
							}
						}
					}
				});
				enrollViewFrames.put(view, frame);
			}
		});
		return view;
	}
	
	
	public void setEnrollViewVisible(final EnrollView view, final boolean isVisible){
		LOG.debug("Making EnrollView visible: " + isVisible);
		final JFrame frame = enrollViewFrames.get(view);
		if(frame==null){
			throw new IllegalStateException("The view must have frame!");
		}
		
		TopPanel.runOnEdt(new Runnable() {
			@Override
			public void run() {
				frame.setVisible(isVisible);
			}
		});
	}
	
	

	
	static public void runOnEdt(Runnable run){
		if(SwingUtilities.isEventDispatchThread()){
			run.run();
		}else{
			try {
				SwingUtilities.invokeAndWait(run);
			} catch (InvocationTargetException | InterruptedException ignore) {	}
		}
	}
	
	
	public void showWarning(String title, String msg){
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
	}
	public void showInfo(String title, String msg){
		JOptionPane.showMessageDialog(null, title, msg, JOptionPane.INFORMATION_MESSAGE);
	}
	public File showDirChooser(String title){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int status = chooser.showSaveDialog(frame);
		if(status != JFileChooser.APPROVE_OPTION){ //-- invalid answer 'cancel' or 'error'
			return null;
		}
		return chooser.getSelectedFile();
	}


	@Override
	public boolean isEnrollViewVisible() {
		for(JFrame frame : enrollViewFrames.values()){
			if (frame.isShowing()){
				return true;
			}
		}
		return false;
	}



	
	@Override
	public void keyPressed(KeyEvent key){
		int keyCode = key.getKeyCode() | key.getModifiersEx();
		//-- 1st round: child components
		KeyListener listener = shortcutListenerMapForComponents.get(keyCode);
		if(listener!=null){
			listener.keyPressed(key);
			//-- 2nd round: top components
		}else{		
			listener = shortcutListenerMapForTop.get(keyCode);
			if(listener!=null){
				listener.keyPressed(key);
			}else{
				//-- not delegating...
//				System.out.println("Not delegate " + key);
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent key) {
		int keyCode = key.getKeyCode() | key.getModifiersEx();
		//-- 1st round: child components
		KeyListener listener = shortcutListenerMapForComponents.get(keyCode);
		if(listener!=null){
			listener.keyReleased(key);
			//-- 2nd round: top components
		}else{		
			listener = shortcutListenerMapForTop.get(keyCode);
			if(listener!=null){
				listener.keyReleased(key);
			}else{
				//-- not delegating...
			}
		}
	}
	@Override
	public void keyTyped(KeyEvent key) {
		int keyCode = key.getKeyCode() | key.getModifiersEx();
		//-- 1st round: child components
		KeyListener listener = shortcutListenerMapForComponents.get(keyCode);
		if(listener!=null){
			listener.keyTyped(key);
			//-- 2nd round: top components
		}else{		
			listener = shortcutListenerMapForTop.get(keyCode);
			if(listener!=null){
				listener.keyTyped(key);
			}else{
				//-- not delegating...
			}
		}
	}
}



