package org.neo.dao;


import com.alibaba.fastjson.JSON;
import org.neo.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class StudentSpecRepositoryImpl {

    @Autowired
    private StudentSpecificationRepository studentSpecificationRepository;

    public List<Student> findByNameOrAddress(){
        List<Student> all = studentSpecificationRepository.findAll(new Specification<Student>() {
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate nameCriteria = criteriaBuilder.like(root.get("name"), "%seng");
                Predicate nameCriteria02 = criteriaBuilder.like(root.get("address"), "%tong%");
                return criteriaBuilder.and(nameCriteria, nameCriteria02);
            }
        });

       return all;
    }


    public List<Student> findByAgeAndAddress(){
        Specification<Student> specification = new Specification<Student>(){
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate nameCriteria = criteriaBuilder.like(root.get("name"), "%kong%");
                Predicate nameCriteria02 = criteriaBuilder.notLike(root.get("name"), "%ba%");
                return criteriaBuilder.and(nameCriteria, nameCriteria02);
            }
        };

        Specification<Student> specification02 = new Specification<Student>(){
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate ageCriteria02 = criteriaBuilder.greaterThan(root.get("age"), 800);
                Predicate ageCriteria = criteriaBuilder.lessThan(root.get("age"), 500);
                return criteriaBuilder.or(ageCriteria, ageCriteria02);
            }
        };

        Specification<Student> allSpec = specification.and(specification02);
        Sort.Order ageOrder = new QSort.Order(Sort.Direction.DESC, "age");

        List<Student> all = studentSpecificationRepository.findAll(allSpec, new Sort(ageOrder));

        return all;
    }
}
