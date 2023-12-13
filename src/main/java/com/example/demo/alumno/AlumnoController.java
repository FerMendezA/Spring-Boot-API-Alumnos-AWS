package com.example.demo.alumno;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/alumnos")
public class AlumnoController {



    private final AlumnoService alumnoService;
    @Autowired
    public AlumnoController(AlumnoService alumnoService) {

        this.alumnoService = alumnoService;

    }

    @GetMapping
    public List<Alumno> getAlumnos(){
        return alumnoService.getAlumnos();
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<String> getAlumnoById(@PathVariable("id") int id){
        Alumno alumno = alumnoService.getAlumnoById(id);
        if(alumno != null) {
            try {

                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(alumno.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }


    @PostMapping
    public ResponseEntity<String> createAlumno(@RequestBody Alumno alumno){
        try{
            alumnoService.addAlumno(alumno);
            return ResponseEntity.status(HttpStatus.CREATED) // Indica la ubicación del nuevo recurso
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(alumno.toString());
        }catch (Exception e){

            //e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
        }

    }

    //POST /alumnos/{id}/fotoPerfil   POST /alumnos/{id}/email

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteAlumno(@PathVariable("id") int id){
        JsonObject response = new JsonObject();
        response.addProperty("idEliminado", 2);
        try {
            alumnoService.deleteAlumno(id);


            System.out.println(response);
            return  new ResponseEntity<>("s", HttpStatus.OK);


        }catch(Exception e){
            return new ResponseEntity<>("s", HttpStatus.NOT_FOUND);


        }
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<String> updateAlumno(@PathVariable("id") int id,
                             @RequestParam(required = false) String nombres,
                             @RequestParam(required = false) String apellidos,
                             @RequestParam(required = false) String matricula,
                             @RequestParam(required = false) Double promedio,
                                               @RequestParam(required = false) String password){
        try{
            Alumno alumno = new Alumno(id, nombres, apellidos, matricula, promedio, password);
            alumnoService.updateAlumno(id, nombres, apellidos, matricula, promedio, password);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }catch (Exception e){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
        }
    }

    @PostMapping("/{id}/fotoPerfil")
    public ResponseEntity<String> uploadFile(@PathVariable("id") int id, @RequestParam("file") MultipartFile file) throws IOException {
        try{
            String response =  alumnoService.uploadFile(file);
            System.out.println(response);
            JsonObject json = new JsonObject();
            json.addProperty("fotoPerfilUrl", response);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json.toString());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(e.getMessage());
        }

    }

    @PostMapping(path = "/{id}/session/login")
    public ResponseEntity<String> loginAlumno(@PathVariable("id") int id,@RequestParam(required = true) String password){
        try{
            String sessionString = alumnoService.login(id, password);
            if(sessionString.length() > 1){
                JsonObject json = new JsonObject();
                json.addProperty("sessionString", sessionString);
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(json.toString());
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON).build();
            }


        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
        }


    }

    @PostMapping(path = "/{id}/session/verify")
    public ResponseEntity<String> verifyAlumno(@PathVariable("id") int id){
        try{
            alumnoService.verify(id);
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(null);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(e.getMessage());
        }


    }
    @PostMapping (path= "/{id}/session/logout")
    public ResponseEntity<String> logoutAlumno(@PathVariable("id") int id){
        try{
            alumnoService.logout(id);
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .build();

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(e.getMessage());
        }
    }

    @PostMapping (path= "/{id}/email")
    public ResponseEntity<?> sns(@PathVariable("id") int id){
        try{
            alumnoService.publish(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(e.getMessage());
        }
    }

}
