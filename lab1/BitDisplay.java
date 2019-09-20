import java.awt.event.*;
import javax.swing.*;

/**
 * This class implements a BitDisplay that acts as a UI
 * for the BitHandler and allows users to input a series of 1s
 * and 0s to be broadcast to all LightPanels in the LightSystem
 * and also can receive broadcasted messages and display them
 *
 * @author: Professor Norman
 */
public class BitDisplay implements ActionListener, BitListener {
	private BitHandler handler;
	private JTextField receiveField;
	private JTextField sendField;

	public BitDisplay(BitHandler handler) {
		this.handler = handler;

		JFrame frame = new JFrame(handler.toString());
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

		receiveField = new JTextField(20);
		receiveField.setEditable(false);
		frame.getContentPane().add(receiveField);

		sendField = new JTextField(20);
		sendField.addActionListener(this);
		frame.getContentPane().add(sendField);

		frame.pack();
		frame.setVisible(true);
		handler.setListener(this);
	}

	/**
	 * Turn the light system on (if it isn't already), then wait half a period. Then
	 * turn the light off, for half a period.
	 */
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			public void run() {
				handler.broadcast(sendField.getText());
				// System.out.println("actionPerformed: done sending " + sendField.getText());
			}
		}.start();
		sendField.selectAll();
	}

	/**
	 * Called by the BitHandler when it receives bits
	 * and used to show the string of bits it has received
	 *
	 * @param h the BitHandler this BitDisplay is associated with
	 * @param bits the string of 1s and 0s that it received
	 */
	public void bitsReceived(BitHandler h, String bits) {
		receiveField.setText(bits);
	}
}