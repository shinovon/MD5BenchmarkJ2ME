import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;

import md5.MD5;

public class MD5BenchMIDlet extends MIDlet implements CommandListener, Runnable {
	private final static String rightHash = "ab5d9e7f6ee29fe76594c6c1cda2f992";
	private Gauge g1;
	private Gauge g2;
	
	public void run() {
		try {
			final int iterations = 10;
			long l = System.currentTimeMillis();
			byte[] b = getBytes(getClass().getResourceAsStream("/test"));
			System.out.println("read time: " + (System.currentTimeMillis() - l));
			final int length = b.length;
			System.out.println("length: " + length);
			String s;
			MD5 md5;
			{
				l = System.currentTimeMillis();
				md5 = new MD5(true);
				long softInitTime = l = System.currentTimeMillis()-l;
				double softHashTime = 0;
				boolean fail = false;
				for(int i = 0; i < iterations; i++) {
					l = System.currentTimeMillis();
					for(int j = 0; j < length; j += 4096) {
						md5.update(b, j, length - j >= 4096 ? 4096 : length - j);
					}
					s = md5.digestString();
					md5.reset();
					softHashTime += l = System.currentTimeMillis()-l;
					if(!s.equalsIgnoreCase(rightHash)) {
						fail = true;
						break;
					}
					g1.setLabel("Testing... (" + (i+1) + "/" + iterations + ")");
					Thread.yield();
				}
				softHashTime /= (double)iterations;
				System.out.println("soft init time: " + softInitTime);
				System.out.println("soft hash time: " + softHashTime);
				if(fail) {
					g1.setLabel("Failed");
				} else {
					double[] benchmark = getBenchmark(softInitTime, softHashTime, length);
					g1.setLabel(benchmark[0]+" (" + benchmark[1] + " Mbit/s)");
					g1.setValue((int)(benchmark[0]));
				}
			}

			try {
				Class.forName("java.security.MessageDigest");
				l = System.currentTimeMillis();
				md5 = new MD5(false);
				long jsrInitTime = l=System.currentTimeMillis()-l;
				double jsrHashTime = 0;
				boolean fail = false;
				for(int i = 0; i < iterations; i++) {
					l = System.currentTimeMillis();
					for(int j = 0; j < length; j += 4096) {
						md5.update(b, j, length - j >= 4096 ? 4096 : length - j);
					}
					s = md5.digestString();
					md5.reset();
					jsrHashTime += l = System.currentTimeMillis()-l;
					if(!s.equalsIgnoreCase(rightHash)) {
						fail = true;
						break;
					}
					Thread.yield();
					g2.setLabel("Testing... (" + (i+1) + "/" + iterations + ")");
				}
				jsrHashTime /= (double)iterations;
				System.out.println("jsr init time: " + jsrInitTime);
				System.out.println("jsr hash time: " + jsrHashTime);
				if(fail) {
					g2.setLabel("Failed");
				} else {
					double[] benchmark = getBenchmark(jsrInitTime, jsrHashTime, length);
					g2.setLabel(benchmark[0]+" (" + benchmark[1] + " Mbit/s)");
					g2.setValue((int)(benchmark[0]));
				}
			} catch (Throwable e) {
				e.printStackTrace();
				g2.setLabel("Not supported");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private double[] getBenchmark(double initTime, double hashTime, int length) {
		double res = initTime + hashTime;
		if(res == 0) {
			res = 1;
		}
		res = 1 / (res / 29272.7D);
		res = (int)(res * 1000) / 1000D;
		if(hashTime == 0) {
			hashTime = 0.5;
		}
		double speed = ((double)length / (hashTime / 1000D)) / 1024D / 1024D;
		System.out.println("speed: " + speed);
		speed *= 8;
		speed = (int)(speed * 1000) / 1000D;
		return new double[] { res, speed };
	}

	protected void destroyApp(boolean unconditional) {}

	protected void pauseApp() {}

	protected void startApp() {
		Form f = new Form("MD5 bench");
		f.addCommand(new Command("Exit", Command.EXIT, 2));
		f.setCommandListener(this);
		f.append("Software MD5:\n");
		g1 = new Gauge("Testing...", false, 1, 0);
		g1.setMaxValue(100);
		f.append(g1);
		f.append("JSR-177 MD5:\n");
		g2 = new Gauge("Testing...", false, 1, 0);
		g2.setMaxValue(100);
		f.append(g2);
		Display.getDisplay(this).setCurrent(f);
		Thread t = new Thread(this);
		t.setPriority(10);
		t.start();
	}
    
    public static byte[] getBytes(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(530308);
        final byte[] array = new byte[4096];
        int read;
        while ((read = inputStream.read(array)) > 0) {
            byteArrayOutputStream.write(array, 0, read);
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArray;
    }

	public void commandAction(Command c, Displayable d) {
		notifyDestroyed();
	}

}
