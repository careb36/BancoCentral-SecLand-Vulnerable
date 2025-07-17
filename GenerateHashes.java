import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== BCrypt Password Hashes for Database Seeding ===");
        System.out.println();
        
        // Demo users
        System.out.println("testuser / password123:");
        System.out.println(encoder.encode("password123"));
        System.out.println();
        
        System.out.println("admin / admin123:");
        System.out.println(encoder.encode("admin123"));
        System.out.println();
        
        System.out.println("carolina_p / carolina123:");
        System.out.println(encoder.encode("carolina123"));
        System.out.println();
        
        System.out.println("test_user / test123:");
        System.out.println(encoder.encode("test123"));
        System.out.println();
    }
} 