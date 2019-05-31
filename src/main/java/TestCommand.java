import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class TestCommand {
    public static void main(String[] args) {
//        String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.27\" /user:\"20_lmt_199\" /password:\"gofar\" service get /value" };
//        String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.184\" /user:\"Administrator\" /password:\"gofar@2019\" service get /value" };
//        String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.20\" /user:\"administrator\" /password:\"gofar@2019\" service get /value" };
        //本机
        /*String[] cmdStr = { "cmd", "/C", "wmic service get /value" };*/
        String[] cmdStr = { "cmd", "/C", "reg query \\\\192.168.1.22\\HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall" };
        //String[] cmdStr = { "cmd", "/C", "reg query \\\\192.168.1.22\\HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall"};
        //String[] cmdStr = { "cmd", "/C", "reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\360安全卫士"};

        //String[] cmdStr = { "cmd", "/C", "reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall /v Text"};

        /**
         * 服务器22
         */
        //获取所有服务
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" service get /value" };
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" service where name=\"gfclient\" call startservice" };
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" SERVICE where name=\"gfclient\" call stopservice" };

        //软件 product get /value //product get name,version
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" product get /value" };
        //String[] cmdStr = { "cmd", "/C", "wmic PRODUCT get /value" };

        //列出进程
        //wmic process list brief
        //(Full显示所有、Brief显示摘要、Instance显示实例、Status显示状态)
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" process get /value" };
        //查找指定进程
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" process where name=\"gf_client.exe\" get /value" };

        //CPU:  cpu get /value  //name,addresswidth,processorid
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" cpu get /value" };

        //内存:
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" memorychip get /value" };
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" memorychip get capacity" };
        /* 内存总大小 */
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" COMPUTERSYSTEM get TotalPhysicalMemory /value" };

        //用户
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" USERACCOUNT get /value" };

        //硬盘
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" volume get /value" };
        //String[] cmdStr = { "cmd", "/C", "wmic volume get /value" };

        //获取系统信息
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" computersystem get /value" };

        //注册表 REGISTRY
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\"192.168.1.22\" /user:\"cad22-win7-u\" /password:\"gofar\" SOFTWAREELEMENT" };

        List<Map<String, Object>> list = null;
        try {
            list = getAllResult(cmdStr, 44);
            System.out.println(list.size());
            JSONArray jr = JSONArray.fromObject(list);
            JSONObject jo = new JSONObject();
            for (int i = 0; i < list.size(); i++) {
                jo.clear();
                jo = (JSONObject) jr.get(i);
                if ("Apache Tomcat".equals(jo.get("Caption"))) {
                    String ifStarted = jo.get("Started").toString();
                    System.out.println("Apache Tomcat服务" + ifStarted);
                } else if ("MySQL".equals(jo.get("Caption"))) {
                    String ifStarted = jo.get("Started").toString();
                    System.out.println("MySQL服务" + ifStarted);
                } else if ("PCMS Service".equals(jo.get("Caption"))) {
                    String ifStarted = jo.get("Started").toString();
                    System.out.println("PCMS Service服务" + ifStarted);
                } else if ("PCMS Watch Service".equals(jo.get("Caption"))) {
                    String ifStarted = jo.get("Started").toString();
                    System.out.println("PCMS Watch Service服务" + ifStarted);
                }
            }
            //System.out.println(list);

            for (Map<String, Object> stringObjectMap : list) {
                System.out.println(stringObjectMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取所有服务信息失败！");
        }
    }

    private static List<Map<String, Object>> getAllResult(String[] cmdStr, int flag) throws IOException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Integer index = 1;
        Process p = null;
        String str = null;
        String[] arrStr = new String[2];
        Map<String, Object> map = new HashMap<String, Object>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                System.out.println(str);
                /*if (str != null && !"".equals(str)) {
                    if (index % flag == 0) {
                        list.add(map);
                        map = new HashMap<String, Object>();
                    }
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                    index++;
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取进程的所有信息失败！");
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取执行结果失败！");
        } finally {
            try {
                if (br != null) {
                }
                br.close();
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (p != null) {
                p.destroy();
            }
        }
        return list;
    }
}