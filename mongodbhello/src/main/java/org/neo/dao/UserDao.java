package org.neo.dao;

import com.mongodb.client.result.DeleteResult;
import org.neo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
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
}
