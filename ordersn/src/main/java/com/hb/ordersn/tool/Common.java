package com.hb.ordersn.tool;

import com.hb.ordersn.imp.OrderSnProductImp;
import com.sun.istack.internal.NotNull;
import org.apache.commons.net.telnet.TelnetClient;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Random;

/**
 * @ClassName Common
 * @Description 静态变量
 * @Auther alex168
 * @Date 2020/10/28
 **/
public class Common {
    static String localhostName="127.0.0.1";
    /*
     * @Author alex168
     * @Description 获取本地ip
     * @Date 2020/10/28
     * @Param 
     * @return 
     **/
    public static String getMacAddress()
    {
        String hostAddress= null;
        try {

            InetAddress a=Inet4Address.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(a);
            Enumeration<InetAddress> enumeration=network.getInetAddresses();
            while (enumeration.hasMoreElements())
            {
                InetAddress address= enumeration.nextElement();
                hostAddress=address.getHostAddress();
                if(address instanceof Inet4Address && !localhostName.equals(hostAddress)){
                    return hostAddress;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostAddress;
    }

    /*
     * @Author alex168
     * @Description 测试telnet 机器端口的连通性
     * @Date 2020/12/15
     * @Param
     * @return
     **/
    public static  boolean telnetClientBoolean(@NotNull String strHostAndPort,@NotNull int timeout)
    {

        String[] hostAndPort=strHostAndPort.split(":");
        if(hostAndPort.length!=2)
        {
            return false;
        }
        String hostName=hostAndPort[0];
        int port =Integer.valueOf(hostAndPort[1]);
        Socket socket = new Socket();
        boolean isConnected = false;
        try {
            socket.connect(new InetSocketAddress(hostName, port), timeout); // 建立连接
            isConnected = socket.isConnected(); // 通过现有方法查看连通状态
        } catch (IOException e) {
            return false;
        }finally{
            try {
                socket.close();   // 关闭连接
            } catch (IOException e) {
                System.out.println("false");
            }
        }
        return isConnected;
    }

    /*
     * @Author alex168
     * @Description 获取当前服务端口
     * @Date 2020/12/15
     * @Param 
     * @return 
     **/
    public static String getPort()
    {
        String listenAddr = null;
        String port = null;
        try {
            Context ctx = new InitialContext();
            MBeanServer tMBeanServer = (MBeanServer) ctx
                    .lookup("java:comp/env/jmx/runtime");
            ObjectName tObjectName = new ObjectName(
                    "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");
            ObjectName serverrt = (ObjectName) tMBeanServer.getAttribute(
                    tObjectName, "ServerRuntime");
            port = String.valueOf(tMBeanServer.getAttribute(serverrt,
                    "ListenPort"));
            listenAddr = (String) tMBeanServer.getAttribute(serverrt,
                    "ListenAddress");
            String[] tempAddr = listenAddr.split("/");
            if (tempAddr.length == 1) {
                listenAddr = tempAddr[0];
            } else if (tempAddr[tempAddr.length - 1].trim().length() != 0) {
                listenAddr = tempAddr[tempAddr.length - 1];
            } else if (tempAddr.length > 2) {
                listenAddr = tempAddr[tempAddr.length - 2];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return  port;

    }

    public static void main(String arg[]){
        StringBuffer platformTag= new StringBuffer();
        String threadId="00000";
        String threadIdStr=String.valueOf(Thread.currentThread().getId());
        platformTag.append("81").append(System.currentTimeMillis()).
                append(threadIdStr.length()==5?threadIdStr:threadId.substring(0, (5-threadIdStr.length()))).append(1).
                append(new Random().nextInt(8)+1).append("01").toString();

        System.out.println(platformTag+":"+threadId.length());

    }
}
