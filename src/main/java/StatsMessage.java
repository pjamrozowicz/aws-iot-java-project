import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;

public class StatsMessage extends AWSIotMessage {
    public StatsMessage(String topic, AWSIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    @Override
    public void onSuccess() {
        System.out.println("Successfully sent message!");
    }

    @Override
    public void onFailure() {
        System.out.println("Failure sending message!");
    }

    @Override
    public void onTimeout() {
        System.out.println("Timeout when sending message!");
    }
}
