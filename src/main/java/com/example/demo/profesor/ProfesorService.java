package com.example.demo.profesor;

import com.example.demo.alumno.Alumno;
import com.example.demo.alumno.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfesorService {
    private final ProfesorRepository profesorRepository;
    private List<Profesor> profesores = new ArrayList<>();
    private int nextId = 1;

    @Autowired
    public ProfesorService(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    public List<Profesor> getProfesores(){
        return profesorRepository.findAll();
    }

    public Profesor getProfesorById(int id){
        return profesorRepository.findById(id).orElse(null);

    }

    public void addProfesor(Profesor profesor){
        if(profesor.validarAtributos()){
            profesorRepository.save(profesor);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los campos enviados no son válidos");
        }
        System.out.println(profesor);
    }

    public void updateProfesor(int id, Integer numeroEmpleado, String nombres, String apellidos, Integer horasClase) throws ResponseStatusException{
        Profesor profesor = new Profesor(id, numeroEmpleado, nombres, apellidos,horasClase);


        if(profesor.validarAtributosPUT()){
            Profesor existingProfesor = profesorRepository.findById(id).orElse(null);
            existingProfesor.setNumeroEmpleado(numeroEmpleado);
            existingProfesor.setNombres(nombres);
            existingProfesor.setApellidos(apellidos);
            existingProfesor.setHorasClase(horasClase);
            profesorRepository.save(existingProfesor);
           // profesor = profesorRepository.updateProfesor(id, numeroEmpleado, nombres, apellidos, horasClase);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los campos enviados no son válidos" + profesor.toString());
        }
        System.out.println("PUT" + profesor);
    }

    public void deleteProfesor(int id) {

        profesorRepository.deleteById(id);


    }
}
