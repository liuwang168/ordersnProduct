package com.hb.ordersn.api;

import javax.annotation.Resource;

/**
 * @ClassName OrderSnProduct
 * @Description 订单号生成接口
 * @Auther alex168
 * @Date 2020/10/28
 **/
public interface OrderSnProduct {
    /*
     * @Author alex168
     * @Description 获取订单号
     * @Date 2020/10/28
     * @Param  业务code，例：支付订单（1），餐消订单（2），采购订单（3）
     * @return 
     **/
    public String getOrderSn(String bussCode,String tableCode);

}
