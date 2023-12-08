package com.example.demo.alumno;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Random;
import java.util.UUID;

@DynamoDBTable(tableName = "sesiones-alumnos")
public class Sesion {

    private String id;


    private Long fecha; //unix timetamp


    private int alumnoId;


    private Boolean active;


    private String sessionString; //string alfanumerico de 128 caracteres
    public Sesion(){

    }

    public Sesion(int alumnoId) {
        this.id = UUID.randomUUID().toString();
        this.fecha = System.currentTimeMillis() / 1000;
        this.alumnoId = alumnoId;
        this.active = true;
        this.sessionString = getRandomString(128);
    }
    @DynamoDBHashKey
    @DynamoDBAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @DynamoDBAttribute
    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }
    @DynamoDBAttribute
    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }
    @DynamoDBAttribute
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    @DynamoDBAttribute
    public String getSessionString() {
        return sessionString;
    }

    public void setSessionString(String sessionString) {
        this.sessionString = sessionString;
    }

    public static String getRandomString(int length) {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return  random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


}
