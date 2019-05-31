package com.wmic_utils;

import com.pojo.SoftwareInfo;
import com.pojo.SystemInfo;
import org.junit.Test;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WmicUtilsTest {

    @Test
    public void printInfo(){
        System.out.println("******************************** 192.168.1.107(win7 x86 旗舰版) ********************************");
        dashBoard("192.168.1.107", "gofar_win7", "gofar");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("******************************** 192.168.1.22(win7 x64 旗舰版) ********************************");
        dashBoard("192.168.1.22", "cad22-win7-u", "gofar");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("******************************** 192.168.1.27(win7 x64 家庭版) ********************************");
        dashBoard("192.168.1.27", "20_lmt_199", "gofar");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("******************************** 192.168.1.10(win7 x64 企业版) ********************************");
        dashBoard("192.168.1.10", "administrator", "gofarlic");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("******************************** 192.168.1.135(win10 x64 企业版) ********************************");
        dashBoard("192.168.1.135", "gofar_win10", "gofar");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("******************************** 192.168.1.203(server2003 x64) ********************************");
        dashBoard("192.168.1.203", "Administrator", "gofar");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("******************************** 192.168.1.184(server2008 x64) ********************************");
        dashBoard("192.168.1.184", "Administrator", "gofar@2019");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("******************************** 192.168.1.20(server2012 x64) ********************************");
        dashBoard("192.168.1.20", "administrator", "gofar@2019");
        System.out.println();
        System.out.println();
        System.out.println();
    }


    public void dashBoard(String address,String userName,String passWord){
        //网络情况
        Boolean ping = WmicUtils.getPing(address);
        System.out.println("================== 网络情况 ==================");
        if(ping){
            System.out.println("网络通讯正常开始请求数据...");
        } else {
            System.out.println("网络通讯异常...");
            return;
        }

        //计算机信息
        SystemInfo systeminfo = WmicUtils.getSysteminfo(address, userName, passWord);
        System.out.println("================== 计算机名 ==================");
        System.out.println(systeminfo.getSysName());
        System.out.println();
        System.out.println("================== OS 名称 ==================");
        System.out.println(systeminfo.getOsName());
        System.out.println();
        System.out.println("================== 物理内存总量 ==================");
        System.out.println(systeminfo.getTotalPhysicalMemory());
        System.out.println();
        System.out.println("================== 可用的物理内存 ==================");
        System.out.println(systeminfo.getAvailablePhysicalMemory());
        System.out.println();
        System.out.println("================== 内存使用率 ==================");
        String memoryUsageRate = null;
        if(null!=systeminfo.getAvailablePhysicalMemory() && null!=systeminfo.getTotalPhysicalMemory()){
            String unUsed = systeminfo.getAvailablePhysicalMemory();
            String[] unUsedArr = unUsed.split(" ");
            String total = systeminfo.getTotalPhysicalMemory();
            String[] totalArr = total.split(" ");
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(0);
            memoryUsageRate = numberFormat.format((Float.valueOf(totalArr[0].replaceAll(",",""))-Float.valueOf(unUsedArr[0].replaceAll(",","")))/Float.valueOf(totalArr[0].replaceAll(",","")) * 100)+"%";
        }
        System.out.println(memoryUsageRate);
        System.out.println();

        //cpu
        List<Map<String, Object>> cpuInfos = WmicUtils.getCpuInfos(address, userName, passWord);
        System.out.println("================== cpu ==================");
        System.out.println("CPU个数："+cpuInfos.size());
        for (Map<String, Object> cpuInfo : cpuInfos) {
            System.out.println(cpuInfo);
            System.out.println("CPU"+cpuInfo.get("Name")+"使用率："+cpuInfo.get("LoadPercentage")+"%");
        }

        //所有软件
        System.out.println("================== 所有软件 ==================");
        WmicUtils.connectIPC(address, userName, passWord);
        List<SoftwareInfo> allProducts = WmicUtils.getAllProducts(address);

        for (SoftwareInfo product : allProducts) {
            System.out.println(product);
        }
        System.out.println();
        WmicUtils.delConnectIPC(address);

        //进程信息
        System.out.println("================== 进程信息 ==================");
        List<Map<String, Object>> processes = WmicUtils.getProcesses(address, userName, passWord);

        for (Map<String, Object> process : processes) {
            System.out.println(process);
            System.out.println("进程："+process.get("Caption")+"，使用内存："+Math.round(Float.valueOf(process.get("WorkingSetSize").toString())/1024/1024)+" MB");
        }
        System.out.println();

        //服务信息
        System.out.println("================== 服务信息 ==================");
        /*List<Map<String, Object>> services = WmicUtils.getServices(address, userName, passWord);
        for (Map<String, Object> service : services) {
            System.out.println(service);
        }*/
    }

    /**
     * 测试获取所有服务信息
     */
    @Test
    public void getServices(){
        List<Map<String, Object>> services = WmicUtils.getServices("192.168.1.22", "cad22-win7-u", "gofar");
        for (Map<String, Object> service : services) {
            System.out.println(service);
        }
    }

    /**
     * 测试获取所有内存条的信息
     * Capacity：内存大小
     */
    @Test
    public void getMemorychips(){
        List<Map<String, Object>> memorychips = WmicUtils.getMemorychips("192.168.1.22", "cad22-win7-u", "gofar");
        Long countSize = 0L;

        for (Map<String, Object> memorychip : memorychips) {
            countSize += Long.valueOf(memorychip.get("Capacity").toString());
            System.out.println(memorychip);
        }

        //总内存大小
        System.out.println("总内存大小:"+countSize/1024/1024+"M");
        System.out.println("总内存大小:"+countSize);
    }

    /**
     * 测试获取内存大小
     */
    @Test
    public void getMemoryCountSize(){
        Long memorySizes = WmicUtils.getMemoryCountSize("192.168.1.22", "cad22-win7-u", "gofar");

        System.out.println("总内存大小:"+memorySizes/1024/1024+"M");
    }

    /**
     * 测试获CPU信息
     * Name：CPU名字
     * LoadPercentage:使用率
     */
    @Test
    public void getCpuInfo(){
        List<Map<String, Object>> cpuInfos = WmicUtils.getCpuInfos("192.168.1.22", "cad22-win7-u", "gofar");

        for (Map<String, Object> cpuInfo : cpuInfos) {
            System.out.println(cpuInfo);
        }
    }

    /**
     * 测试获所有磁盘信息
     * Capacity：总空间
     * FreeSpace：剩余空间
     */
    @Test
    public void getDiskInfos(){
        List<Map<String, Object>> diskInfos = WmicUtils.getDiskInfos("192.168.1.22", "cad22-win7-u", "gofar");

        for (Map<String, Object> diskInfo : diskInfos) {
            System.out.println(diskInfo);
        }
    }

    /**
     * 测试获计算机信息
     * Name：计算机名
     * Model：算机制造商和型号
     */
    @Test
    public void getComputersystem(){
        Map<String, Object> computerSystem = WmicUtils.getComputerSystem("192.168.1.22", "cad22-win7-u", "gofar");

        System.out.println(computerSystem);
    }

    /**
     * 测试获计算机信息(Systeminfo)
     * Name：计算机名
     * Model：算机制造商和型号
     */
    @Test
    public void getSysteminfo(){
        SystemInfo systeminfo = WmicUtils.getSysteminfo("192.168.1.22", "cad22-win7-u", "gofar");
        System.out.println(systeminfo);
    }

    /**
     * 测试获所有进程信息
     * WorkingSetSize：内存使用
     */
    @Test
    public void getProcesses(){
        List<Map<String, Object>> processes = WmicUtils.getProcesses("192.168.1.22", "cad22-win7-u", "gofar");

        for (Map<String, Object> process : processes) {
            System.out.println(process);
        }
    }

    /**
     * 测试获所有软件信息
     */
    @Test
    public void getProducts(){
        List<Map<String, Object>> products = WmicUtils.getProducts("192.168.1.22", "cad22-win7-u", "gofar");

        for (Map<String, Object> product : products) {
            System.out.println(product);
        }
    }

    /**
     * 测试IPC连接
     */
    @Test
    public void connectIPC(){
        WmicUtils.connectIPC("192.168.1.22", "cad22-win7-u", "gofar");
    }

    /**
     * 测试断开IPC连接
     */
    @Test
    public void delConnectIPC(){
        WmicUtils.delConnectIPC("192.168.1.22");
    }

    /**
     * 测试获取所有软件
     */
    @Test
    public void getProductsReg(){
        //22服务器
        System.out.println("============================== 22服务器 ===============================");
        WmicUtils.connectIPC("192.168.1.22", "cad22-win7-u", "gofar");
        List<SoftwareInfo> allProducts = WmicUtils.getAllProducts("192.168.1.22");

        for (SoftwareInfo product : allProducts) {
            System.out.println(product);
        }
        WmicUtils.delConnectIPC("192.168.1.22");


        //27服务器
        System.out.println("============================== 27服务器 ===============================");
        WmicUtils.connectIPC("192.168.1.27", "20_lmt_199", "gofar");
        List<SoftwareInfo> allProducts1 = WmicUtils.getAllProducts("192.168.1.27");

        for (SoftwareInfo product : allProducts1) {
            System.out.println(product);
        }
        WmicUtils.delConnectIPC("192.168.1.27");
    }

    /**
     * 测试断开IPC连接
     */
    @Test
    public void getPing(){
        WmicUtils.getPing("192.168.1.22");
    }
}
