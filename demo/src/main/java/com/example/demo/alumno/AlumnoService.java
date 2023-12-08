package com.example.demo.alumno;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.example.demo.AmazonDynamoClient;
import com.example.demo.AmazonS3Client;
import com.example.demo.AwsSNS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class AlumnoService {


    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private AmazonSNSClient amazonSNSClient;
    @Autowired
    private AmazonS3 s3Client;
    private String TOPIC_ARN = "arn:aws:sns:us-east-1:911596133994:uady-topic";
    private static final String BUCKET_NAME = "proyectomendez";
    private final AlumnoRepository alumnoRepository;
    private List<Alumno> alumnos = new ArrayList<>();
    private int nextId = 1;

    @Autowired
    public AlumnoService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;

    }

    public List<Alumno> getAlumnos() {
        return alumnoRepository.findAll();
    }

    public Alumno getAlumnoById(int id) {
        return alumnoRepository.findById(id).orElse(null);
    }

    public void addAlumno(Alumno alumno) throws ResponseStatusException {
        if (alumno.validarAtributos()) {
            alumnoRepository.save(alumno);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los campos enviados no son válidos");
        }
        System.out.println(alumno);
    }

    public void updateAlumno(int id, String nombres, String apellidos, String matricula, Double promedio, String password) throws ResponseStatusException {
        Alumno alumno = new Alumno(id, nombres, apellidos, matricula, promedio, password);
        //System.out.println("Validar atributos put " + alu);
        if (alumno.validarAtributosPUT()) {

            Alumno existingAlumno = alumnoRepository.findById(id).orElse(null);
            //existingAlumno.setNumeroEmpleado(numeroEmpleado);
            existingAlumno.setNombres(nombres);
            existingAlumno.setApellidos(apellidos);
            existingAlumno.setMatricula(matricula);
            existingAlumno.setPromedio(promedio);
            alumnoRepository.save(existingAlumno);
        } else {
            //System.out.println("Enviaron " + id + " nombres: " + nombres + " apellidos: " + apellidos + " matricula " + matricula + " promedio: " + promedio);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los campos enviados no son válidos" + alumno.toString());
        }
        //System.out.println("PUT" + alumno);
    }

    public void deleteAlumno(int id) throws ResponseStatusException {
        alumnoRepository.deleteById(id);
    }


    private File convertMulti(MultipartFile file) {
        File converted = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(converted)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return converted;
    }

    public String uploadFile(MultipartFile file){
        File fileObj = convertMulti(file);
        String fileName = System.currentTimeMillis()+  "_" + file.getOriginalFilename();
        System.out.println(fileName);
        System.out.println(fileObj);
        //s3Client.putObject(new PutObjectRequest(BUCKET_NAME, fileName,fileObj).withCannedAcl(CannedAccessControlList.PublicRead));
        s3Client.putObject(new PutObjectRequest(BUCKET_NAME,fileName,fileObj));
        String url = s3Client.getUrl(BUCKET_NAME, fileName).toExternalForm();
        //System.out.println("URL : " +url);
        fileObj.delete();
        return  url;
    }

    public String login (int id, String password){
        String sessionString = "";
        Alumno alumno =alumnoRepository.findById(id).orElse(null);
        System.out.println("El alumno login: " + alumno.toString());
        if(alumno != null && alumno.getPassword().equals(password)){
            Sesion sesion = new Sesion(id);
            dynamoDBMapper.save(sesion);
            System.out.println("sesion uuid " + sesion.getId());
           sessionString = sesion.getSessionString();
        }
        return sessionString;
    }

    public boolean verify(int id){
        //Map<String, AttributeValue> expressionAttributeValues =
        //        new HashMap<String, AttributeValue>();
        //expressionAttributeValues.put(":id", new AttributeValue().withN(String.valueOf(id)));
        //DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        //        .withFilterExpression("alumnoId = :id").withExpressionAttributeValues(expressionAttributeValues);

        //List<Sesion> scanResult = dynamoDBMapper.scan(Sesion.class, scanExpression);

        Map<String, AttributeValue> expressionAttributeValues =
                new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":id", new AttributeValue().withN(String.valueOf(id)));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("alumnoId = :id").withExpressionAttributeValues(expressionAttributeValues);

        List<Sesion> scanResult = dynamoDBMapper.scan(Sesion.class, scanExpression);
        if(!scanResult.isEmpty() && scanResult.size()>0) {
           Sesion sesion=scanResult.get(0);
            return sesion.getActive();
        }


        return false;

    }

    public boolean logout(int id){



        Map<String, AttributeValue> expressionAttributeValues =
                new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":id", new AttributeValue().withN(String.valueOf(id)));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("alumnoId = :id").withExpressionAttributeValues(expressionAttributeValues);

        List<Sesion> scanResult = dynamoDBMapper.scan(Sesion.class, scanExpression);
        if(!scanResult.isEmpty() && scanResult.size()>0) {
            Sesion sesion=scanResult.get(0);
            //System.out.println("HOLA logout " + sesion.getId());
            //System.out.println("sesion uuid " + sesion.getId());
            sesion.setActive(false);
            dynamoDBMapper.save(sesion);
            return true;
        }
        return false;
    }

    public Sesion scanById(int id){
        Map<String, AttributeValue> expressionAttributeValues =
                new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":id", new AttributeValue().withN(String.valueOf(id)));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("alumnoId = :id").withExpressionAttributeValues(expressionAttributeValues);

        List<Sesion> scanResult = dynamoDBMapper.scan(Sesion.class, scanExpression);
        return scanResult.get(0);
    }

    public boolean publish(int id){
        Alumno alumno = alumnoRepository.findById(id).orElse(null);
        if(alumno != null){
            PublishRequest publishRequest =
                    new PublishRequest(TOPIC_ARN, alumno.emailAlumno());
            amazonSNSClient.publish(publishRequest);
            return true;
        }
        return false;
    }

}


/*
    public void login(int id, String password){
        //sesiones-alumnos
        Alumno alumno =alumnoRepository.findById(id).orElse(null);
        if(alumno != null && alumno.getPassword().equals(password)){
            HashMap<String, AttributeValue> itemValues = new HashMap<String,AttributeValue>();

            // Add all content to the table
            Long time = System.currentTimeMillis() / 1000;
            String idData = Integer.toString(id);
            itemValues.put("id", AttributeValue.
                    .s(UUID.randomUUID().toString()).build());
            itemValues.put("fecha", AttributeValue.builder().n(time.toString()).build());
            itemValues.put("alumnoId", AttributeValue.builder().n(idData).build());
            itemValues.put("active", AttributeValue.builder().bool(true).build());
            itemValues.put("sessionString", AttributeValue.builder().s(getRandomString(128)).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName("sesiones-alumnos")
                    .item(itemValues)
                    .build();

            try {
                dynamoDBMapper.save(request);
                System.out.println( " was successfully updated");

            } catch (ResourceNotFoundException e) {
                System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", "sesiones-alumnos");
                System.err.println("Be sure that it exists and that you've typed its name correctly!");
               // System.exit(1);
            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
               // System.exit(1);
            }
        }


        //alumnoRepository.
    }

     public String uploadFile(MultipartFile file) throws IOException{
        try{
            ;
            String fileName = file.getOriginalFilename();



            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return "se subió";
        }catch(IOException e){
            throw new IOException(e.getMessage());
        }
    }

    public String upload(MultipartFile file){
        File fileObj = convertMulti(file);
        String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .build();
        //s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileObj));
        return null;
    }


    */
//     ScanRequest scanRequest = new ScanRequest()
//           .withTableName("sesiones-alumnos")
//            .withFilterExpression("alumnoId = :val")
//          .withProjectionExpression("active")
//         .withExpressionAttributeValues(expressionAttributeValues);


//ScanResult result = .scan(scanRequest);
//ItemCollection<Sesion> items = table.scan("Price < :pr", // FilterExpression
//    "Id, Title, ProductCategory, Price", // ProjectionExpression
//      null, // ExpressionAttributeNames - not used in this example
//  expressionValues);
//of(":active", AttributeValue.builder().bool(true).build());

        /*// Aquí se establece la expresión de filtro para el atributo "sesion"
        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression("sesion = :active")
                .expressionValues(expressionValues)
                .build();

        ScanEnhancedResponse<TuItem> scanEnhancedResponse = table.scan(scanEnhancedRequest);
*/



