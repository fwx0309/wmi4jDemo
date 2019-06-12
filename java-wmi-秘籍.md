# 一. 环境准备

1. windows要开启Remote Registry与Windows Management Instrumentation服务；

2. 修改安全策略（ ==这条一般不用改，可以查看确认一下==）：

   Administrative Tools>Local Security Policy>Local Policy>Security Policy>Network access: Sharing and security model for local accounts。修改为Classic（经典）

3. 防火墙策略：

   1. 新增入栈规则：添加24158端口，规则命名为24158；

   2. 允许程序通过Windows 防火墙通信：在“家庭/工作（专用）”、“域”（添加域的机器开启）中开启下列允许：

      > * 24158；
      > * Windows Management Instrumentation(WMI)；
      > * 远程服务管理；
      > * 文件和打印机共享；
      > * 135（server2003）

4. 修改注册表权限：

   1. 需要修改regedit中HKEY_CLASSES_ROOT\CLSID\{76A64158-CB41-11D1-8B02-00600806D9B6}的权限，windows2008不再给Administrators完全控制权。（==目前不需要修改这个了==）

      > - 以管理员身份登录到目标远程主机；
      > - 运行程序Regedit；
      > - 如果您被要求允许Regedit程序对计算机进行更改，请单击"Yes"；
      > - 导航到注册表项 HKEY_CLASSES_ROOT\CLSID\\{76A64158-CB41-11D1-8B02-00600806D9B6}
      > - 右键单击此项目并选择“权限”;
      > - 点击“高级”；
      > - 选择“Owner”选项卡；
      > - 在“将所有者更改为...”框中，选择显示您当前登录的账户；
      > - 单击“OK”
      > - 单击“OK”
      > - 再次右击注册表项并选择“权限”；
      > - 选择“Administrator”组；
      > - 勾选全部；

   2. HKEY_LOCAL_MACHINE/SOFTWARE/Microsoft/Windows/CurrentVersion/Policies/System

      - create or modify 32-bit DWORD: LocalAccountTokenFilterPolicy
      - set the value to: 1



# 二. 固定端口（现在不需要改）

> 参考：https://blog.csdn.net/sarahcla/article/details/51814410

方法一：修改配置

1. net stop winmgmt 

2. winmgmt /standalonehost 

3. net start winmgmt 

   执行后，WMI服务的可执行文件路径变成了：C:\Windows\system32\svchost.exe -k winmgmt 任务管理器中，可以看到其对应的独立进程。

   

# 三. ad域

1. 新建：运行—dcpromo；

   > 参考：http://www.sohu.com/a/166462698_99973431

2. 客户机加入域：修改客户机首选DNS为域服务器的IP，在计算机信息中“更改设置”—计算机名—更改—域（填上域名）；

   - 管理（策略）：管理工具—组策略管理；

     > 参考：https://www.docin.com/p-1793179165.html

   - 开服务：计算机配置—策略—Windows设置—安全设置—系统服务；

   - 开端口：计算机配置—策略—管理模板：…--网络—网络连接—windows防火墙—标准配置文件/域配置文件；

3. 刷新客户机命令：gpupdate /force；

4. ==注意==：使用指定组策略，需要将机器移到改组中。



# 四. 命令

### 1. wmic

1. 远程格式：

   wmic /node:"**IP**" /user:"**userName**" /password:"**password**"

2. 服务：

   1. 获取所有服务：service get /value
   2. 运行服务：SERVICE where name="gfclient" call startservice
   3. 停止服务：SERVICE where name="gfclient" call stopservice
   4. 更改服务启动类型[Auto|Disabled|Manual]：SERVICE where name="tlntsvr" set startmode="Auto"

3. 进程：

   1. 获取所有进程：process get /value
   2. 关闭进程：
      - process 3216 call terminate
      - wmic process where processid="3216" delete 
      - process where name="java.exe" call terminate 
      - process where name="java.exe" delete 
   3. 创建进程：process call create "C:\Program Files (x86)\gf_client\release\soft\gf_cm.exe"

4. 环境变量：

   1. 所有：ENVIRONMENT get /value
   2. 系统path：ENVIRONMENT where "name='path' and username='<system>'"

5. 内存：

   1.  所有内存条的信息：memorychip get /value 
   2. 获取所有总内存大小：COMPUTERSYSTEM get TotalPhysicalMemory /value

6. CPU：cpu get /value

7. 磁盘：volume get /value

8. 计算机信息：computersystem get /value

9. 软件：PRODUCT get /value（仅基于msi的软件）

### 2. reg （需要使用IPC通讯）

1. 建立IPC通讯：net use \\192.168.1.22\ipc$ "gofar" /user:"cad22-win7-u"
2. 断开IPC通讯：net use \\192.168.1.22\ipc$ /del
3. 获取卸载软件列表：reg query \\\192.168.1.22\HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall
4. 获取卸载软件列表（64位系统需要获取这个路径下的信息和上面的合并）：reg query \\\\192.168.1.22\HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall
5. 获取详细软件的信息：reg query \\\\192.168.1.22\key

### 3. Windows命令

1. 获取系统信息：systeminfo /s 192.168.1.22 /u cad22-win7-u /p gofar /fo List
2. 获取所有进程信息：tasklist /s 192.168.1.22 /u cad22-win7-u /p gofar /V /FO LIST



# 五. 代码

### 1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fwx</groupId>
    <artifactId>wmi4jDemo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>cn.chenlichao</groupId>
            <artifactId>wmi4j</artifactId>
            <version>0.9</version>
        </dependency>

        <dependency>
            <groupId>net.sf.json-lib</groupId>
            <artifactId>json-lib</artifactId>
            <version>2.4</version>
            <classifier>jdk15</classifier>
        </dependency>

        <!--https://mvnrepository.com/artifact/org.hyperic/sigar -->
        <dependency>
            <groupId>org.fusesource</groupId>
            <artifactId>sigar</artifactId>
            <version>1.6.4</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
</project>
```

### 2. 实体类

- com.pojo.SoftwareInfo.java
- com.pojo.SystemInfo.java
- com.pojo.ClientConnectData.java
- com.pojo.ProcessEntity.java

### 3. 工具类

- com.wmic_utils.WmicUtils.java

### 4. 测试类

- com.wmic_utils.WmicUtilsTest.java