package org.neo;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.DeleteResult;
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
    public void testSaveByRepository(){
        //不指定id会自动生成
        User save = userRepository.save(new User(null, "matrix", 10000, "robot"));
        System.out.println(JSON.toJSONString(save));
    }

    @Test
    public void testFindByNameRepository(){
        User bajie = userRepository.findByUsername("bajie");
        System.out.println(JSON.toJSONString(bajie));
    }



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

    @Test
    public void testUpate(){
        User test = userDao.findByUsername("test");
        if (test != null){
            test.setUsername("xiaobai");
        }
        userDao.save(test);
    }

    @Test
    public void testDel(){
        User neo = userDao.findByUsername("matrix");
        if (neo != null){
            DeleteResult del = userDao.del(neo.getId());
            System.out.println(JSON.toJSONString(del));
        }
    }

}