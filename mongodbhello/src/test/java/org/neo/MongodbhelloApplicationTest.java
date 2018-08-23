package org.neo;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo.dao.UserDao;
import org.neo.dao.UserRepository;
import org.neo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongodbhelloApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDao userDao;

    @Test
    public void testSave(){
        List<User> users = new ArrayList<>();
        users.add(new User("1", "wukong", 500, "dasheng"));
        users.add(new User("2", "bajie", 800, "tianpeng"));
        users.add(new User("3", "shaseng", 666, "shaheshang"));
        users.add(new User("4", "tangseng", 999, "shengseng"));

        /*List<User> saveAll = userRepository.saveAll(users);
        System.out.println(saveAll);

        User saveUser = userRepository.save(new User("5", "test", 5, "test"));
        System.out.println(saveUser);*/

        userDao.insertAll(users);
        userDao.save(new User("5", "test", 5, "test"));
    }

    @Test
    public void testFindAll(){
        /*Sort.Order order = new Sort.Order(Sort.Direction.DESC, "age");
        List<User> all = userRepository.findAll(new Sort(order));*/

        List<User> all = userDao.findAll();
        System.out.println(JSON.toJSONString(all));
    }

    @Test
    public void testFindByUsername(){
        /*User wukong = userRepository.findByUsername("wukong");*/

        User wukong = userDao.findByUsername("wukong");
        System.out.println(JSON.toJSONString(wukong));
    }
}
