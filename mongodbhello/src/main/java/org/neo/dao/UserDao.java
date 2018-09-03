package org.neo.dao;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.neo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;

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

    /**
     * 限制返回的字段
     */
    public List<User> findAllWithProject(){
        BasicDBObject qryObj = new BasicDBObject();

        BasicDBObject fieldObj = new BasicDBObject();
        fieldObj.put("username", 1);
        fieldObj.put("age", 1);

        BasicQuery basicQuery = new BasicQuery(qryObj.toJson(), fieldObj.toJson());
        List<User> all = mongoTemplate.find(basicQuery, User.class);
        return all;
    }

    /**
     * 限制返回的字段
     */
    public List<User> findByAgeWithProject(int minAge, int maxAge){
        BasicDBObject qryObj = new BasicDBObject();
        qryObj.put("age", new BasicDBObject("$gte", minAge).append("$lte", maxAge));

        BasicDBObject fieldObj = new BasicDBObject("_id", 0);
        fieldObj.put("username", 1);
        fieldObj.put("age", 1);

        BasicQuery basicQuery = new BasicQuery(qryObj.toJson(), fieldObj.toJson());
        List<User> all = mongoTemplate.find(basicQuery, User.class);
        return all;
    }

    public User findByUsername(String username){
        Criteria usernameCriteria = Criteria.where("username").is(username);
        User one = mongoTemplate.findOne(new Query(usernameCriteria), User.class);
        return one;
    }

    /**
     * BasicQuery才有过滤字段功能？？
     * Document可以替代BasicDBOject
     */
    public List<User> findByNeUsername(String username){

        /*BasicDBObject filedObj = new BasicDBObject("_id", 0)
                .append("username", 1).append("age", 1);*/

        Document filed = new Document("_id", 0)
                .append("username", 1).append("age", 1);

        Criteria neUsernameCriteria = Criteria.where("username").ne(username);
        Query query = new BasicQuery(null, filed.toJson()).addCriteria(neUsernameCriteria);

        List<User> users = mongoTemplate.find(query, User.class);
        return users;
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
         * mongoTemplate生成的shell:
         * { "birth" : { "$gte" : { "$date" : 834012366000 }, "$lte" : { "$date" : 902534888000 } } }
         */

        /*Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("birth").gt(startTime),
                Criteria.where("birth").lt(endTime)
        );*/
        Query birthQry = new Query(Criteria.where("birth").gte(startTime).lte(endTime));
        List<User> users = mongoTemplate.find(birthQry, User.class, "user");
        return users;
    }

    /**
     * study.test_date中文档
     */
    public List<User> findBetweenTimeStr(String startTime, String endTime){

        Query birthQry = new Query(Criteria.where("birth").gte(startTime).lte(endTime));
        List<User> users = mongoTemplate.find(birthQry, User.class, "test_date");
        return users;
    }

    /**
     * return user.getMappedResults()，用List<Map>接收查询结果，如果值为空，会被过滤
     *
     * {"_id":"monster","count":2}
     * {"_id":"master","count":1}
     * {"count":1} //这里应该是{"_id":null,"count":1}
     * {"_id":"student","count":3}
     */
    public AggregationResults<Document> getRoleCount(){
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("role").count().as("count"));
        AggregationResults<Document> user = mongoTemplate.aggregate(aggregation, "user", Document.class);
        return user;
    }

    public AggregationResults<Document> getRoleCountDesc(){
        List<AggregationOperation> countAndSort = Arrays.asList(
                new AggregationOperation[]{
                        Aggregation.group("role").count().as("count"),
                        Aggregation.sort(Sort.Direction.DESC, "count")
                }
        );
        AggregationResults<Document> user =
                mongoTemplate.aggregate(Aggregation.newAggregation(countAndSort), "user", Document.class);

        return user;
    }

    //取人数最多的role组
    public AggregationResults<Document> getRoleCountMax(){
        List<AggregationOperation> countAndSort = Arrays.asList(
                new AggregationOperation[]{
                        Aggregation.group("role").count().as("count"),
                        Aggregation.sort(Sort.Direction.DESC, "count"),
                        Aggregation.limit(1)
                }
        );
        AggregationResults<Document> aggregateResult =
                mongoTemplate.aggregate(Aggregation.newAggregation(countAndSort), "user", Document.class);

        return aggregateResult;
    }

    public AggregationResults<Document> getCountBetweenAbility(String item, int low, int high){
        List<AggregationOperation> aggregationOperations = Arrays.asList(
                new AggregationOperation[]{
                        Aggregation.match(Criteria.where("role").is("student")),
                        Aggregation.unwind("abilities"),
                        Aggregation.match(Criteria.where("abilities.item").is(item).and("abilities.value").gte(low).lte(high)),
                        Aggregation.group("role").count().as("count")
                }
        );
        AggregationResults<Document> aggregateResult =
                mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations), "user", Document.class);

        return aggregateResult;
    }

    /**
     * 注意orOperator的使用方式:
     * 聚合第三步也可以写成：Aggregation.match(
     *                        Criteria.where("abilities.item").is(item).
     *                        orOperator(Criteria.where("abilities.value").lt(low),
     *                                   Criteria.where("abilities.value").gt(high))
     *                     ),
     * 对应mongoTemplate自动生成的shell
     * { "aggregate" : "user", "pipeline" :
     * [{ "$match" : { "$or" : [{ "role" : "student" }, { "role" : "master" }] } },
     *  { "$unwind" : "$abilities" },
     *  { "$match" : { "abilities.item" : "attack", "$or" : [{ "abilities.value" : { "$lt" : 30 } }, { "abilities.value" : { "$gt" : 60 } }] } },
     *  { "$group" : { "_id" : "$role", "count" : { "$sum" : 1 } } }], "cursor" : { "batchSize" : 2147483647 } }
     *
     *
     *
     * 但是聚合第一步不能写成：Aggregation.match(
     *                          Criteria.where("role").is("student").
     *                          orOperator(Criteria.where("role").is("master"))
     *                       ),
     * 对应mongoTemplate自动生成的shell
     *{ "aggregate" : "user", "pipeline" :
     *      * [{ "$match" : { "role" : "student", "$or" : [{ "role" : "master" }] } },
     *      *  { "$unwind" : "$abilities" },
     *      *  { "$match" : { "abilities.item" : "attack", "$and" : [{ "$or" : [{ "abilities.value" : { "$lt" : 30 } }, { "abilities.value" : { "$gt" : 60 } }] }] } },
     *      *  { "$group" : { "_id" : "$role", "count" : { "$sum" : 1 } } }], "cursor" : { "batchSize" : 2147483647 } }
     * 在mongo执行,没有结果
     * db.user.aggregate(
     * ... { "$match" : { "role" : "student", "$or" : [{ "role" : "master" }] } }
     * ... )
     * >
     *
     *
     * 总结：Criteria.where("K").is("V").orOperator(Criteria c1, Criteria c2)
     *      只有c1,c2才是or关系，和前面的where是and关系
     */
    public AggregationResults<Document> getCountNotBetweenAbility(String item, int low, int high){

        Criteria orCriteria = new Criteria().orOperator(
                Criteria.where("abilities.value").lt(low),
                Criteria.where("abilities.value").gt(high)
        );

        Criteria orCriteria02 = new Criteria().orOperator(
                Criteria.where("role").is("student"),
                Criteria.where("role").is("master")
        );

        List<AggregationOperation> aggregationOperations = Arrays.asList(
                new AggregationOperation[]{
                        Aggregation.match(orCriteria02),
                        Aggregation.unwind("abilities"),
                        Aggregation.match(Criteria.where("abilities.item").is(item).andOperator(orCriteria)),
                        Aggregation.group("role").count().as("count")
                }
        );
        AggregationResults<Document> aggregateResult = mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations), "user", Document.class);

        return aggregateResult;
    }

    /**
     * mongoTemplate的聚合很强大，应该带$的字段，如果不带$，自动帮我们加上了
     */
    public AggregationResults<Document> getSummaryAbilityOfStudent(String role, String ability, String operator){

//        Fields fields = Fields.from(Fields.field("grp_role", "role"), Fields.field("grp_ability_item", "abilities.item"));
        Fields fields = Fields.from(Fields.field("role_alias", "$role"), Fields.field("item_alias", "$abilities.item"));

        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(Aggregation.match(Criteria.where("role").is(role)));
        aggregationOperations.add(Aggregation.unwind("$abilities"));

        if(operator.equalsIgnoreCase("avg")) {
            aggregationOperations.add(
                    Aggregation.group("$role", "$abilities.item").avg("$abilities.value").as(operator+"_" + ability));
        }
        else if(operator.equalsIgnoreCase("sum")) {
            aggregationOperations.add(
                    Aggregation.group("$role", "$abilities.item").sum("$abilities.value").as(operator+"_" + ability));
        }
        else if(operator.equalsIgnoreCase("max")) {
            aggregationOperations.add(
                    //Aggregation.group("role", "abilities.item").min("abilities.value").as(operator+"_" + ability));
                    Aggregation.group(fields).max("$abilities.value").as(operator+"_" + ability));
        }
        else if(operator.equalsIgnoreCase("min")) {
            aggregationOperations.add(
                    //Aggregation.group("role", "abilities.item").min("abilities.value").as(operator+"_" + ability));
                    Aggregation.group(fields).min("$abilities.value").as(operator+"_" + ability));
        }

        AggregationResults<Document> aggregateResult = mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations), "user", Document.class);

        return aggregateResult;
    }

    public AggregationResults<Document> getSummaryAbility(){
        List<AggregationOperation> aggregationOperations = Arrays.asList(
                new AggregationOperation[]{
                        Aggregation.unwind("$abilities"),
                        Aggregation.group("$abilities.item")
                                   .sum("$abilities.value").as("sum")
                                   .avg("$abilities.value").as("avg")
                                   .max("$abilities.value").as("max")
                                   .min("$abilities.value").as("min")}
        );

        AggregationResults<Document> aggregateResult = mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations), "user", Document.class);
        return aggregateResult;
    }


    //将同年同月同日生分一组  https://blog.csdn.net/haiyoung/article/details/80865043
    public AggregationResults<Document> getSummarySameBirth(){

        //给分组的字段（_id里面）起别名
        Fields fields = Fields.from(Fields.field("$year", "$birth"),
                                    Fields.field("$month", "$birth"),
                                    Fields.field("$dayOfMonth", "$birth"));
        //Lambda
        AggregationExpression sumExpression  =
                (ctx) -> ctx.getMappedObject(Document.parse(new BasicDBObject("$sum", "$abilities.value").toJson()));

        AggregationOperation sumSameBirth = Aggregation.group(fields)
                                                       .count().as("totalSame")
                                                       .sum("$age").as("totalAge")
                                                       .sum(sumExpression).as("totalAbility");

        AggregationResults<Document> aggregateResult = mongoTemplate.aggregate(Aggregation.newAggregation(sumSameBirth), "user", Document.class);

        return aggregateResult;
    }

    /**
     * 调用存储过程（函数）
     */
    public Object callProcess(){
        ScriptOperations scriptOperations = mongoTemplate.scriptOps();
        //参数放到函数名后面
        Object getCountByAgeRange = scriptOperations.call("getCountByAgeRange", 1, 1000);
        return getCountByAgeRange;
    }

    public static void main(String[] args) throws Exception{
       /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = sdf.parse("1955-05-05 05:05:05");
        System.out.println(parse);*/

        Map<String, String> map = new LinkedHashMap<>();
        map.put("_id",null);
        System.out.println(JSON.toJSONString(map));

        System.out.println(new BasicDBObject("year", new BasicDBObject("$year", "$birth")).toJson());
    }
}
