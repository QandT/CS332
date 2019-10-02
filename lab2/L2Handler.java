/**
 * This class implements a layer 2 handler that is responsible for
 * passing along an L2Frame to be sent on layer 1 and receiving a
 * string of bits from layer 1 and creating an L2Frame from them
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class L2Handler implements BitListener {
	private BitHandler handler;
	//private Layer2Listener layer2listener;
	private int macAddr;

	public L2Handler(String host, int port, int addr) {
		handler = new BitHandler(host, port);
		handler.setListener(this);
		macAddr = addr;
	}

	public L2Handler(int addr) {
		handler = new BitHandler();
		handler.setListener(this);
		macAddr = addr;
	}

	public int getMACAddress() {
		return macAddr;
	}

	public void bitsReceived(BitHandler handler, String bits) {
		// maybe do something here?
	}

	/**
	 * Conversion method that takes an integer and the number of bits
	 * to be used for the conversion (i.e. the number of bits assigned
	 * to the field determined by our protocol) and returns a string 
	 * that is a binary representation of the given input
	 *
	 * @param input the integer to be converted to binary
	 * @param bitLength the number of bits to be used when converting
	 */
	private String toString(int input, int bitLength) {
		String output = "";
		int x = 2;
		for (int i = 0; i < bitLength; i++) {
			if (input % x >= x/2) {
				output = "1" + output;
			} else {
				output = "0" + output;
			}
			x *= 2;
		}
		return output;
	}
}