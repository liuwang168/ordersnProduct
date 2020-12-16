package com.hb.ordersn.imp;

import com.hb.ordersn.api.NodeRegister;
import com.hb.ordersn.api.OrderSnProduct;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @ClassName OrderSnProduct
 * @Description orderSnproduct
 * @Auther alex168
 * @Date 2020/10/28
 **/
@Order(-1)
public class OrderSnProductImp implements OrderSnProduct {

    private NodeRegister nodeRegisterIml;

    public  OrderSnProductImp(NodeRegister nodeRegisterIml){
        this.nodeRegisterIml=nodeRegisterIml;
    }
  /*
     * @Author alex168
     * @Description 订单号生产规则:2位业务编码(81)+13时间戳（毫米）+1～2位机器标示码+5位线程id+1位随机+分表code
     * @Date 2020/10/28
     * @Param bussCode代表是那个业务(如：81代表餐消订单) tableCode 参数主要用来分表用(如：01)
     * @return 有序增长的数字id
     **/
    @Override
    public String getOrderSn(String bussCode,String tableCode) {
        Integer computNo=nodeRegisterIml.getBussComputNo();
        StringBuffer platformTag= new StringBuffer();
        String threadId="00000";
        String threadIdStr=String.valueOf(Thread.currentThread().getId());
        return platformTag.append(bussCode).append(System.currentTimeMillis()).
                append(threadIdStr.length()==5?threadIdStr:threadId.substring(0, (5-threadIdStr.length()))+threadIdStr).append(computNo).
                append(new Random().nextInt(8)+1).append(tableCode).toString();
    }



}
