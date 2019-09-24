public class Test {
	public static void main(String[] args) {

		TestL2Frame();

		LightSystem system = new LightSystem();
		LightDisplay d1 = new LightDisplay(new LightPanel());
		LightDisplay d2 = new LightDisplay(new LightPanel());

		BitDisplay b = new BitDisplay(new BitHandler());
		BitDisplay b2 = new BitDisplay(new BitHandler());
	}

	private static void TestL2Frame() {

	}
}