import java.io.*;
import java.net.*;
import java.util.*;

public class TestClient {
    public static void main(String[] args) {
        LightDisplay d1 = new LightDisplay(new LightPanel("153.106.116.87", 9223));
        BitDisplay b1 = new BitDisplay(new BitHandler("153.106.116.87", 9223));
    }
}
