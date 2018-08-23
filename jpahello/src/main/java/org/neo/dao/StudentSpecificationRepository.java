package org.neo.dao;

import org.neo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 只是继承JpaSpecificationExecutor，在测试类中Autowired报错
 *
 * 同时继承JpaRepository, JpaSpecificationExecutor解决问题
 */
public interface StudentSpecificationRepository
        extends JpaRepository<Student,String>, JpaSpecificationExecutor<Student> {

}
