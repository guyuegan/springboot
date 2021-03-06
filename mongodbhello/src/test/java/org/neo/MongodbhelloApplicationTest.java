package org.neo;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo.dao.UserDao;
import org.neo.dao.UserRepository;
import org.neo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongodbhelloApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDao userDao;

    @Test
    public void testSaveByRepository() {
        //不指定id会自动生成
        User save = userRepository.save(new User(null, "matrix", 10000, "robot"));
        System.out.println(JSON.toJSONString(save));
    }

    @Test
    public void testFindByNameRepository() {
        User bajie = userRepository.findByUsername("bajie");
        System.out.println(JSON.toJSONString(bajie));
    }


    @Test
    public void testSave() {
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
    public void testFindAll() {
        /*Sort.Order order = new Sort.Order(Sort.Direction.DESC, "age");
        List<User> all = userRepository.findAll(new Sort(order));*/

        List<User> all = userDao.findAll();
        all.forEach((u) -> System.out.println(JSON.toJSONString(u)));
    }

    @Test
    public void testFindByUsername() {
        /*User wukong = userRepository.findByUsername("wukong");*/

        User wukong = userDao.findByUsername("wukong");
        System.out.println(JSON.toJSONString(wukong));
    }

    @Test
    public void testUpate() {
        User test = userDao.findByUsername("test");
        if (test != null) {
            test.setUsername("xiaobai");
        }
        userDao.save(test);
    }

    @Test
    public void testDel() {
        User neo = userDao.findByUsername("matrix");
        if (neo != null) {
            DeleteResult del = userDao.del(neo.getId());
            System.out.println(JSON.toJSONString(del));
        }
    }

    @Test
    public void testPage() {
        List<User> page = userDao.findPage(1, 2);
        page.forEach((u) -> System.out.println(JSON.toJSONString(u)));
    }

    @Test
    public void testRegex() {
        List<User> sen = userDao.findByUsernameAndAge("sen", 555, 888);
        sen.forEach((u) -> System.out.println(JSON.toJSONString(u)));
    }

    @Test
    public void testSort(){
        List<User> users = userDao.findByAgeMutilWith(1, 1000);
        users.forEach((u) -> System.out.println(JSON.toJSONString(u)));
    }

    @Test
    public void testSort02(){
        List<User> users = userDao.findByAgeOrderList(1, 1000);
        users.forEach((u) -> System.out.println(JSON.toJSONString(u)));
    }

    @Test
    public void testConverter(){
        userDao.save(new User(null, "niumowang", 777, "niuniu"));
    }

    @Test
    public void testSetField(){
        try {

            List<User.Ability> abilities = Arrays.asList(
                    new User.Ability[]{
                            new User().new Ability("attack", 88),
                            new User().new Ability("happy", 88)
                    }
            );
            UpdateResult updateResult = userDao.setField("username", "^niu", "abilities", abilities);
            System.out.println(JSON.toJSONString(updateResult));

            /*UpdateResult updateResult = userDao.setField("username", "^hon", "role", "monster");
            System.out.println(JSON.toJSONString(updateResult));*/

            /* Date birth = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1988-08-08 08:08:08");
            UpdateResult updateResult = userDao.setField("username", "^ba", "birth", birth);
            System.out.println(JSON.toJSONString(updateResult));*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindBetweenTime(){
        try {
            /**
             * mongoTemplate生成的shell:
             * { "birth" : { "$gte" : { "$date" : 834012366000 }, "$lte" : { "$date" : 902534888000 } } }
             */
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = sdf.parse("1993-03-03 11:03:03");//取库中1993-03-03 03:03:03数据
            Date endTime = sdf.parse("1998-08-08 16:08:08");//取库中1998-08-08 08:08:08数据
            List<User> betweenTime = userDao.findBetweenTime(startTime, endTime);
            //jdk1.8新写法
            betweenTime.forEach((u) -> System.out.println(JSON.toJSONString(u)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findBetweenTimeStr() {
        String startTime = "2017-03-03 11:03:03";
        String endTime = "2019-08-08 16:08:08";
        List<User> betweenTime = userDao.findBetweenTimeStr(startTime, endTime);
        //jdk1.8新写法
        betweenTime.forEach((u) -> System.out.println(JSON.toJSONString(u)));
    }

    @Test
    public void testCountByRole(){
        AggregationResults<Document> countByRole = userDao.getRoleCount();
        countByRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void getRoleCountDesc(){
        AggregationResults<Document> countByRole = userDao.getRoleCountDesc();
        countByRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void getRoleMaxCount(){
        AggregationResults<Document> countByRole = userDao.getRoleCountMax();
        countByRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void getCountBetweenAbility(){
        AggregationResults<Document> countByRole = userDao.getCountBetweenAbility("attack", 60, 100);
        countByRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void getCountNotBetweenAbility(){
        AggregationResults<Document> countByRole = userDao.getCountNotBetweenAbility("attack", 30, 60);
        countByRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void getSummaryAbilityOfRole(){
        AggregationResults<Document> avgAbilityOfRole = userDao.getSummaryAbilityOfStudent("student", "attack", "avg");
        System.out.println("avg:");
        avgAbilityOfRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));

        AggregationResults<Document> sumAbilityOfRole = userDao.getSummaryAbilityOfStudent("student", "attack", "sum");
        System.out.println("sum:");
        sumAbilityOfRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));

        AggregationResults<Document> maxAbilityOfRole = userDao.getSummaryAbilityOfStudent("student", "attack", "max");
        System.out.println("max:");
        maxAbilityOfRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));

        AggregationResults<Document> minAbilityOfRole = userDao.getSummaryAbilityOfStudent("student", "attack", "min");
        System.out.println("min:");
        minAbilityOfRole.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void getSummaryAbility(){
        AggregationResults<Document> summaryAbility = userDao.getSummaryAbility();
        summaryAbility.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void getSummarySameBirth(){
        AggregationResults<Document> summarySameBirth = userDao.getSummarySameBirth();
        summarySameBirth.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void callProcess(){
        Object processResult = userDao.callProcess();
        System.out.println(processResult);
    }

    @Test
    public void findAllWithProject(){
        List<User> allWithProject = userDao.findAllWithProject();
        allWithProject.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void findByAgeWithProject(){
        List<User> allWithProject = userDao.findByAgeWithProject(666, 1000);
        allWithProject.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }

    @Test
    public void findByNeUsername(){
        List<User> neUsername = userDao.findByNeUsername("liuernihou");
        neUsername.forEach((obj) -> System.out.println(JSON.toJSONString(obj)));
    }
}