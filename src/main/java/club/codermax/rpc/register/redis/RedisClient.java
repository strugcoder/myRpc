package club.codermax.rpc.register.redis;

import club.codermax.rpc.framework.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

public class RedisClient {

    private static Logger logger = LoggerFactory.getLogger(RedisClient.class);


    private static String addr = Configuration.getInstance().getAddress().split(":")[0];
    private static int port = Integer.parseInt(Configuration.getInstance().getAddress().split(":")[1]);


//    private static String addr = "101.200.48.218";
//    private static int port = 6379;

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 1024;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值8。
    private static int MAX_IDLE = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;

    private static int TIMEOUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;


    private static JedisPool jedisPool = null;


    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();

            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            //jedisPool = new JedisPool(config, addr, port, TIMEOUT);
            //需要认证
             jedisPool = new JedisPool(config, addr, port, TIMEOUT,"123456");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            logger.info(addr + " " + port);
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Set<String> getValues(String key) {
        Jedis jedis = getJedis();
        if (jedis == null) {
            throw new RuntimeException("jedis 获取失败: " + addr + ":" + port);
        }
        try {
            return jedis.smembers(key);
        }finally {
            jedis.close();
        }
    }


    public Set<String> getKeyBring(String key) {
        key += "*";
        Jedis jedis = getJedis();
        if (jedis == null) {
            throw new RuntimeException("jedis 获取失败: " + addr + ":" + port);
        }
        try {
            return jedis.keys(key);
        } finally {
            jedis.close();
        }
    }


    public void add2Set(String key, String value) {
        Jedis jedis = getJedis();
        if (jedis == null) {
            throw new RuntimeException("jedis 获取失败: " + addr + ":" + port);
        }
        try {
            jedis.sadd(key, value);
        } finally {
            jedis.close();
        }
    }

}