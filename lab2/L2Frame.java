/**
 * This class implements a layer 2 frame to be passed between
 * LightPanels in our LightSystem
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class L2Frame {

	public static int BCAST_ADDR = 15;

	private int destAddr;
	private int srcAddr;
	private int type;
	private int vlanID;
	private int payloadSize;
	private int checksum;
	private String payload;

	public L2Frame(int dA, int sA, int t, int vlID, String pyld) throws Exception {
		
		if (dA >= 0 && dA < 15) {
			destAddr = dA;
		} else {
			throw new Exception("Destination Address must be between 0 and 14");
		}

		if (sA >= 0 && sA < 15) {
			srcAddr = sA;
		} else {
			throw new Exception("Source Address must be between 0 and 14");
		}

		if (t >= 0 && t < 4) {
			type = t;
		} else {
			throw new Exception("Type must be between 0 and 3");
		}

		if (vlID >= 0 && vlID < 4) {
			vlanID = vlID;
		} else {
			throw new Exception("VLAN ID must be between 0 and 3");
		}
			
		payload = pyld;
		payloadSize = payload.length();
		checksum = computeErrorCheck(toBinary(destAddr, 4)
					+ toBinary(srcAddr, 4) + toBinary(type, 2)
					+ toBinary(vlanID, 2) + toBinary(payload)
					+ toBinary(payloadSize, 8));
	}

	public int getDestAddr() {
		return destAddr;
	}

	public int getSrcAddr() {
		return srcAddr;
	}

	public int getType() {
		return type;
	}

	public int getVLANID() {
		return vlanID;
	}

	public int getPayloadSize() {
		return payloadSize;
	}

	public int getChecksum() {
		return checksum;
	}

	public String getPayload() {
		return payload;
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
	private String toBinary(int input, int bitLength) {
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

	/**
	 * Conversion method that takes a plaintext string and
	 * returns a string that is a binary representation
	 * of the given input - referenced StackOverflow for solution
	 *
	 * @param input the plaintext to be converted to binary
	 */
	private String toBinary(String input) {
		StringBuilder output = new StringBuilder();
		byte[] bytes = input.getBytes();

		for (byte b : bytes) {
			int currentByte = b;
			for (int i = 0; i < 8; i++) {
				output.append((currentByte & 128) == 0 ? 0 : 1);
				currentByte <<= 1;
			}
		}

		return output.toString();
	}

	/**
	 * Helper method that determines the parity of a given
	 * bit string - returns 0 if there are an even number of
	 * 1s and 1 if there are an odd number of 1s in the string
	 *
	 * @param bitString the string to be error-checked
	 */
	private int computeErrorCheck(String bitString) {
		int ones = 0;
		for (int i = 0; i < bitString.length(); i++) {
			if (bitString.charAt(i) == '1') {
				ones++;
			}
		}
		return ones % 2;
	}

	/**
	 * Method that returns a bit string representation of the layer
	 * 2 frame object that is being represented
	 */
	public String toString() {
		return "0 " + toBinary(destAddr, 4) + " " + toBinary(srcAddr, 4)
			+ " " + toBinary(type, 2) + " " + toBinary(vlanID, 2)
			+ " " + toBinary(payloadSize, 8) + " " + toBinary(checksum, 1)
			+ " " + toBinary(payload);				
	}
}