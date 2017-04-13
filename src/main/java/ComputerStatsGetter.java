import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;


public class ComputerStatsGetter {

    public static void main(String[] args) throws AWSIotException {

        String clientEndpoint   = "private";
        String clientId         = "MSISender";
        String certificateFile  = "certificateFirstThing.cert.pem.crt";
        String privateKeyFile   = "31f7071e58-private.pem.key";

        SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

        client.connect();
        System.out.println("Connected!");
        String topic = "CPU/usage";

        for(int i=0;i<100;i++){
            try {
                String payload = String.valueOf(getProcessCpuLoad());
                if(!payload.equals("NaN")){
                    System.out.println("Publishing: " + payload);
                    client.publish(topic, AWSIotQos.QOS0, payload);
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        client.disconnect();

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
