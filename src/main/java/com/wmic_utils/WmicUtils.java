package com.wmic_utils;

import com.pojo.SoftwareInfo;
import com.pojo.SystemInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class WmicUtils {



    /****************************************************************** 1.服务 ******************************************************************/
    /**
     * 服务 -- 获取所有服务信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static List<Map<String, Object>> getServices(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" service get /value" };
        int flag = 25;  //信息列数

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
                if (str != null && !"".equals(str)) {
                    if (index % flag == 0) {
                        list.add(map);
                        map = new HashMap<String, Object>();
                    }
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
    /****************************************************************** /1.服务 ******************************************************************/


    /****************************************************************** 2.内存 ******************************************************************/
    /**
     * 内存 -- 获取所有内存条信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static List<Map<String, Object>> getMemorychips(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" memorychip get /value" };
        int flag = 30;  //信息列数

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
                if (str != null && !"".equals(str)) {
                    if (index % flag == 0) {
                        list.add(map);
                        map = new HashMap<String, Object>();
                    }
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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

    /**
     * 内存 -- 获取所有总内存大小
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static Long getMemoryCountSize(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" COMPUTERSYSTEM get TotalPhysicalMemory /value" };

        Process p = null;
        String str = null;
        String[] arrStr = new String[2];
        Long size = 0L;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                if (str != null && !"".equals(str)) {
                    arrStr = str.split("=");
                    size = Long.valueOf(arrStr[1]);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
        return size;
    }
    /****************************************************************** /2.内存 ******************************************************************/

    /****************************************************************** 3.CPU ******************************************************************/
    /**
     * CPU -- 获取所有CPU信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static List<Map<String, Object>> getCpuInfos(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" cpu get /value" };
        int flag = 48;  //信息列数

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
                if (str != null && !"".equals(str)) {
                    if (index % flag == 0) {
                        list.add(map);
                        map = new HashMap<String, Object>();
                    }
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
    /****************************************************************** /3.CPU ******************************************************************/

    /****************************************************************** 4.磁盘 ******************************************************************/
    /**
     * 获取所有磁盘信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static List<Map<String, Object>> getDiskInfos(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\"  volume get /value" };
        int flag = 44;  //信息列数

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
                if (str != null && !"".equals(str)) {
                    if (index % flag == 0) {
                        list.add(map);
                        map = new HashMap<String, Object>();
                    }
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
    /****************************************************************** /4.磁盘 ******************************************************************/

    /****************************************************************** 5.计算机信息 ******************************************************************/
    /**
     * 获取所有磁盘信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static Map<String, Object> getComputerSystem(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\"  computersystem get /value" };
        int flag = 58;  //信息列数

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
                if (str != null && !"".equals(str)) {
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
        return map;
    }

    /**
     * 获取系统信息(systeminfo)
     * @param address 远程机器地址
     * @return
     */
    public static SystemInfo getSysteminfo(String address,String userName,String passWord) {
        //systeminfo /s 192.168.1.22 /u cad22-win7-u /p gofar /fo List
        String[] cmdStr = { "cmd", "/C", "systeminfo /s "+address+" /u "+userName+" /p "+passWord+""};

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        SystemInfo systemInfo = new SystemInfo();
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);

            while ((str = br.readLine()) != null) {
                //System.out.println(str);

                //设置主机名
                if(str.startsWith("主机名")){
                    String[] split = str.split(":");
                    str = str.endsWith(":")?"":split[1].trim();
                    systemInfo.setSysName(str);
                }

                //设置OS 名称
                if(str.startsWith("OS 名称")){
                    String[] split = str.split(":");
                    str = str.endsWith(":")?"":split[1].trim();
                    systemInfo.setOsName(str);
                }

                //设置物理内存总量
                if(str.startsWith("物理内存总量")){
                    String[] split = str.split(":");
                    str = str.endsWith(":")?"":split[1].trim();
                    systemInfo.setTotalPhysicalMemory(str);
                }

                //设置可用的物理内存
                if(str.startsWith("可用的物理内存")){
                    String[] split = str.split(":");
                    str = str.endsWith(":")?"":split[1].trim();
                    systemInfo.setAvailablePhysicalMemory(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
        return systemInfo;
    }
    /****************************************************************** /5.计算机信息 ******************************************************************/

    /****************************************************************** 6.进程 ******************************************************************/
    /**
     * 获取所有进程信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static List<Map<String, Object>> getProcesses(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\"  process get /value" };
        int flag = 45;  //信息列数

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
                if (str != null && !"".equals(str)) {
                    if (index % flag == 0) {
                        list.add(map);
                        map = new HashMap<String, Object>();
                    }
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
    /****************************************************************** /6.进程 ******************************************************************/

    /****************************************************************** 7.软件（wmic方式，基于msi的软件） ******************************************************************/
    /**
     * 获取所有软件信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static List<Map<String, Object>> getProducts(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\"  PRODUCT get /value" };
        int flag = 27;  //信息列数

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
                if (str != null && !"".equals(str)) {
                    if (index % flag == 0) {
                        list.add(map);
                        map = new HashMap<String, Object>();
                    }
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
    /****************************************************************** /7.软件（wmic方式，基于msi的软件） ******************************************************************/


    /****************************************************************** /8.IPC连接 ******************************************************************/
    //------------------ 连接
    public static void connectIPC(String address,String userName,String password) {
        //net use \\192.168.1.22\ipc$ "gofar" /user:"cad22-win7-u"
        String cmdStr = "net use \\\\"+address+"\\ipc$ \""+password+"\" /user:\""+userName+"\"";

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                System.out.println(str);
            }
            //System.out.println("连接成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //------------------ 断开连接
    public static void delConnectIPC(String address) {
        //net use \\192.168.1.22\ipc$ /del
        String cmdStr = "net use \\\\"+address+"\\ipc$ /del";

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                System.out.println(str);
            }
            //System.out.println("断开连接成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /****************************************************************** /8.IPC连接 ******************************************************************/

    /****************************************************************** 9.软件（reg方式） ******************************************************************/
    /**
     * 获取所有软件详细信息
     * @param address
     * @return
     */
    public static List<SoftwareInfo> getAllProducts(String address){
        List<String> productsReg = WmicUtils.getProductsReg(address);
        List<String> productsRegWow6432Node = WmicUtils.getProductsRegWow6432Node(address);

        HashMap<String, String> map = new HashMap<String, String>();
        for (String s : productsReg) {
            int index = s.lastIndexOf("\\")+1;
            String name = s.substring(index,s.length());
            if(!map.containsKey(name)){
                if("IE4Data".equals(name) || "IEData".equals(name) || "IE40".equals(name) || "IE5BAKEX".equals(name) || "AddressBook".equals(name)
                        || "Connection Manager".equals(name) || "DirectDrawEx".equals(name) || "Fontcore".equals(name) || "IE5BAKEX".equals(name) || "WIC".equals(name)
                        || "MobileOptionPack".equals(name) || "SchedulingAgent".equals(name) || "DXM_Runtime".equals(name) || "MPlayer2".equals(name)){
                    continue;
                } else {
                    map.put(name,s);
                }
            }
        }

        for (String s : productsRegWow6432Node) {
            int index = s.lastIndexOf("\\")+1;
            String name = s.substring(index,s.length());
            if(!map.containsKey(name)){
                if("IE4Data".equals(name) || "IEData".equals(name) || "IE40".equals(name) || "IE5BAKEX".equals(name) || "AddressBook".equals(name)
                        || "Connection Manager".equals(name) || "DirectDrawEx".equals(name) || "Fontcore".equals(name) || "IE5BAKEX".equals(name) || "WIC".equals(name)
                        || "MobileOptionPack".equals(name) || "SchedulingAgent".equals(name) || "DXM_Runtime".equals(name) || "MPlayer2".equals(name)){
                    continue;
                } else {
                    map.put(name,s);
                }
            }
        }

        List<SoftwareInfo> softwareInfos = new ArrayList<SoftwareInfo>();

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            //System.out.println(entry);
            SoftwareInfo softwareInfo = WmicUtils.getProductMsg(address, entry.getValue());
            if (softwareInfo.getDisplayName()==null || softwareInfo.getDisplayName().length()<=0){
                continue;
            }
            softwareInfos.add(softwareInfo);
        }

        return softwareInfos;
    }

    /**
     * 获取 \HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall 下所有软件信息
     * @param address       远程机器地址
     * @return
     */
    public static List<String> getProductsReg(String address) {
        String[] cmdStr = { "cmd", "/C", "reg query \\\\"+address+"\\HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall" };

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        //key
        ArrayList<String> keys = new ArrayList<String>();

        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                if (str != null && !"".equals(str)) {
                    char c = str.charAt(str.lastIndexOf("\\") + 1);
                    if('{'!=(c)){
                        keys.add(str);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
        return keys;
    }

    /**
     * 获取 \HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall 下所有软件信息（Wow6432Node）
     * @param address       远程机器地址
     * @return
     */
    public static List<String> getProductsRegWow6432Node(String address) {
        String[] cmdStr = { "cmd", "/C", "reg query \\\\"+address+"\\HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall"};

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        //key
        ArrayList<String> keys = new ArrayList<String>();

        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                if (str != null && !"".equals(str)) {
                    char c = str.charAt(str.lastIndexOf("\\") + 1);
                    if('{'!=(c)){
                        keys.add(str);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
        return keys;
    }

    /**
     * 获取软件详细信息
     * @param address       远程机器地址
     * @return
     */
    public static SoftwareInfo getProductMsg(String address,String key) {
        key = key.replaceAll(" ","\" \"");
        String[] cmdStr = { "cmd", "/C", "reg query \\\\"+address+"\\"+key};

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        SoftwareInfo softwareInfo = new SoftwareInfo();

        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);

            while ((str = br.readLine()) != null) {
                //System.out.println(str);
                String trim = str.trim();
                //设置软件名
                if(trim.startsWith("DisplayName")){
                    String[] split = trim.split("    REG_SZ    ");
                    str = str.endsWith("    REG_SZ    ")?"":split[1];
                    softwareInfo.setDisplayName(str);
                }

                //设置版本
                if(trim.startsWith("DisplayVersion")){
                    String[] split = trim.split("    REG_SZ    ");
                    str = str.endsWith("    REG_SZ    ")?"":split[1];
                    softwareInfo.setDisplayVersion(str);
                }

                //设置发布者
                if(trim.startsWith("Publisher")){
                    String[] split = trim.split("    REG_SZ    ");
                    str = str.endsWith("    REG_SZ    ")?"":split[1];
                    softwareInfo.setPublisher(str);
                }

                //设置安装日期
                if(trim.startsWith("InstallDate")){
                    String[] split = trim.split("    REG_SZ    ");
                    str = str.endsWith("    REG_SZ    ")?"":split[1];
                    if (str.length()>0){
                        str = str.replaceAll("/","-");
                    }
                    softwareInfo.setInstallDate(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
        return softwareInfo;
    }
    /****************************************************************** /9.软件（reg方式） ******************************************************************/


    /****************************************************************** 10.ping ******************************************************************/
    /**
     * 获取软件详细信息
     * @param address       远程机器地址
     * @return
     */
    public static Boolean getPing(String address) {
        String[] cmdStr = { "cmd", "/C", "ping "+address+""};

        boolean flag = false;

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        ArrayList<Boolean> booleans = new ArrayList<Boolean>();

        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);

            while ((str = br.readLine()) != null) {
                System.out.println(str);
                if(str.startsWith("来自") || str.startsWith("Replay form")){
                    if(str.indexOf("TTL=")!=-1){
                        booleans.add(true);
                    }else {
                        booleans.add(false);
                    }
                }
            }

            flag=booleans.get(3);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
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
        return flag;
    }
    /****************************************************************** /10.ping ******************************************************************/
}
