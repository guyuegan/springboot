package org.neo.dao;

import org.neo.model.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentCrudRepository extends CrudRepository<Student, Integer> {

    public Student findByName(String name);
}
