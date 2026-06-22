package com.xanhdi.website.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageServiceImpl implements StorageService {

    @Value("${supabase.url:https://ustnundvxlmxrxdavbym.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket:xanhdi-media}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (supabaseKey == null || supabaseKey.trim().isEmpty()) {
            throw new IllegalStateException("Supabase API Key (supabase.key) is missing or empty! Please define the SUPABASE_KEY environment variable or set it in application.properties.");
        }

        if (!supabaseKey.trim().startsWith("eyJ")) {
            throw new IllegalArgumentException("Invalid Supabase Key format! The key must be a long JWT token starting with 'eyJ'. Ensure you are NOT using the database password (like 'Hongquan@123') as the API token.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        // Clean and generate a unique file name to avoid collisions
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String cleanName = UUID.randomUUID().toString() + extension;
        
        // Supabase storage REST upload URL
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + cleanName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.valueOf(file.getContentType() != null ? file.getContentType() : "application/octet-stream"));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                uploadUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            // Return public URL (assumes bucket is public)
            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + cleanName;
        } else {
            throw new IOException("Failed to upload file to Supabase Storage: " + response.getBody());
        }
    }
}
