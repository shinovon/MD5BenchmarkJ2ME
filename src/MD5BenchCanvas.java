import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class MD5BenchCanvas extends Canvas {

	private MD5BenchMIDlet midlet;
	private int soft;
	private String softt = "Testing..";
	private int jsr;
	private String jsrt = "Testing..";

	public MD5BenchCanvas(MD5BenchMIDlet midlet) {
		this.midlet = midlet;
		try {
			setTitle("MD5 bench");
			setFullScreenMode(true);
		} catch (Throwable e) {
		}
	}

	protected void paint(Graphics g) {
		Font f = Font.getDefaultFont();
		g.setFont(f);
		int fh = f.getHeight();
		int w = getWidth();
		int h = getHeight();
		g.setColor(-1);
		g.fillRect(0, 0, w, h);
		g.setColor(0);
		g.drawString("MD5 bench", 0, 0, 0);
		g.drawString("Exit", 0, h - fh, 0);
		
		g.drawString("Software MD5: ", 0, fh+4,0);
		g.drawString(softt, 10, fh*2+8, 0);
		g.setColor(0x0000FF);
		if(soft > 100) soft = 100;
		if(soft > 0)
		g.fillRect(6, fh*3+11, (int)((w-11)*(soft/100F)), 23);
		g.setColor(0);
		g.drawRect(5, fh*3+10, w-10, 24);

		g.drawString("JSR-177 MD5: ", 0, fh*3+40,0);
		g.drawString(jsrt, 10, fh*4+44, 0);
		g.setColor(0x0000FF);
		if(jsr > 100) jsr = 100;
		if(jsr > 0)
		g.fillRect(6, fh*5+47, (int)((w-11)*(jsr/100F)), 23);
		g.setColor(0);
		g.drawRect(5, fh*5+46, w-10, 24);
	}

	public void soft(int i, String s) {
		soft = i;
		softt = s;
		repaint();
	}

	public void jsr(int i, String s) {
		jsr = i;
		jsrt = s;
		repaint();
	}
	
	public void keyPressed(int key) {
		if(key <= -5) {
			midlet.notifyDestroyed();
		}
	}

}
