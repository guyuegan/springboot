package org.neo.dao;

import com.alibaba.fastjson.JSON;
import org.neo.entity.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PeopleDao {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void setAll(Map<String, String> peoples){
        redisTemplate.opsForValue().multiSet(peoples);
    }

    public void set(People people){
        redisTemplate.opsForValue().set(people.getName(), JSON.toJSONString(people));
    }

    public List<String> getAll(List<String> keys){
        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        return values;
    }

    public String get(String key){
        String value = redisTemplate.opsForValue().get(key);
        return value;
    }

    public String update(People people){
        //getAndSet 返回旧值，设置新值
        String oldVal = redisTemplate.opsForValue().getAndSet(people.getName(), JSON.toJSONString(people));
        return oldVal;
    }

    public long incr(long num){
        Long loginNum = redisTemplate.opsForValue().increment("loginNum", num);
        return loginNum;
    }
}
