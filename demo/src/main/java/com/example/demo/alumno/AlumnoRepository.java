package com.example.demo.alumno;

import com.example.demo.profesor.Profesor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno,Integer> {


}
