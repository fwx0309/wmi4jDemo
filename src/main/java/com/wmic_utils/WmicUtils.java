package com.wmic_utils;

import com.pojo.ProcessEntity;
import com.pojo.SoftwareInfo;
import com.pojo.SystemInfo;
import wmic.Bytes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.NumberFormat;
import java.util.*;

public class WmicUtils {
    //可以设置长些，防止读到运行此次系统检查时的cpu占用率，就不准了
    private static final int CPUTIME = 5000;

    private static final int PERCENT = 100;

    private static final int FAULTLENGTH = 10;

    /****************************************************************** 0.获取所有数据 ******************************************************************/
    public static Map getAllData(String address,String userName,String passWord){
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        //网络情况
        Boolean ping = WmicUtils.getPing(address);
        if(ping){
            map.put("network","网络通讯正常!");
        } else {
            map.put("network","网络通讯异常!");
            return map;
        }

        //计算机信息
        SystemInfo systeminfo = WmicUtils.getSysteminfo(address, userName, passWord);
        map.put("sysName",systeminfo.getSysName());                                             //计算机名
        map.put("osName",systeminfo.getOsName());                                               //OS 名称
        map.put("totalPhysicalMemory",systeminfo.getTotalPhysicalMemory());                     //物理内存总量
        map.put("availablePhysicalMemory",systeminfo.getAvailablePhysicalMemory());             //可用的物理内存

        String memoryUsageRate = null;                                                          //内存使用率
        if(null!=systeminfo.getAvailablePhysicalMemory() && null!=systeminfo.getTotalPhysicalMemory()){
            String unUsed = systeminfo.getAvailablePhysicalMemory();
            String[] unUsedArr = unUsed.split(" ");
            String total = systeminfo.getTotalPhysicalMemory();
            String[] totalArr = total.split(" ");
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(0);
            memoryUsageRate = numberFormat.format((Float.valueOf(totalArr[0].replaceAll(",",""))-Float.valueOf(unUsedArr[0].replaceAll(",","")))/Float.valueOf(totalArr[0].replaceAll(",","")) * 100)+"%";
        }
        map.put("memoryUsageRate",memoryUsageRate);

        //cpu
        List<Map<String, Object>> cpuInfos = WmicUtils.getCpuInfos(address, userName, passWord);
        map.put("CPUNum",cpuInfos.size());                                                       //CPU个数
        map.put("cpuInfos",cpuInfos);                                                            //名字：Name；使用率：LoadPercentage


        //所有软件
        boolean connectIPC = WmicUtils.connectIPC(address, userName, passWord);     //创建IPC连接
        if (connectIPC){
            List<SoftwareInfo> allProducts = WmicUtils.getAllProducts(address);
            map.put("products",allProducts);
            WmicUtils.delConnectIPC(address);                                       //释放IPC连接
        }

        //进程信息
        List<ProcessEntity> processes = WmicUtils.getProcessesByTasklist(address, userName, passWord);
        map.put("processes",processes);

        //服务信息
        List<Map<String, Object>> services = WmicUtils.getServices(address, userName, passWord);
        map.put("services",services);

        //磁盘信息  Capacity：总空间 FreeSpace：剩余空间
        List<Map<String, Object>> diskInfos = WmicUtils.getDiskInfos(address, userName, passWord);
        map.put("diskInfos",diskInfos);

        //环境变量
        List<Map<String, String>> environments = WmicUtils.getEnvironment(address, userName, passWord);
        map.put("environments",environments);

        return map;
    }
    /****************************************************************** 0.获取所有数据 ******************************************************************/


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
            release(br,isr,p);
        }
        return list;
    }

    /**
     * 服务 -- 获取开启服务
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @param serviceName   服务名
     * @return
     */
    public static boolean startService(String address,String userName,String password,String serviceName) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" SERVICE where name=\""+serviceName+"\" call startservice" };

        boolean startFlag = false;

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                str = str.trim();
                if (str.startsWith("ReturnValue")){
                    System.out.println(str);
                    String[] split = str.split("=");
                    if("0;".equals(split[1].trim()) || "10;".equals(split[1].trim())){
                        startFlag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("操作失败！");
        } finally {
            release(br,isr,p);
        }
        return startFlag;
    }

    /**
     * 服务 -- 获取关闭服务
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @param serviceName   服务名
     * @return
     */
    public static boolean stopService(String address,String userName,String password,String serviceName) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" SERVICE where name=\""+serviceName+"\" call stopservice" };

        boolean startFlag = false;

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                str = str.trim();
                if (str.startsWith("ReturnValue")){
                    System.out.println(str);
                    String[] split = str.split("=");
                    if("0;".equals(split[1].trim()) || "5;".equals(split[1].trim())){
                        startFlag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("操作失败！");
        } finally {
            release(br,isr,p);
        }
        return startFlag;
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
            release(br,isr,p);
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
            release(br,isr,p);
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
            release(br,isr,p);
        }
        return list;
    }


    /**
     * 获得CPU使用率.
     * @return 返回cpu使用率
     * /node:"192.168.1.22" /user:"cad22-win7-u" /password:"gofar"
     * //可以设置长些，防止读到运行此次系统检查时的cpu占用率，就不准了
     * //private static final int CPUTIME = 5000;
     * //private static final int PERCENT = 100;
     */
    public static Map<String,Object> getCpuRatioForWindows(String address,String userName,String password) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            //String[] procCmd = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" process get Caption,ProcessId,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount" };
            String[] procCmd = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount" };

            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
//            Map<String, String> map0 = WmicUtils.getProcesse("192.168.1.22", "cad22-win7-u", "gofar", "360zip.exe");
//            System.out.println(map0);
            Thread.sleep(CPUTIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
//            Map<String, String> map1 = WmicUtils.getProcesse("192.168.1.22", "cad22-win7-u", "gofar", "360zip.exe");
//            System.out.println(map1);
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];

//                long kTime = Long.valueOf(map1.get("KernelModeTime")) - Long.valueOf(map0.get("KernelModeTime"));
//                long uTime = Long.valueOf(map1.get("UserModeTime")) - Long.valueOf(map0.get("UserModeTime"));

                //总时间
                long countTime = (busytime + idletime);

                //System.out.println(Double.valueOf(PERCENT * (kTime+uTime) / countTime).doubleValue());

                Double cpuUsageRate = Double.valueOf(PERCENT * (busytime) / countTime).doubleValue();
                map.put("cpuUsageRate",cpuUsageRate);
                map.put("countTime",countTime);

                return map;
            } else {
                map.put("cpuUsageRate",0.0);
                return map;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            map.put("cpuUsageRate",0.0);
            return map;
        }
    }

    /** *//**
     * 读取CPU信息.
     * @param proc
     * @return
     * private static final int FAULTLENGTH = 10;
     */
    private static long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream(),"GBK");
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            //int pIdidx = line.indexOf("ProcessId");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {

                System.out.println(line);

                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,//ProcessId,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperation
                String caption = Bytes.substring(line, capidx, cmdidx - 1).trim();
                String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }
                // log.info("line="+line);
                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    idletime += Long.valueOf(Bytes.substring(line, kmtidx, rocidx - 1).trim()).longValue();
                    idletime += Long.valueOf(Bytes.substring(line, umtidx, wocidx - 1).trim()).longValue();
                    continue;
                }

                kneltime += Long.valueOf(Bytes.substring(line, kmtidx, rocidx - 1).trim()).longValue();
                usertime += Long.valueOf(Bytes.substring(line, umtidx, wocidx - 1).trim()).longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
            release(br,isr,p);
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
            release(br,isr,p);
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
            release(br,isr,p);
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
            release(br,isr,p);
        }
        return list;
    }

    /**
     * 获取单个进程信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static Map<String, String> getProcesse(String address,String userName,String password,String processName) {
        //String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\"  process where name=\""+processName+"\" get KernelModeTime,UserModeTime /value" };
        String[] cmdStr = { "cmd", "/C", "wmic process where name=\""+processName+"\" get KernelModeTime,UserModeTime /value" };

        HashMap<String, String> map = new HashMap<String, String>();

        Process p = null;
        String str = null;
        String[] arrStr = new String[2];
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                //System.out.println(str);
                if(str.startsWith("KernelModeTime")){
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                }

                if(str.startsWith("UserModeTime")){
                    arrStr = str.split("=");
                    str = str.endsWith("=") ? "" : arrStr[1];
                    map.put(arrStr[0], str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
        } finally {
            release(br,isr,p);
        }
        return map;
    }

    /**
     * 获取创建进程
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @param processePath  进程路径
     * @return
     */
    public static boolean createProcesse(String address,String userName,String password,String processePath) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" process call create \""+processePath+"\"" };

        boolean createFlag = false;

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                str = str.trim();
                if (str.startsWith("ReturnValue")){
                    System.out.println(str);
                    String[] split = str.split("=");
                    if("0;".equals(split[1].trim())){
                        createFlag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
        } finally {
            release(br,isr,p);
        }
        return createFlag;
    }

    /**
     * 获取根据进程名关闭进程
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @param processeName  进程路径
     * @return
     */
    public static boolean closeProcessesByName(String address,String userName,String password,String processeName) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\" process where name=\""+processeName+"\" call terminate" };

        boolean closeFlag = false;

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
                str = str.trim();
                if ("No Instance(s) Available.".equals(str)){
                    return true;
                }
                if (str.startsWith("ReturnValue")){
                    System.out.println(str);
                    String[] split = str.split("=");
                    if("0;".equals(split[1].trim())){
                        closeFlag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
        } finally {
            release(br,isr,p);
        }
        return closeFlag;
    }

    /**
     * 获取所有进程信息（使用tasklist信息中包含用户名）
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static List<ProcessEntity> getProcessesByTasklist(String address, String userName, String password) {
        String cmdStr = "tasklist /s "+address+" /u "+userName+" /p "+password+" /V /FO LIST";
        int flag = 45;  //信息列数

        List<ProcessEntity> list = new ArrayList<ProcessEntity>();
        Integer index = 1;
        Process p = null;
        String str = null;
        String[] arrStr = new String[2];
        ProcessEntity processEntity = null;

        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                if (str != null && !"".equals(str)) {

                    if (str.startsWith("映像名称")){
                        processEntity = new ProcessEntity();

                        str=str.substring(str.indexOf(":")+1);
                        if (str.length()>0){
                            str= str.trim();
                        }
                        processEntity.setpName(str);
                    } else if (str.startsWith("PID")){
                        str=str.substring(str.indexOf(":")+1);
                        if (str.length()>0){
                            str= str.trim();
                        }
                        processEntity.setPid(str);
                    } else if (str.startsWith("会话名")){
                        str=str.substring(str.indexOf(":")+1);
                        if (str.length()>0){
                            str= str.trim();
                        }
                        processEntity.setSessionName(str);
                    } else if (str.startsWith("会话#")){
                        str=str.substring(str.indexOf(":")+1);
                        if (str.length()>0){
                            str= str.trim();
                        }
                        processEntity.setSession(str);
                    } else if (str.startsWith("内存使用")){
                        str=str.substring(str.indexOf(":")+1);
                        if (str.length()>0){
                            str= str.trim();
                        }
                        processEntity.setUsedMemory(str);
                    } else if (str.startsWith("用户名")){
                        str=str.substring(str.indexOf(":")+1);
                        if (str.length()>0){
                            str= str.trim();
                        }
                        processEntity.setUserName(str);
                    } else if (str.startsWith("CPU 时间")){
                        str=str.substring(str.indexOf(":")+1);
                        if (str.length()>0){
                            str= str.trim();
                        }
                        processEntity.setCpuTime(str);
                        list.add(processEntity);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取信息失败！");
        } finally {
            release(br,isr,p);
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
            release(br,isr,p);
        }
        return list;
    }
    /****************************************************************** /7.软件（wmic方式，基于msi的软件） ******************************************************************/


    /****************************************************************** /8.IPC连接 ******************************************************************/
    //------------------ 连接
    public static boolean connectIPC(String address,String userName,String password) {
        //net use \\192.168.1.22\ipc$ "gofar" /user:"cad22-win7-u"
        String cmdStr = "net use \\\\"+address+"\\ipc$ \""+password+"\" /user:\""+userName+"\"";

        boolean connectFlag = false;

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                //System.out.println(str);
                if(str.startsWith("命令成功完成")){
                    connectFlag = true;
                }
            }
            //System.out.println("连接成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connectFlag;
    }

    //------------------ 断开连接
    public static boolean delConnectIPC(String address) {
        //net use \\192.168.1.22\ipc$ /del
        String cmdStr = "net use \\\\"+address+"\\ipc$ /del";

        boolean closeConnectFlag = false;

        Process p = null;
        String str = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(cmdStr);
            isr = new InputStreamReader(p.getInputStream(), "GBK");
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                //System.out.println(str);
                if(str.startsWith("ipc$ 已经删除。")){
                    closeConnectFlag = true;
                }
            }
            //System.out.println("断开连接成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return closeConnectFlag;
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
            release(br,isr,p);
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
            release(br,isr,p);
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
            release(br,isr,p);
        }
        return softwareInfo;
    }
    /****************************************************************** /9.软件（reg方式） ******************************************************************/


    /****************************************************************** /10.环境变量 ******************************************************************/
    /**
     * 获取所有环境变量信息
     * @param address       远程机器地址
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    public static List<Map<String, String>> getEnvironment(String address,String userName,String password) {
        String[] cmdStr = { "cmd", "/C", "wmic /node:\""+address+"\" /user:\""+userName+"\" /password:\""+password+"\"  ENVIRONMENT get /value" };
        int flag = 8;  //信息列数

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Integer index = 1;
        Process p = null;
        String str = null;
        String[] arrStr = new String[2];
        Map<String, String> map = new HashMap<String, String>();
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
                        map = new HashMap<String, String>();
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
            release(br,isr,p);
        }
        return list;
    }
    /****************************************************************** /10.环境变量 ******************************************************************/


    /****************************************************************** 11.ping ******************************************************************/
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
                //System.out.println(str);
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
            release(br,isr,p);
        }
        return flag;
    }
    /****************************************************************** /11.ping ******************************************************************/

    /****************************************************************** 资源释放 ******************************************************************/
    public static void release(BufferedReader br,InputStreamReader isr,Process p){
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
    /****************************************************************** /资源释放 ******************************************************************/

}
