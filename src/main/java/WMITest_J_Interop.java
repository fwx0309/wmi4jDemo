import java.net.UnknownHostException;
import java.util.logging.Level;

import org.jinterop.dcom.common.IJIUnreferenced;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.jinterop.dcom.impls.automation.IJIEnumVariant;

public class WMITest_J_Interop {

    private JIComServer comStub = null;
    private IJIComObject comObject = null;
    private IJIDispatch dispatch = null;
    private String address = null;
    private JISession session = null;
    
    public WMITest_J_Interop(String address, String[] args) throws JIException, UnknownHostException
    {
        this.address = address;
        session = JISession.createSession(args[1],args[2],args[3]);     //domain, username, password
        session.useSessionSecurity(true);
        session.setGlobalSocketTimeout(60000);      //将超时时间设置长一些
        comStub = new JIComServer(JIProgId.valueOf("WbemScripting.SWbemLocator"),address,session);
//        String addressTemp = comStub.getAddress();
//        addressTemp = addressTemp.substring(0,addressTemp.lastIndexOf("["));
//        addressTemp = addressTemp+"[24158]";
//        comStub.setAddress(addressTemp);

        IJIComObject unknown = comStub.createInstance();
        comObject = (IJIComObject)unknown.queryInterface("76A6415B-CB41-11d1-8B02-00600806D9B6");//ISWbemLocator
        //This will obtain the dispatch interface
        dispatch = (IJIDispatch)JIObjectFactory.narrowObject(comObject.queryInterface(IJIDispatch.IID));
    }


    public void performOp(String strQuery) throws JIException, InterruptedException
    {
        System.gc();
        JIVariant results[] = dispatch.callMethodA("ConnectServer",
                        new Object[]{new JIString(address),JIVariant.OPTIONAL_PARAM(),
                        JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
                        JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
                        new Integer(0),JIVariant.OPTIONAL_PARAM()});

        /*using the dispatch results above you can use the "ConnectServer" api to retrieve a pointer to IJIDispatch of ISWbemServices OR Make a direct call like below ,
        in this case you would get back an interface pointer to ISWbemServices , NOT to it's IDispatch*/
        JICallBuilder callObject = new JICallBuilder();
        callObject.addInParamAsString(address,JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        callObject.addInParamAsString("",JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        callObject.addInParamAsString("",JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        callObject.addInParamAsString("",JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        callObject.addInParamAsString("",JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        callObject.addInParamAsString("",JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        callObject.addInParamAsInt(0,JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(null,JIFlags.FLAG_NULL);
        callObject.setOpnum(0);
        callObject.addOutParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
        IJIComObject wbemServices = JIObjectFactory.narrowObject((IJIComObject)((Object[])comObject.call(callObject))[0]);
        wbemServices.setInstanceLevelSocketTimeout(1000);
        wbemServices.registerUnreferencedHandler(new IJIUnreferenced(){
            public void unReferenced()
            {
                System.out.println("wbemServices unreferenced... ");
            }
        });

        //Lets have a look at both.
        IJIDispatch wbemServices_dispatch = (IJIDispatch)JIObjectFactory.narrowObject((results[0]).getObjectAsComObject());
        //results = wbemServices_dispatch.callMethodA("InstancesOf", new Object[]{new JIString("Win32_Process"), new Integer(0), JIVariant.OPTIONAL_PARAM()});
//        String strQuery = "SELECT * FROM Win32_PerfFormattedData_PerfOS_Processor WHERE Name != '_Total'";
        results = wbemServices_dispatch.callMethodA("ExecQuery", new Object[] {new JIString(strQuery), JIVariant.OPTIONAL_PARAM(),
                JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});
        
        IJIDispatch wbemObjectSet_dispatch = (IJIDispatch)JIObjectFactory.narrowObject((results[0]).getObjectAsComObject());
        JIVariant variant = wbemObjectSet_dispatch.get("_NewEnum");
        IJIComObject object2 = variant.getObjectAsComObject();

        System.out.println(object2.isDispatchSupported());
        System.out.println(object2.isDispatchSupported());
        
        object2.registerUnreferencedHandler(new IJIUnreferenced(){
            public void unReferenced()
            {
                System.out.println("object2 unreferenced...");
            }
        });

        IJIEnumVariant enumVARIANT = (IJIEnumVariant)JIObjectFactory.narrowObject(object2.queryInterface(IJIEnumVariant.IID));

        //This will return back a dispatch of ISWbemObjectSet

        //OR
        //It returns back the pointer to ISWbemObjectSet
        callObject = new JICallBuilder();
        callObject.addInParamAsString("Win32_Process",JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        callObject.addInParamAsInt(0,JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(null,JIFlags.FLAG_NULL);
        callObject.setOpnum(4);
        callObject.addOutParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
        IJIComObject wbemObjectSet = JIObjectFactory.narrowObject((IJIComObject)((Object[])wbemServices.call(callObject))[0]);

        //okay seen enough of the other usage, lets just stick to disptach, it's lot simpler
        JIVariant Count = wbemObjectSet_dispatch.get("Count");
        int count = Count.getObjectAsInt();
        for (int i = 0; i < count; i++)
        {
            Object[] values = enumVARIANT.next(1);
            JIArray array = (JIArray)values[0];
            Object[] arrayObj = (Object[])array.getArrayInstance();
            for (int j = 0; j < arrayObj.length; j++)
            {
                IJIDispatch wbemObject_dispatch = (IJIDispatch)JIObjectFactory.narrowObject(((JIVariant)arrayObj[j]).getObjectAsComObject());
                JIVariant variant2 = (JIVariant)(wbemObject_dispatch.callMethodA("GetObjectText_",new Object[]{new Integer(1)}))[0];
                System.out.println(variant2.getObjectAsString().getString());
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        }
    }

    private void killme() throws JIException
    {
        JISession.destroySession(session);
    }

    public static void main(String[] args) throws Exception {
        
        args = new String[4];
//        args[0] = "192.168.1.20";
//        args[1] = "192.168.1.20";       //domain
//        args[2] = "administrator";      //username
//        args[3] = "gofar@2019";         //password

//        args[0] = "192.168.1.27";
//        args[1] = "192.168.1.27";       //domain
//        args[2] = "20_lmt_199";      //username
//        args[3] = "gofar";         //password

        args[0] = "192.168.1.22";
        args[1] = "192.168.1.22";       //domain
        args[2] = "cad22-win7-u";      //username
        args[3] = "gofar";         //password

//        args[0] = "192.168.1.112";
//        args[1] = "192.168.1.112";       //domain
//        args[2] = "gofar3";      //username
//        args[3] = "gofar123";         //password
        JISystem.getLogger().setLevel(Level.ALL);
        JISystem.setInBuiltLogHandler(false);
        JISystem.setAutoRegisteration(true);
        WMITest_J_Interop test = new WMITest_J_Interop(args[0],args);
        
//        // 系统信息
//        wt.query("SELECT * FROM Win32_ComputerSystem");
//        // CPU信息
//        wt.query("SELECT * FROM Win32_PerfFormattedData_PerfOS_Processor WHERE Name != '_Total'");
//        // 内存信息
//        wt.query("SELECT * FROM Win32_PerfFormattedData_PerfOS_Memory");
//        // 磁盘信息
//        wt.query("SELECT * FROM Win32_PerfRawData_PerfDisk_PhysicalDisk Where Name != '_Total'");

//        System.out.println("//        // 系统信息");
//        test.performOp("SELECT * FROM Win32_ComputerSystem");
//        System.out.println("//        // CPU信息");
//        test.performOp("SELECT * FROM Win32_PerfFormattedData_PerfOS_Processor WHERE Name != '_Total'");
        System.out.println("//        // 内存信息");
        test.performOp("SELECT * FROM Win32_PerfFormattedData_PerfOS_Memory");
//        System.out.println("//        // 磁盘信息");
//        test.performOp("SELECT * FROM Win32_PerfRawData_PerfDisk_PhysicalDisk Where Name != '_Total'");
//        System.out.println("//        // 进程信息");
//        test.performOp("SELECT * FROM Win32_Process Where Name != '_Total'");
//        System.out.println("//        // 服务信息");
//        test.performOp("SELECT * FROM Win32_Service");

/*        System.out.println("//        // 软件安装信息");
        test.performOp("SELECT * FROM Win32_Product");*/
        
        test.killme();
    }
}