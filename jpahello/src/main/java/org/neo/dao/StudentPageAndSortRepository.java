package org.neo.dao;

import org.neo.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StudentPageAndSortRepository extends PagingAndSortingRepository<Student, Integer> {

    public Page<Student> findByAgeBetween(int min, int max, Pageable pageable);

    List<Student> findByAgeBetween(int min, int max, Sort sort);
}
