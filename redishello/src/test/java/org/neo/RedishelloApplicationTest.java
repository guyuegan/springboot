package org.neo;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo.dao.PeopleDao;
import org.neo.entity.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedishelloApplicationTest {

    @Autowired
    PeopleDao peopleDao;

    @Test
    public void testSet(){
        People hua = new People("liudehua", 60, "ice rain");
        People you = new People("zhangxueyou", 50, "kiss goodbye");
        People ming = new People("liming", 40, "sweet");
        People cheng = new People("guofucheng", 60, "ice war");

        Map hashMap = new HashMap<>();
        hashMap.put(hua.getName(), JSON.toJSONString(hua));
        hashMap.put(you.getName(), JSON.toJSONString(you));
        hashMap.put(ming.getName(), JSON.toJSONString(ming));
        hashMap.put(cheng.getName(), JSON.toJSONString(cheng));

        peopleDao.setAll(hashMap);

        People people = new People("test", 999, "test");
        peopleDao.set(people);
    }

    @Test
    public void testGet(){
        List<String> keys = new ArrayList<>();
        keys.addAll(Arrays.asList(
                "liudehua",
                "zhangxueyou",
                "liming",
                "guofucheng"));

        List<String> values = peopleDao.getAll(keys);
        System.out.println(JSON.toJSONString(values));

        String liming = peopleDao.get("liming");
        System.out.println(JSON.toJSONString(liming));
    }

    @Test
    public void testUpdate(){
        People people = new People("guofucheng", 17, "cherry dance");
        String oldVal = peopleDao.update(people);
        System.out.println(oldVal);
    }

    @Test
    public void testIncr(){
        System.out.println(peopleDao.incr(1));
    }
}
