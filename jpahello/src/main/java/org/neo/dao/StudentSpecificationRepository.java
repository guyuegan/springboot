package org.neo.dao;

import org.neo.model.Student;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * 只是继承JpaSpecificationExecutor，在测试类中Autowired报错
 *
 * 同时继承JpaRepository, JpaSpecificationExecutor解决问题
 */
public interface StudentSpecificationRepository extends JpaRepository<Student,String>, JpaSpecificationExecutor<Student> {

}
