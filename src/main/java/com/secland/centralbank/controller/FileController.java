package com.secland.centralbank.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File controller demonstrating Path Traversal and File Disclosure vulnerabilities.
 * <p>
 * <strong>WARNING:</strong> This controller contains intentional security vulnerabilities
 * for educational purposes. Never use this code in production!
 * </p>
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final String UPLOAD_DIR = "uploads/";

    /**
     * Downloads a file from the server.
     * <p>
     * <strong>Intentional Vulnerability (Path Traversal):</strong> This endpoint accepts
     * any filename without validation, allowing attackers to access files outside the
     * intended directory using path traversal sequences like "../".
     * </p>
     * <p>
     * Example exploit: GET /api/files/download?filename=../../../etc/passwd
     * </p>
     *
     * @param filename the name of the file to download
     * @return ResponseEntity with file contents or error message
     */
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam String filename) {
        try {
            // VULNERABILITY: Direct path concatenation without validation
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Reads and displays file contents directly.
     * <p>
     * <strong>Intentional Vulnerability (Path Traversal + Information Disclosure):</strong>
     * This endpoint not only allows path traversal but also displays sensitive file
     * contents directly in the HTTP response.
     * </p>
     * <p>
     * Example exploit: GET /api/files/read/../../../../application.properties
     * </p>
     *
     * @param filename the name of the file to read
     * @return ResponseEntity with file contents as text
     */
    @GetMapping("/read/{filename}")
    public ResponseEntity<String> readFile(@PathVariable String filename) {
        try {
            // VULNERABILITY: Path traversal through path variable
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            
            String content = Files.readString(filePath);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(content);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file: " + filename + " - " + e.getMessage());
        }
    }

    /**
     * Lists files in a directory.
     * <p>
     * <strong>Intentional Vulnerability (Directory Traversal):</strong> This endpoint
     * allows listing files in any directory on the system.
     * </p>
     *
     * @param directory the directory path to list (optional, defaults to upload dir)
     * @return ResponseEntity with list of files
     */
    @GetMapping("/list")
    public ResponseEntity<String> listFiles(@RequestParam(defaultValue = "") String directory) {
        try {
            // VULNERABILITY: Directory traversal without validation
            Path dirPath = Paths.get(UPLOAD_DIR + directory);
            
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                return ResponseEntity.badRequest().body("Directory not found: " + directory);
            }

            StringBuilder fileList = new StringBuilder();
            fileList.append("Files in directory: ").append(dirPath.toString()).append("\n\n");
            
            Files.list(dirPath).forEach(path -> {
                fileList.append(path.getFileName().toString()).append("\n");
            });
            
            return ResponseEntity.ok(fileList.toString());
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error listing directory: " + e.getMessage());
        }
    }
}
