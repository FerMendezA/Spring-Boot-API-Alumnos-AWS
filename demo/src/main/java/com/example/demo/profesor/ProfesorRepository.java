package com.example.demo.profesor;

import com.example.demo.alumno.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor,Integer> {

}
