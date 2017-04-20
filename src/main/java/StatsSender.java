import com.amazonaws.services.iot.client.*;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;


public class StatsSender {
    private MyDevice myDevice;

    public StatsSender(MyDevice myDevice){
        this.myDevice = myDevice;
    }

    public void launch() throws AWSIotException, IOException {
        String clientEndpoint   = "afvm3xhclkq9c.iot.us-east-1.amazonaws.com";
        String certificateFile  = "certificateFirstThing.cert.pem.crt";
        String privateKeyFile   = "31f7071e58-private.pem.key";
        AWSIotQos qos           = AWSIotQos.QOS0;
        int timeout             = 3000;

        SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, myDevice.getThingName(), pair.keyStore, pair.keyPassword);
        client.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, client.getClientId()));
        String topic = "CPU/usage/" + myDevice.getThingName();

        client.attach(myDevice);
        client.connect();

        AWSIotConnectionStatus status = AWSIotConnectionStatus.DISCONNECTED;

        while(true){
            AWSIotConnectionStatus newStatus = client.getConnectionStatus();
            if (!status.equals(newStatus)) {
                System.out.println(System.currentTimeMillis() + " Connection status changed to " + newStatus);
                status = newStatus;
            }

            try {
                String payload = myDevice.getThingName() + ": " + String.valueOf(getProcessCpuLoad());
                if(!payload.equals("NaN")){
                    if(myDevice.getSending()) client.publish(new StatsMessage(topic,qos, payload),timeout);
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                client.disconnect();
            }
        }

    }

    public static double getProcessCpuLoad() throws Exception {
        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "SystemCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int)(value * 1000) / 10.0);
    }
}
