public class L2Frame {
	
	private int _destAddr;
	private int _srcAddr;
	private int _dataType;
	private int _vlanID;
	private int _payloadSize;
	private int _checksum;
	private String _payload;

	public L2Frame(int destAddr, int srcAddr, int dataType, int vlanID, String payload) {
		_destAddr = destAddr;
		_srcAddr = srcAddr;
		_dataType = dataType;
		_vlanID = vlanID;
		byte[] payloadBytes = payload.getBytes();
		_payloadSize = (byte) payloadBytes.length;
		_checksum = 0;
		_payload = payload;
	}
}