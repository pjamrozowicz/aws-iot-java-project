import com.amazonaws.services.iot.client.*;

public class MyDevice extends AWSIotDevice{

    @AWSIotDeviceProperty
    private boolean sending;

    public MyDevice(String thingName) {
        super(thingName);
    }


    public void setSending(boolean sending){
        //System.out.println("Desired state for sending: " + sending);
        this.sending = sending;
    }

    public boolean getSending(){
        //System.out.println("Reported state for sending: " + sending);
        return this.sending;
    }

}
