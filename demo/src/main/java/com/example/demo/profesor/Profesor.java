package com.example.demo.profesor;

import com.google.gson.JsonObject;
import jakarta.persistence.*;

@Entity
@Table(name = "profesores")
public class Profesor {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "numero_empleado")
    private Integer numeroEmpleado;

    private String nombres;
    private String apellidos;
    @Column(name = "horas_clase")
    private Integer horasClase;

    public Profesor(){

    }

    public Profesor(int id, Integer numeroEmpleado, String nombres, String apellidos, Integer horasClase) {
        this.id = id;
        this.numeroEmpleado = numeroEmpleado;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.horasClase = horasClase;
    }

    public Profesor(Integer numeroEmpleado, String nombres, String apellidos, Integer horasClase) {
        this.numeroEmpleado = numeroEmpleado;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.horasClase = horasClase;
    }

    @Override
    public String toString() {

        return "{"
                + "\"id\":" + this.id + ","
                + "\"numeroEmpleado\":" + this.numeroEmpleado + ","
                + "\"nombres\":\"" + this.nombres + "\","
                + "\"apellidos\":\"" + this.apellidos + "\","
                + "\"horasClase\":" + this.horasClase
                + "}";

    }


    public boolean validarAtributos() {


        if (numeroEmpleado == null || numeroEmpleado <= 0){
            return false;
        }

        if (nombres == null || nombres.trim().isEmpty()) {
            return false;
        }

        if (apellidos == null || apellidos.trim().isEmpty()) {
            return false;
        }

        if (horasClase == null || horasClase <= 0){
            return false;
        }

        return true;
    }

    public boolean validarAtributosPUT(){
        if(nombres == null && apellidos == null && numeroEmpleado == null && horasClase == null){
            return false;
        }

        if (numeroEmpleado != null){
            if ( numeroEmpleado < 0 ){
                return false;
            }
        }
        if(horasClase != null){
            if (horasClase < 0 ){
                return false;
            }
        }

        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(Integer numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
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

    public Integer getHorasClase() {
        return horasClase;
    }

    public void setHorasClase(Integer horasClase) {
        this.horasClase = horasClase;
    }
}
