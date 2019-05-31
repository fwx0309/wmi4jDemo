import cn.chenlichao.wmi4j.SWbemLocator;
import cn.chenlichao.wmi4j.SWbemObject;
import cn.chenlichao.wmi4j.SWbemServices;
import cn.chenlichao.wmi4j.WMIException;

import java.net.UnknownHostException;

public class QuickStart {

    public static void main(String[] args) {

        String server = "192.168.1.22";
        String username = "cad22-win7-u";
        String password = "gofar";
        String namespace = "root\\cimv2";

        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.27\" /user:\"20_lmt_199\" /password:\"gofar\" service get /value" };


        SWbemLocator locator = new SWbemLocator(server,username,password,namespace);

        try {
            SWbemServices services = locator.connectServer();
            SWbemObject object = services.get("Win32_Service.Name='AppMgmt'");

            //print AppMgmt properties
            System.out.println(object.getObjectText());

            //print AppMgmt service state
            System.out.println(object.getPropertyByName("State").getStringValue());

            //Stop AppMgmt service
            //object.execMethod("Stop");

        } catch (WMIException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
