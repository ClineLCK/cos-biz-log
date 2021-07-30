package com.coco.terminal.cocobizlog.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author future
 */
@Configuration
@EnableCaching
@Profile(value = {"prod", "test"})
@Slf4j
public class RedisTestAndProdConfiguration extends CachingConfigurerSupport {

    @Value("${spring.redis.default-expire-time}")
    private Integer defaultExpireTime;


    /**
     * 初始化 RedisTemplate
     *
     * @param clusterNodes
     * @param redirects    在群集上执行命令时重定向的最大数量。
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(@Value("${spring.redis.cluster.nodes}") String clusterNodes,
                                                       @Value("${spring.redis.cluster.max-redirects}") int redirects) {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(connectionFactory(getClusterConfiguration(clusterNodes, redirects)));
        setSerializer(template);
        log.info("redis use test or prod profiles");
        return template;
    }

    /**
     * Redis Cluster参数配置
     *
     * @param clusterNodes
     * @return
     */
    public RedisClusterConfiguration getClusterConfiguration(String clusterNodes, int redirects) {
        Map<String, Object> source = new HashMap<String, Object>();
        source.put("spring.redis.cluster.nodes", clusterNodes);
        source.put("spring.redis.cluster.max-redirects", redirects);
        return new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
    }

    /**
     * 连接池设置
     *
     * @param configuration
     * @return
     */
    private RedisConnectionFactory connectionFactory(RedisClusterConfiguration configuration) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    /**
     * 序列化工具
     * 使用 Spring 提供的序列化工具替换 Java 原生的序列化工具
     *
     * @param template
     */
    private void setSerializer(RedisTemplate template) {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
    }

    /**
     * 管理缓存
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        // 设置缓存过期时间
        rcm.setDefaultExpiration(defaultExpireTime);//秒
        return rcm;
    }

    /**
     * key的生成策略
     *
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName()).append("@");
                sb.append(method.getName()).append("(");
                if (params.length > 0) {
                    for (Object obj : params) {
                        String param = obj == null ? "null" : obj.toString();
                        sb.append(param).append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append(")");
                return sb.toString();
            }
        };
    }

}
