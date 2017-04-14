
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.fasterxml.jackson.databind.ObjectMapper;


public class StatsTopic extends AWSIotTopic {

    private MyDevice myDevice;
    ObjectMapper objectMapper = new ObjectMapper();

    public StatsTopic(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        boolean state = Boolean.parseBoolean(String.valueOf(message));
        myDevice.setSending(state);

    }

    public void setMyDevice(MyDevice myDevice){
        this.myDevice = myDevice;
    }
}
