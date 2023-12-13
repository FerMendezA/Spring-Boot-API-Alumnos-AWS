package com.example.demo.alumno;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import jakarta.persistence.*;

import java.util.Random;

@Entity
@Table(name = "alumnos")
@DynamoDBTable(tableName = "sesiones-alumnos")

public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombres;

    private String apellidos;
    private String matricula;
    private Double promedio;
    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl;
    private String password;

    private Long fecha;
    private boolean active;
    private String sessionString;

    public Alumno(){

    }
    public Alumno(int id, String nombres, String apellidos, String matricula, Double promedio, String password) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.matricula = matricula;
        this.promedio = promedio;
        this.password = password;
    }

    public Alumno(String nombres, String apellidos, String matricula, Double promedio, String password) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.matricula = matricula;
        this.promedio = promedio;
        this.password  = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Double getPromedio() {
        return promedio;
    }

    public void setPromedio(Double promedio) {
        this.promedio = promedio;
    }

    @Override
    public String toString() {

        return "{"
                + "\"id\":" + this.id + ","
                + "\"nombres\":\"" + this.nombres + "\","
                + "\"apellidos\":\"" + this.apellidos + "\","
                + "\"matricula\":\"" + this.matricula + "\","
                + "\"promedio\":" + this.promedio
                + "}";
    }

    public String emailAlumno(){
        return  "Alumno: "+   this.nombres + "  " + this.apellidos + "\n"
                + "Promedio: " + this.promedio;
    }

    public boolean validarAtributos() {

        if (nombres == null ) {
            return false;
        }

        if (apellidos == null || apellidos.trim().isEmpty()) {
            return false;
        }

        if (matricula == null || matricula.trim().isEmpty()){
            return false;
        }

        if (promedio == null || promedio < 0 ){
            return false;
        }

        return true;
    }

    public boolean validarAtributosPUT(){
       if(nombres == null && apellidos == null && matricula == null && promedio == null){
            return false;
        }
        if(promedio!=null){
            if ( promedio < 0 ){
                return false;
            }
        }

        if(matricula != null){
            for (int i = 0; i < matricula.length(); i++) {
                if ((!Character.isLetterOrDigit(matricula.charAt(i)))) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setFotoPerfilUrl(String fotoPerfilUrl) {
        this.fotoPerfilUrl = fotoPerfilUrl;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Alumno(int id, String nombres, String apellidos, String matricula, Double promedio, String fotoPerfilUrl, String password) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.matricula = matricula;
        this.promedio = promedio;
        this.fotoPerfilUrl = fotoPerfilUrl;
        this.password = password;
    }


}
