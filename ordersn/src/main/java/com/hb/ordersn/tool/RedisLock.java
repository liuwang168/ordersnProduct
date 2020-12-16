package com.hb.ordersn.tool;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisLock
 * @Description 获取reids分布式锁，只适合在同一地域，如果跨地域会存在时差的问题
 * todo 对于夸时区问题建议不要共享redis集群
 * @Author alex168
 * @Date 2020/12/15
 **/
public class RedisLock {
    static String REDIS_LOCK="Order_redisLock_ComputerNo";

    static long TIME_OUT=60*1000;

    public static boolean  getlock(RedisTemplate stringRedisTemplate)
    {
        long lockTime= System.currentTimeMillis()+TIME_OUT;
        if(stringRedisTemplate.opsForValue().setIfAbsent(REDIS_LOCK,String.valueOf(lockTime)))
        {
            stringRedisTemplate.expire(REDIS_LOCK,lockTime,TimeUnit.MILLISECONDS);
            return true;
        }

        //获取锁的值
        String currentLockValue =  String.valueOf(stringRedisTemplate.opsForValue().get(REDIS_LOCK));
        //锁的值小于当前时则锁已过期
        if (!StringUtils.isEmpty(currentLockValue) && Long.parseLong(currentLockValue) < System.currentTimeMillis()) {
            //getAndSet线程安全所以只会有一个线程重新设置锁的新值
            String oldValue = String.valueOf(stringRedisTemplate.opsForValue().getAndSet(REDIS_LOCK, String.valueOf(lockTime)));
            //比较锁的getSet获取到的最近锁值和最开始获取到的锁值，如果不相等则证明锁已经被其他线程获取了。
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentLockValue)) {
                return true;
            }
        }
        return false;
    }
    //release lock
    public static boolean releaselock(RedisTemplate stringRedisTemplate)
    {
        return stringRedisTemplate.delete(REDIS_LOCK);
    }
}
