package murach.dibu;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class DBUtil {
    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEmFactory() {
        if (emf == null) {
            try {
                try {
                    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
                    dotenv.entries().forEach(entry -> {
                        System.setProperty(entry.getKey(), entry.getValue());
                    });
                } catch (Exception e) {
                }

                String dbUrl = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : System.getProperty("DB_URL");
                String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : System.getProperty("DB_USER");
                String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : System.getProperty("DB_PASSWORD");
                
                
            

                Map<String, String> properties = new HashMap<>();
                properties.put("jakarta.persistence.jdbc.url", dbUrl);
                properties.put("jakarta.persistence.jdbc.user", dbUser);
                properties.put("jakarta.persistence.jdbc.password", dbPassword);
                properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
                properties.put("jakarta.persistence.schema-generation.database.action", "update");

                emf = Persistence.createEntityManagerFactory("emailListPU", properties);
                
            } catch (Exception e) {
                System.err.println("Lỗi khởi tạo JPA: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return emf;
    }

    public static void closeEmf() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}