import java.lang.Character.*;

/**
 * This class implements a BitHandler that receives a string of
 * 1s and 0s from a BitListener and sends that information along to
 * the LightPanel it is associated with which in turn broadcasts it
 * to the rest of the LightPanels connected to the LightSystem
 *
 * @author: Professor Norman
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class BitHandler extends Thread {
	public static final int HALFPERIOD = 5;

	private static final String SILENCE = "SILENCE";
	private static final String EXPECT_ZERO = "EXPECT_ZERO";
	private static final String EXPECT_ONE = "EXPECT_ONE";
	private static final String HALF_ZERO = "HALF_ZERO";
	private static final String HALF_ONE = "HALF_ONE";
	private static final String GARBAGE = "GARBAGE";

	private LightPanel panel;
	private BitListener listener;
	private String state = SILENCE;

	/**
	 * Default constructor that creates a BitHandler using
	 * localhost and the default port
	 */
	public BitHandler() {
		this("localhost", LightSystem.DEFAULT_PORT);
	}

	/**
	 * Explicit constructor that creates a LightPanel using
	 * a given host name and port, then starts running the BitHandler
	 *
	 * @param host host the LightSystem is running on that the
	 *             LightPanel connects to
	 * @param port port the LightSystem is running on that the
	 *             LightPanel connects to
	 */
	public BitHandler(String host, int port) {
		panel = new LightPanel(host, port);
		start();
	}

	/**
	 * Wait a given amount of time; used to distinguish between
	 * bits that are sent and received
	 *
	 * @param millisenconds amount of time to pause the thread
	 */
	public static void pause(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// should never get here, but
			// let's print something just in case
			e.printStackTrace();
		}
	}

	/**
	 * Turn the light system on (if it isn't already), then wait half a period. Then
	 * turn the light off, for half a period.
	 * 
	 * @throws CollisionException when a collision occurs
	 */
	public void broadcastZero() throws CollisionException {
		// System.out.println(getID() + " Broadcasting: 0");
			if (!panel.isOn()) {
				panel.switchOn();
			}
			pause(HALFPERIOD);
			if (!panel.isOn()) {
				throw new CollisionException();
			}
			panel.switchOff();
			pause(HALFPERIOD);
			if (panel.isOn()) {
				throw new CollisionException();
			}
	}

	/**
	 * Turn the light system off (if it isn't already), then wait half a period.
	 * Then turn the light on, for half a period.
	 * 
	 * @throws CollisionException when a collision occurs
	 */
	public void broadcastOne() throws CollisionException {
		// System.out.println(getID() + " Broadcasting: 1");
			if (panel.isOn()) {
				panel.switchOff();
			}
			pause(HALFPERIOD);
			if (panel.isOn()) {
				throw new CollisionException();
			}
			panel.switchOn();
			pause(HALFPERIOD);
			if (!panel.isOn()) {
				throw new CollisionException();
			}
	}

	/**
	 * Given a string of bits (0s and 1s), send each bit using broadcastOne/Zero().
	 * Build up a string of successfully sent bits (called "broadcasted"). Switch
	 * the light off when done.
	 * 
	 * @throws CollisionException when a collision occurs
	 */
	public void broadcast(String bits) throws CollisionException {
		for (int i = 0; i < bits.length(); i++) {
			if (bits.charAt(i) == '0') {
				broadcastZero();
			} else if (bits.charAt(i) == '1') {
				broadcastOne();
			} else {
				// Shouldnt get here
				System.out.println("Error broadcasting");
			}
		}
		panel.switchOff();
	}

	/**
	 * Return the ID of the LightPanel the BitHandler is tied to
	 *
	 * @return the ID of the LightPanel the BitHandler is associated with
	 */
	public String toString() {
		return panel.toString();
	}

	/**
	 * Repeatedly (and as fast as possible), check if the panel's light has changed
	 * from on to off or vice versa. When it does, check how much time passed
	 * between transitions. Based on this time and which direction the transition
	 * was made, determine if a 0 or 1 bit was received. Send the result off to the
	 * registered Listener.
	 */
	public void run() {
		long lastTransition = System.currentTimeMillis();
		String bits = "";
		boolean wasOn = false;
		while (true) {
			long time = System.currentTimeMillis();
			String lastState = state;

			if (panel.isOn() != wasOn) { // a transition was made: on to off or vice versa
				if (time - lastTransition > HALFPERIOD * 1.5) {
					// full delay
					if (state.equals(SILENCE)) {
						/* all broadcasts start with a 0, after silence for a while */
						state = EXPECT_ZERO;
					} else if (state.equals(EXPECT_ZERO)) {
						bits = "";
						state = SILENCE;
					} else if (state.equals(EXPECT_ONE)) {
						state = EXPECT_ZERO;
						notifyReceived(bits);
						bits = "";
					} else if (state.equals(HALF_ZERO)) {
						bits += "0";
						state = HALF_ONE;
					} else if (state.equals(HALF_ONE)) {
						bits += "1";
						state = HALF_ZERO;
					} else if (state.equals(GARBAGE))
						state = HALF_ZERO;
				} else {
					// half delay
					if (state.equals(SILENCE))
						state = EXPECT_ZERO;
					else if (state.equals(EXPECT_ZERO))
						state = HALF_ZERO;
					else if (state.equals(EXPECT_ONE))
						state = HALF_ONE;
					else if (state.equals(HALF_ZERO)) {
						bits += "0";
						state = EXPECT_ZERO;
					} else if (state.equals(HALF_ONE)) {
						bits += "1";
						state = EXPECT_ONE;
					} else if (state.equals(GARBAGE))
						state = HALF_ZERO;
				}
				lastTransition = time;
				wasOn = !wasOn;
			} else if (time - lastTransition > 3 * HALFPERIOD) {
				// timeout

				// System.out.println(this + ": timeout");

				if (state.equals(SILENCE)) {
					// no transition
				} else if (state.equals(EXPECT_ZERO)) {
					bits = "";
					state = GARBAGE;
				}

				if (state.equals(EXPECT_ONE)) {
					state = SILENCE;
					notifyReceived(bits);
					bits = "";
				} else if (state.equals(HALF_ZERO)) {
					state = SILENCE;
					notifyReceived(bits + "0");
					bits = "";
				} else if (state.equals(HALF_ONE)) {
					bits = "";
					state = GARBAGE;
				} else if (state.equals(GARBAGE)) {
					// no transition
				}
			}

			// System.out.println(getID() + " is alive");

			pause(1);
		}
	}

	/**
	 * Explicitly set the reference to a given BitListener
	 *
	 * @param l BitListener we want to reference
	 */
	public void setListener(BitListener l) {
		listener = l;
	}

	/**
	 * Returns if the listener is not receiving anything
	 *
	 * @return if the BitListener's state == SILENCE
	 */
	public boolean isSilent() {
		return state.equals(SILENCE);
	}

	/**
	 * Returns the ID of the LightPanel (in integer form)
	 *
	 * @return the associated LightPanel ID (integer)
	 */
	public int getID() {
		return panel.getID();
	}

	/**
	 * Notify the user that the BitHandler has received bits
	 * from somewhere
	 *
	 * @param bits the string of 0s and 1s that the BitHandler received
	 */
	private void notifyReceived(final String bits) {
		if (listener == null)
			return;
		new Thread() {
			public void run() {
				listener.bitsReceived(BitHandler.this, bits);
			}
		}.start();
		System.out.println(this + " received bits: " + bits);
	}
}