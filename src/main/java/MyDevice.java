import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotDeviceProperty;

public class MyDevice extends AWSIotDevice{

    @AWSIotDeviceProperty
    private boolean sending;

    public MyDevice(String thingName) {
        super(thingName);
    }


    public void setSending(boolean sending){
        this.sending = sending;
    }

    public boolean getSending(){
        return this.sending;
    }

}
