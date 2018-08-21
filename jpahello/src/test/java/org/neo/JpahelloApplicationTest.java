package org.neo;

import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo.dao.StudentCrudRepository;
import org.neo.dao.StudentPageAndSortRepository;
import org.neo.dao.StudentRepository;
import org.neo.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpahelloApplicationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentCrudRepository studentCrudRepository;

    @Autowired
    private StudentPageAndSortRepository studentPageAndSortRepository;

    @Test
    public void testStudentRepository(){
        Assert.assertEquals("wukong", studentRepository.fetchById(1).getName());

        Assert.assertEquals("wukong", studentRepository.findById(1).getName());
        Assert.assertEquals("bajie", studentRepository.getById(2).getName());
        Assert.assertEquals("shaseng", studentRepository.readById(3).getName());
//        Assert.assertEquals("tangseng", studentRepository.loadById(4).getName());

        Assert.assertEquals(0, studentRepository.findByAddressAndAge("huaguoshan", 100).size());
        Assert.assertEquals(2, studentRepository.findByAddressOrAge("huaguoshan", 100).size());

        Assert.assertEquals(2, studentRepository.countByNameContains("seng"));

        System.out.println(JSON.toJSONString(studentRepository.findByAgeBetweenOrderByAgeDesc(80, 200)));

        /* 自动生成的sql
Hibernate: select student0_.id as id1_0_, student0_.address as address2_0_, student0_.age as age3_0_, student0_.name as name4_0_ from t_stu student0_ where student0_.id=?

Hibernate: select student0_.id as id1_0_0_, student0_.address as address2_0_0_, student0_.age as age3_0_0_, student0_.name as name4_0_0_ from t_stu student0_ where student0_.id=?
Hibernate: select student0_.id as id1_0_, student0_.address as address2_0_, student0_.age as age3_0_, student0_.name as name4_0_ from t_stu student0_ where student0_.id=?
Hibernate: select student0_.id as id1_0_, student0_.address as address2_0_, student0_.age as age3_0_, student0_.name as name4_0_ from t_stu student0_ where student0_.id=?

Hibernate: select student0_.id as id1_0_, student0_.address as address2_0_, student0_.age as age3_0_, student0_.name as name4_0_ from t_stu student0_ where student0_.address=? and student0_.age=?
Hibernate: select student0_.id as id1_0_, student0_.address as address2_0_, student0_.age as age3_0_, student0_.name as name4_0_ from t_stu student0_ where student0_.address=? or student0_.age=?

Hibernate: select count(student0_.id) as col_0_0_ from t_stu student0_ where student0_.name like ?
         */
    }

    @Test
    public void testSave(){
        Student student = new Student("xiaoma", "donghai", 66);
        Assert.assertEquals("xiaoma", studentCrudRepository.save(student).getName());
    }

    @Test
    public void testSave02(){
        /*Student student = studentCrudRepository.findByName("xiaoma");
        student.setName("xiaobailong");
        Assert.assertEquals("xiaobailong", studentCrudRepository.save(student).getName());*/

        Student student = studentCrudRepository.findByName("wukong");
        student.setAge(77);
        studentCrudRepository.save(student);

        Student student02 = studentCrudRepository.findByName("bajie");
        student02.setAge(77);
        studentCrudRepository.save(student02);

    }

    @Test
    public void testDelete(){
        System.out.println("before del: " + studentCrudRepository.count());
        Student student = studentCrudRepository.findByName("xiaobailong");
        studentCrudRepository.delete(student);
        System.out.println("after del: " + studentCrudRepository.count());

    }

    @Test
    public void testFindAll(){
        List<Student> all = (List<Student>) studentCrudRepository.findAll();
        System.out.println(JSON.toJSONString(all));
    }

    @Test
    public void testPage(){
        /**
         * 分页后，页数从0开始
         */

        //设置分页策略（传入Pageable对象）
        Pageable pageRequest = new QPageRequest(0, 2);

        Page<Student> page01 = studentPageAndSortRepository.findByAgeBetween(1, 200, pageRequest);
        System.out.println("\n" + JSON.toJSONString(page01.getContent()));
        System.out.println("总记录数：" + page01.getTotalElements());
        System.out.println("总页数：" + page01.getTotalPages());
        System.out.println("当前页：" + page01.getNumber());

        Pageable pageRequest02 = new QPageRequest(1, 2);
        Page<Student> page02 = studentPageAndSortRepository.findByAgeBetween(1, 200, pageRequest02);
        System.out.println("\n" + JSON.toJSONString(page02.getContent()));
        System.out.println("总记录数：" + page02.getTotalElements());
        System.out.println("总页数：" + page02.getTotalPages());
        System.out.println("当前页：" + page02.getNumber());
    }


    @Test
    public void testSort(){
        //以age降序
        Sort order1 = new Sort(new Sort.Order(Sort.Direction.DESC, "age"));
        List<Student> students = studentPageAndSortRepository.findByAgeBetween(1, 200, order1);
        System.out.println(JSON.toJSONString(students) + "\n");

        //以age, name降序
        Sort order2 = new Sort(new Sort.Order(Sort.Direction.DESC, "age"),
                new Sort.Order(Sort.Direction.DESC, "name"));
        List<Student> students02 = studentPageAndSortRepository.findByAgeBetween(1, 200, order2);
        System.out.println(JSON.toJSONString(students02) + "\n");

        //以age降序，name升序
        Sort order3 = new Sort(new Sort.Order(Sort.Direction.DESC, "age"),
                new Sort.Order(Sort.Direction.ASC, "name"));
        List<Student> students3 = studentPageAndSortRepository.findByAgeBetween(1, 200, order3);
        System.out.println(JSON.toJSONString(students3) + "\n");
    }

    @Test
    public void testSpecification(){

    }

}
