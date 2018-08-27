package org.neo.dao;

import com.mongodb.client.result.DeleteResult;
import org.neo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(User user){
         mongoTemplate.save(user);
    }

    public void insertAll(List<User> all){
        mongoTemplate.insertAll(all);
    }

    public List<User> findAll(){
        List<User> all = mongoTemplate.findAll(User.class);
        return all;
    }

    public User findByUsername(String username){
        Criteria usernameCriteria = Criteria.where("username").is(username);
        User one = mongoTemplate.findOne(new Query(usernameCriteria), User.class);
        return one;
    }

    public DeleteResult del(String id){
        /**
         * 这里"id"也可以，"_id"也可以
         * 主要是要指定remove的第二个参数：User.class
         */
        Criteria idCriteria = Criteria.where("id").is(id);
        DeleteResult remove = mongoTemplate.remove(new Query(idCriteria), User.class);
        return remove;
    }

    //分页查询
    public List<User> findPage(int offset, int pageSize){
        Query query = new Query().skip(offset).limit(pageSize);
        List<User> all = mongoTemplate.find(query, User.class);
        return all;
    }

    //多条件查询
    public List<User> findByUsernameAndAge(String username, int minAge, int maxAge){
        Criteria criteria = Criteria.where("username").regex(username).and("age").gt(minAge).lt(maxAge);
        List<User> users = mongoTemplate.find(new Query(criteria), User.class);
        return users;
    }

    //排序
    public List<User> findByAge(int minAge, int maxAge){
        Sort orders = Sort.by(new Sort.Order(Sort.Direction.DESC, "age")).
                by(new Sort.Order(Sort.Direction.ASC, "username"));
        Criteria criteria = Criteria.where("age").gt(minAge).lt(maxAge);
        List<User> users = mongoTemplate.find(new Query(criteria).with(orders), User.class);
        return users;
    }
}
