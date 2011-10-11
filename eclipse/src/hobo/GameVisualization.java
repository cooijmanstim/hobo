package hobo;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameVisualization extends JPanel implements GameObserver {
	public static final int SCALE = 128;
	
	private State lastState = null;
	
	private final Object lastDecisionLock = new Object();
	private Decision lastDecision = null;
	
	public GameVisualization() {
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				synchronized (lastDecisionLock) {
					lastDecision = new Decision(e.getX() / SCALE, e.getY() / SCALE);
					lastDecisionLock.notify();
				}
			}
		});
	}
	
	public PlayerInteraction getUserInterface() {
		return new PlayerInteraction() {
			@Override public Decision askDecision(Player p, State s) {
				try {
					Decision d = null;
					while (d == null) {
							synchronized (lastDecisionLock) {
								lastDecisionLock.wait();
								d = lastDecision;
								lastDecision = null;
							}
					}
					return d;
				} catch (InterruptedException e) {
					return null;
				}
			}

			@Override public void tellIllegal (Player p, State s, Decision d, String reason) { message("illegal move"); }
			@Override public void tellDraw    (Player p, State s) { message("draw game"   ); }
			@Override public void tellLoss    (Player p, State s) { message(p+", you lose"); }
			@Override public void tellWin     (Player p, State s) { message(p+", you win" ); }
			
			@Override public void observe(Event e) {}
		};
	}

	public void visualize(Game g) {
		g.registerObserver(this);
	}
	
	public void observe(Event e) {
		lastState = e.state;
		repaint();
	}
	
	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		this.setPreferredSize(new Dimension(lastState.size * SCALE, lastState.size * SCALE));

		for (int i = 1; i < lastState.size; i++) {
			g.drawLine(i * SCALE, 0, i * SCALE, lastState.size * SCALE);
			g.drawLine(0, i * SCALE, lastState.size * SCALE, i * SCALE);
		}

		for (int x = 0; x < lastState.size; x++) {
			for (int y = 0; y < lastState.size; y++) {
				String s = lastState.symbolAt(x, y);
				if (s == null) continue;
				g.setFont(g.getFont().deriveFont(72.0f));
				g.drawString(s, x * SCALE, (y+1) * SCALE);
			}
		}
	}
	
	public void message(String s) {
		System.out.println(s);
	}
}
