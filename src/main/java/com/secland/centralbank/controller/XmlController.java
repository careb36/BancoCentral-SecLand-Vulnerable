package com.secland.centralbank.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * XML processing controller demonstrating XXE (XML External Entity) vulnerabilities.
 * <p>
 * <strong>WARNING:</strong> This controller contains intentional XXE vulnerabilities
 * for educational purposes. Never use this code in production!
 * </p>
 */
@RestController
@RequestMapping("/api/xml")
public class XmlController {

    /**
     * Processes XML input for transaction import.
     * <p>
     * <strong>Intentional Vulnerability (XXE):</strong> This endpoint processes XML
     * without disabling external entity processing, allowing attackers to read
     * local files, perform SSRF attacks, or cause DoS.
     * </p>
     * <p>
     * Example XXE payload:
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
     * &lt;!DOCTYPE transaction [
     *   &lt;!ENTITY xxe SYSTEM "file:///etc/passwd"&gt;
     * ]&gt;
     * &lt;transaction&gt;
     *   &lt;description&gt;&amp;xxe;&lt;/description&gt;
     * &lt;/transaction&gt;
     * </pre>
     * </p>
     *
     * @param xmlData the XML data to process
     * @return ResponseEntity with processing result
     */
    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> importTransactions(@RequestBody String xmlData) {
        try {
            // VULNERABILITY: XXE - DocumentBuilderFactory with default settings
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            // These lines would prevent XXE, but they are commented out for vulnerability demo:
            // factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            // factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            // Parse the XML - this is where XXE attack occurs
            org.w3c.dom.Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));
            
            // Extract and return content (this will include any XXE payload results)
            String description = document.getElementsByTagName("description").item(0).getTextContent();
            
            return ResponseEntity.ok("Transaction imported successfully. Description: " + description);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error processing XML: " + e.getMessage());
        }
    }

    /**
     * Validates XML structure.
     * <p>
     * <strong>Intentional Vulnerability (XXE):</strong> Another XXE vector through
     * XML validation without proper security configuration.
     * </p>
     *
     * @param xmlData the XML data to validate
     * @return ResponseEntity with validation result
     */
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> validateXml(@RequestBody String xmlData) {
        try {
            // VULNERABILITY: XXE in validation
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            // Parse XML (vulnerable to XXE)
            builder.parse(new ByteArrayInputStream(xmlData.getBytes()));
            
            return ResponseEntity.ok("XML is valid");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Invalid XML: " + e.getMessage());
        }
    }

    /**
     * Demonstrates XML processing with error information disclosure.
     * <p>
     * <strong>Intentional Vulnerability (Information Disclosure + XXE):</strong>
     * Returns detailed error messages that may contain sensitive information.
     * </p>
     *
     * @param xmlData the XML data to process
     * @return ResponseEntity with detailed processing information
     */
    @PostMapping(value = "/process", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> processXml(@RequestBody String xmlData) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            org.w3c.dom.Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));
            
            StringBuilder result = new StringBuilder();
            result.append("XML Processing Results:\n");
            result.append("Root element: ").append(document.getDocumentElement().getNodeName()).append("\n");
            result.append("Number of child nodes: ").append(document.getDocumentElement().getChildNodes().getLength()).append("\n");
            
            // Extract all text content (including XXE results)
            result.append("Content: ").append(document.getTextContent()).append("\n");
            
            return ResponseEntity.ok(result.toString());
            
        } catch (Exception e) {
            // VULNERABILITY: Detailed error message disclosure
            return ResponseEntity.status(500)
                    .body("Processing failed with error: " + e.getClass().getName() + 
                          " - Message: " + e.getMessage() + 
                          " - Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "None"));
        }
    }
}
