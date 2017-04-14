import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Patryk on 2017-04-14.
 */

public class Controller {
    private final String name;
    private final String topic;
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final AWSIotQos qos = AWSIotQos.QOS0;
    private AWSIotMqttClient client;

    public Controller(String name, String topic) throws IOException {
        this.name = name;
        this.topic = topic;

        subscribeToTopic();
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Enter your name: ");
        String name = in.readLine();
        System.out.println("Enter topic that you want subscribe to: ");
        String topic = in.readLine();
        new Controller(name, topic);
    }

    private void subscribeToTopic() throws IOException {
        StatsTopic myTopic = new StatsTopic(topic, qos);
        String clientEndpoint = "afvm3xhclkq9c.iot.us-east-1.amazonaws.com";
        String certificateFile = "certificateFirstThing.cert.pem.crt";
        String privateKeyFile = "31f7071e58-private.pem.key";

        SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        client = new AWSIotMqttClient(clientEndpoint, name, pair.keyStore, pair.keyPassword);
        client.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, client.getClientId()));

        try {
            client.connect();
            client.subscribe(myTopic);
            sendLoop();
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
    }

    private void sendLoop() throws IOException {
        System.out.println("Waiting for data...");

        while (true) {
            System.out.println("Write name of device and his state: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String deviceName = in.readLine();
            String parts[] = deviceName.split(" ");

            if (parts.length != 2) {
                System.out.println("Wrong command. Usage: <deviceName> [true|false]");
            } else if (parts[1].equals("true") || parts[1].equals("false")) {
                String topic = "$aws/things/" + parts[0] + "/shadow/update";
                String payload = "{\"state\" : {\"desired\" : {\"sending\" : " + parts[1] + "}}}";

                try {
                    int timeout = 3000;
                    client.publish(new StatsMessage(topic, qos, payload), timeout);
                } catch (AWSIotException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
