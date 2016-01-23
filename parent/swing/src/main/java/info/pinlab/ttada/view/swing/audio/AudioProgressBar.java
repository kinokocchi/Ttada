package info.pinlab.ttada.view.swing.audio;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


public class AudioProgressBar extends JPanel implements MouseListener{
	private static final long serialVersionUID = 2908038571940895699L;
	private Color barCol = new Color(81, 149, 224);
	private Color barCol2 = new Color(236, 245, 255);

	private Color bgCol = new Color(240,240,240);
	//	private Color bgCol2 = new Color(250,250,250);
	private Color textCol = Color.BLACK;

	private volatile long selStart = 0;
	private volatile long selEnd = 0;
	private volatile long selMax = 100;

	boolean isDisplayTime= true;
	boolean isStringPainted = true;

	private Font font = null;

	private String label = "Audio Progress Bar";

	private long refreshRateInMs = 30;

	private volatile Thread infiniteProgressAnimator = null;
	boolean isMouseEnabled = false;

	private class InfiniteProgressIndicator extends SwingWorker<Void, Void>{
		private int increment = 8;

		@Override
		protected Void doInBackground() throws Exception{
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run(){
					selStart = 0;
					selEnd = 20;
					selMax = 100;
//					repaint();
				}
			});
			final long t0 = System.currentTimeMillis();
			while(true){
				final String t = String.format("%4.1f", (System.currentTimeMillis() - t0)/1000.0d);
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run(){
						selStart += increment;
						selEnd += increment;
						if(isStringPainted && isDisplayTime)
							label = t;
						repaint();
						if(selEnd >= selMax || selStart <= 0)
							increment *= -1;
					}
				});
				Thread.sleep(refreshRateInMs);
				if(isCancelled()){
					infiniteProgressAnimator= null;
					break;
				}
			}

			return null;

		}
	}



	public void isDisplayTime(boolean b){
		isDisplayTime = b;
	}

	public AudioProgressBar(){
		setBackground(bgCol);
		setFont(font);
		addMouseListener(this);
	}


	public void startInfinitProgress(){
		//		isInfFinished(false);
		if(infiniteProgressAnimator != null)
			return;
		infiniteProgressAnimator = new Thread(new InfiniteProgressIndicator());
		infiniteProgressAnimator.start();
		//		System.out.println("ANIMATOR 1!!! " + infiniteProgressAnimator);
	}


	private void clearBar(){
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				selMax = 100;
				selStart = 0;
				selEnd = 0;
				label = "";
			}
		}, true);
//		repaint();
	}

	public void stopInfinitProgress(){
		//		System.out.println("EDT ? " + SwingUtilities.isEventDispatchThread());
		//		System.out.println("ANIMATOR STOP!!! " + infiniteProgressAnimator);
		if(infiniteProgressAnimator != null){
			infiniteProgressAnimator.interrupt();
			try {
				infiniteProgressAnimator.join(80);
				clearBar();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			infiniteProgressAnimator = null;
		}
//		repaint();
	}


	public void setMaximum(final long max){
		runOnEdt(new Runnable() {
			@Override
			public void run() {	selMax = max;}
		}, true);
//		repaint();
	}

	public long getMaximum(){
		return selMax;
	}

	public void setSelection(final long start, final long end){
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				selStart = start;
				selEnd = end;
			}
		}, true);
//		repaint();
	}


	private static void runOnEdt(Runnable run, boolean isSync){
		if(SwingUtilities.isEventDispatchThread()){
			run.run();
		}else{
			if(isSync){
				try {
					SwingUtilities.invokeAndWait(run);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				SwingUtilities.invokeLater(run);
			}
		}
	}

	/**
	 * Sets selection end.
	 * 
	 * @param cur
	 */
	public void setSelection(final long cur){
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				selEnd = cur;
			}
		}, true);
	}

	public void setCursor(final long pos){
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				selEnd = pos;
			}
		}, true);
	}

	public void isStringPainted(boolean b){
		isStringPainted = b;
	}


	public void setString(final String s){
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				label = s;
			}
		}, true);
//		repaint();
	}

	public void setTextColor(Color col){
		textCol = col;
//		repaint();
	}
	public void setBarColor(Color col){
		barCol = col;
		barCol2 = null;
//		repaint();
	}
	public void setBarColor(Color col, Color col2){
		barCol = col;
		barCol2 = col2;
//		repaint();
	}

	int margin = 2;

	@Override  
	public void paintComponent(java.awt.Graphics gc){  
		super.paintComponent(gc);

		int w = this.getWidth();
		int h = this.getHeight();

		int x0 = (int) Math.floor(w * ((double)selStart / selMax));
		int x1 = (int) Math.ceil(w * ((double)selEnd / selMax));   

		if(barCol2!= null){
			GradientPaint gp = new GradientPaint(0, 0, barCol2, 0, h, barCol); //new GradientPaint(10, Color.YELLOW, 30, Color.BLUE);
			Graphics2D g2d = (Graphics2D)gc;
			g2d.setPaint(gp);
			g2d.fill(new Rectangle(x0, 0, x1-x0, h));
		}else{
			gc.setColor(barCol);
			gc.fillRect(x0, 0, x1-x0, h);
		}

		//-- rigid lines at the end of bar --//
		gc.setColor(Color.LIGHT_GRAY);
		gc.drawLine(x1, 0, x1, h);
		gc.drawLine(x0, 0, x0, h);
		//		if(x0==0){
		//			gc.drawLine(x1-x0, 0, x1-x0, h);
		//			System.out.println(x0 +" . " + x1);
		//		}		

		//-- drawing TRICKS --//
		int inc = 15;
		for (int i = 0;  i*inc < w ; i++ ){
			int x = i*inc;
			gc.setColor(Color.LIGHT_GRAY);
			gc.drawLine(x, 0, x, 3);
			gc.drawLine(x, h-4, x, h);
		}

		//		 int margin = 3;
		//		 gc.setColor(bgCol2);
		//		 gc.fillRect(0, 0, w, margin);
		//		 gc.fillRect(0, h-margin, w, h);
		//		 g2d.drawRect(x0, 0, x1-x0, h);

//		System.out.println(">> " +label);
		if(isStringPainted){
			FontMetrics metrics = gc.getFontMetrics(getFont());
			int labW = metrics.stringWidth(label);
			gc.setColor(textCol);
			gc.drawString(label, (w-labW)/2,  h/2+metrics.getAscent()/3);
		}
	}  


	@Override
	public void mouseClicked(MouseEvent e) {
		if(!isMouseEnabled)
			return;
	}
	@Override
	public void mousePressed(MouseEvent e){
		if(!isMouseEnabled)
			return;
		int x = e.getX();
		setSelection(selMax * x / getWidth());
//		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e){
		if(!isMouseEnabled)
			return;
	}
	@Override
	public void mouseEntered(MouseEvent ignore){	}
	@Override
	public void mouseExited(MouseEvent ignore){		}




}
