import java.awt.event.*;
import javax.swing.*;

/**
 * This class implements a LightDisplay that holds a reference
 * to a particular LightPanel, contains the UI elements used to 
 * show the light's status and turn it on/off, and extends the Thread
 * class for multithreading capabilities
 *
 * @author: Professor Norman
 */
public class LightDisplay extends Thread implements ActionListener {
	private LightPanel panel;
	private ImageIcon lightOffIcon;
	private ImageIcon lightOnIcon;
	private JLabel lightLabel;

	/**
	 * This creates a LightDisplay referencing a LightPanel, 
	 * adds the necessary UI elements (buttons, picture, etc.),
	 * and calls the run() method once the LightDisplay is constructed
	 *
	 * @param panel the LightPanel this LightDisplay references
	 */
	public LightDisplay(LightPanel panel) {
		this.panel = panel;

		JFrame frame = new JFrame();
		frame.setTitle(panel.toString());

		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.LINE_AXIS));

		lightOffIcon = new ImageIcon("lightoff.gif");
		lightOnIcon = new ImageIcon("lighton.gif");

		lightLabel = new JLabel(lightOffIcon);
		lightLabel.setBorder(BorderFactory.createEtchedBorder());
		frame.getContentPane().add(lightLabel);

		JButton onButton = new JButton("ON");
		onButton.setMnemonic(KeyEvent.VK_N);
		onButton.setActionCommand("on");
		onButton.addActionListener(this);
		frame.getContentPane().add(onButton);

		JButton offButton = new JButton("OFF");
		offButton.setMnemonic(KeyEvent.VK_F);
		offButton.setActionCommand("off");
		offButton.addActionListener(this);
		frame.getContentPane().add(offButton);

		frame.pack();
		frame.setVisible(true);

		start();
	}

	/**
	 * This is an event handler for when the switch is flipped
	 *
	 * @param e the event that takes place (i.e. if the switch was flipped
	 *          on or off)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("on"))
			panel.switchOn();
		else
			panel.switchOff();
	}

	/**
	 * This ensures that the LightDisplay window is showing the correct
	 * image based on its state (on/off)
	 */
	public void run() {
		while (true) {
			if (panel.isOn())
				lightLabel.setIcon(lightOnIcon);
			else
				lightLabel.setIcon(lightOffIcon);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}
}