import com.amazonaws.services.iot.client.AWSIotException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Launcher {
    public static void main(String[] args) throws IOException, AWSIotException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Thing's name: ");
        String name = in.readLine();
        StatsSender statsSender = new StatsSender(new MyDevice(name));
        statsSender.launch();
    }
}
