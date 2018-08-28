package org.neo.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.neo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
        Criteria idCriteria = Criteria.where("_id").is(id);
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

    //多字段排序[mongoTemplate]: 一个字段一个Sort, 即多个with
    public List<User> findByAgeMutilWith(int minAge, int maxAge){
        Sort ageOrd = Sort.by(new Sort.Order(Sort.Direction.DESC, "age"));
        Sort usernameOrd = Sort.by(new Sort.Order(Sort.Direction.ASC, "username"));
        Criteria criteria = Criteria.where("age").gt(minAge).lt(maxAge);
        List<User> users = mongoTemplate.find(new Query(criteria).with(ageOrd).with(usernameOrd), User.class);
        return users;
    }

    //多字段排序[JPA]: 多字段一个Sort, 一个with
    public List<User> findByAgeOrderList(int minAge, int maxAge){
        Sort ordLs = Sort.by(
                Arrays.asList(new Sort.Order[]{
                        new Sort.Order(Sort.Direction.DESC, "age"),
                        new Sort.Order(Sort.Direction.ASC, "username")})
        );
        Criteria criteria = Criteria.where("age").gt(minAge).lt(maxAge);
        List<User> users = mongoTemplate.find(new Query(criteria).with(ordLs), User.class);
        return users;
    }

    //插入字段, 如果birth字段在库中格式是ISODate, 代码中用Date, 如果库中String， 代码中也用String
    public UpdateResult setField(String filterKey, String fileterVal, String field, Object fieldVal) throws Exception{
        Query usernameQry = new Query(Criteria.where(filterKey).regex(fileterVal));
        Update birthUp = new Update().set(field, fieldVal);
        UpdateResult birth = mongoTemplate.upsert(usernameQry, birthUp, User.class);
        return birth;
    }

    public List<User> findBetweenTime(Date startTime, Date endTime){
        /**
         * mongoTemplate生成的shell: { "birth" : { "$gte" : { "$date" : 834012366000 }, "$lte" : { "$date" : 902534888000 } } }
         */

        Criteria criteria = new Criteria();
        criteria.andOperator(
//                Criteria.where("birth").gte(startTime)
                Criteria.where("birth").lte(endTime)
        );
        Query birthQry = new Query(criteria);
        List<User> users = mongoTemplate.find(birthQry, User.class);
        return users;
    }

    public static void main(String[] args) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = sdf.parse("1955-05-05 05:05:05");
        System.out.println(parse);

    }
}
