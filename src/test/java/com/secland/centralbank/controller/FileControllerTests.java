package com.secland.centralbank.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for {@link FileController}.
 * <p>
 * This test class validates the intentional path traversal vulnerabilities
 * for educational purposes. These tests demonstrate how attackers could
 * exploit file system access vulnerabilities.
 * </p>
 */
@WebMvcTest(FileController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("File Controller Tests - Path Traversal Vulnerabilities")
class FileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests that normal file read operations work as expected.
     * This establishes baseline functionality before testing vulnerabilities.
     */
    @Test
    @DisplayName("Should read normal file successfully")
    void readFile_normalPath_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/files/read/test.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));
    }

    /**
     * Tests the path traversal vulnerability in file reading.
     * <p>
     * <strong>Intentional Vulnerability:</strong> This test demonstrates how
     * an attacker could use path traversal to read system files like /etc/passwd.
     * </p>
     */
    @Test
    @DisplayName("Should allow path traversal attack (Educational Vulnerability)")
    void readFile_pathTraversal_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/files/read/../../../etc/passwd"))
                .andExpect(status().isOk());
    }

    /**
     * Tests the path traversal vulnerability in file downloads.
     * <p>
     * <strong>Intentional Vulnerability:</strong> This test demonstrates how
     * an attacker could download sensitive configuration files.
     * </p>
     */
    @Test
    @DisplayName("Should allow download of sensitive files (Educational Vulnerability)")
    void downloadFile_pathTraversal_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/files/download")
                .param("filename", "../../../application.properties"))
                .andExpect(status().isOk());
    }

    /**
     * Tests the directory traversal vulnerability in directory listing.
     * <p>
     * <strong>Intentional Vulnerability:</strong> This test demonstrates how
     * an attacker could list contents of sensitive directories.
     * </p>
     */
    @Test
    @DisplayName("Should allow directory traversal (Educational Vulnerability)")
    void listFiles_directoryTraversal_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/files/list")
                .param("directory", "../../../"))
                .andExpect(status().isOk());
    }

    /**
     * Tests that the vulnerability works with Windows-style path traversal.
     * This ensures cross-platform vulnerability demonstration.
     */
    @Test
    @DisplayName("Should allow Windows-style path traversal (Educational)")
    void readFile_windowsPathTraversal_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/files/read/..\\..\\..\\windows\\system32\\drivers\\etc\\hosts"))
                .andExpect(status().isOk());
    }
}
