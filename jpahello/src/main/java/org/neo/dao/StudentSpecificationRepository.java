package org.neo.dao;

import org.neo.model.Student;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class StudentSpecificationRepository implements JpaSpecificationExecutor<Student> {

    public List<Student> findAllStu() {


        return this.findAll(new Specification<Student>() {
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate nameCriteria = criteriaBuilder.like(root.get("name"), "%seng");
                Predicate ageCriteria = criteriaBuilder.greaterThan(root.get("age"), 150);

                return criteriaBuilder.and(nameCriteria, ageCriteria);
            }
        });

    }
}
