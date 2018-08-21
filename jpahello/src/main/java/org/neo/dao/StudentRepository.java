package org.neo.dao;

import org.neo.model.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface StudentRepository extends Repository<Student, Integer> {

    //?1: 使用方法的第一个参数
    @Query("select s from Student s where s.id=?1")
    public Student fetchById(int id);

    /**
     * 衍生查询：带findBy,getBy,readBy的方法，
     * Repository都会自动帮我们执行查询
     *
     * 在PartTree类中有正则匹配，可以查看有哪些方法可以自动生成查询语句
     * org.springframework.data.repository.query.parser.PartTree
     *
     * 在PartTreeJpaQuery类中生成查询
     * org.springframework.data.jpa.repository.query.PartTreeJpaQuery
     */
    public Student findById(int id);
    public Student getById(int id);
    public Student readById(int id);

    // loadBy不能自动生成查询
//    public Student loadById(int id);


    public List<Student> findByAddressAndAge(String address, int age);
    public List<Student> findByAddressOrAge(String address, int age);

    public int countByNameContains(String name);

    public List<Student> findByAgeBetweenOrderByAgeDesc(int min, int max);


}
