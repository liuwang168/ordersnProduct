package com.hb.ordersn.register;

import com.hb.ordersn.api.NodeRegister;
import com.hb.ordersn.tool.Common;
import com.hb.ordersn.tool.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;

import java.util.*;

/**
 * @ClassName NodeRegister
 * @Description 注册节点
 * @Auther alex168
 * @Date 2020/10/28
 **/
@Order(-1)
public class NodeRegisterImp implements NodeRegister {

    private String currentEnv;

    private RedisTemplate redisTemplate;
    //本地ip地址
    private String localIp;

    volatile int computNo=0;



    @Autowired
    public NodeRegisterImp(@Qualifier("redisTemplate")RedisTemplate redisTemplate, @Value("${spring.application.name}")String currentEnv)
    {
        this.redisTemplate=redisTemplate;
        this.currentEnv=currentEnv;
        //获取本地ip
        localIp= Common.getMacAddress();
        registerToRedis();
    }

    /*
     * @Author alex168
     * @Description 注册当前节点的机器码
     * @Date 2020/10/28
     * @Param 
     * @return 
     **/
    public void registerToRedis(){
        boolean flag=false;
        try {
            for(;;)
            {
                if (RedisLock.getlock(redisTemplate)) {
                    flag=true;
                    break;
                }
                else {
                    Thread.sleep(1000);
                }
            }
            Map<String,Object> map = redisTemplate.boundHashOps(currentEnv).entries();
            if (null != map && map.size() > 0) {
                computNo = getComputNo(map);
            } else {
                computNo = 1;
            }
            if(computNo>99)
            {
                throw new Exception("computNo count exception");
            }
            redisTemplate.boundHashOps(currentEnv).put(localIp + ":" + Common.getPort(), computNo);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (flag) {
                RedisLock.releaselock(redisTemplate);
            }
        }
    }

    /*
     * @Author alex168
     * @Description 获取节点序号
     * @Date 2020/12/15
     * @Param 
     * @return 
     **/
    public int getComputNo(Map<String,Object> map){
        int beforNum=0;
        int computNowaNo=0;
        List<Integer> valueList= new ArrayList<>();
        for(Map.Entry<String,Object> entry :map.entrySet())
        {
            if(!Common.telnetClientBoolean(entry.getKey(),1000)){
                redisTemplate.boundHashOps(currentEnv).delete(entry.getKey());
            }else {
                valueList.add(Integer.valueOf(String.valueOf(entry.getValue())));
            }

        }
        if(valueList.size()==0)
        {
            return 1;

        }
        Collections.sort(valueList);
        for(int i=0;i<valueList.size();i++)
        {
            if(beforNum+1!=valueList.get(i))
            {
                computNowaNo=beforNum+1;
            }
            beforNum=valueList.get(i);
        }

        if(computNowaNo==0)
        {
            computNowaNo=valueList.size()+1;
        }
        return computNowaNo;
    }
    /*
    * @Author alex168
    * @Description 获取订单号
    * @Date 2020/10/28
    * @Param
    * @return
    **/
    @Override
    public Integer getBussComputNo()
    {
        if(computNo != 0L)
        {
            return computNo;
        }
        return Integer.valueOf(String.valueOf(redisTemplate.boundHashOps(currentEnv).get(localIp)));
    }
}
