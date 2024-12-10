package com.ttruongdev.hotelmanagement.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ttruongdev.hotelmanagement.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class AwsS3Service {

    private final String bucketName = "images-hotel-management";

    @Value("${aws.s3.access.key}")
    private String awsS3AcessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    public String saveImageToS3(MultipartFile photo) {
        String s3LocationImage = null;

        try {
            String fileName = photo.getOriginalFilename();
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AcessKey, awsS3AcessKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.AP_SOUTHEAST_2)
                    .build();

            InputStream inputStream = photo.getInputStream();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            s3Client.putObject(putObjectRequest);
            return "https://" + bucketName + "s3.amazonaws.com/" + fileName;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AppException("Unable to upload image to s3 bucket: " + ex.getMessage());
        }

    }


}
