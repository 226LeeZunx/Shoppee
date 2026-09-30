package murach.controller;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MailUtilGmail {

    public static void sendMail(String to, String from,
            String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        try {
            String apiKey = null;
            
            // 1. Thử lấy từ Dotenv
            try {
                Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
                apiKey = dotenv.get("BREVO_API_KEY");
            } catch (Exception e) {}

            // 2. Lấy từ biến môi trường (Render) hoặc System Properties (đã được DBUtil nạp)
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = System.getenv("BREVO_API_KEY");
            }
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = System.getProperty("BREVO_API_KEY");
            }

            
            String cleanFrom = from.trim();
            String cleanTo = to.trim();
            String cleanSubject = subject.replace("\"", "\\\"");
            String cleanBody = body.replace("\\", "\\\\")
                                     .replace("\"", "\\\"")
                                     .replace("\n", "\\n")
                                     .replace("\r", "");

            String jsonPayload = "{"
                    + "\"sender\":{\"email\":\"" + cleanFrom + "\"},"
                    + "\"to\":[{\"email\":\"" + cleanTo + "\"}],"
                    + "\"subject\":\"" + cleanSubject + "\","
                    + (bodyIsHTML ? "\"htmlContent\":\"" : "\"textContent\":\"") + cleanBody + "\""
                    + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new MessagingException("Lỗi từ Brevo (Mã " + response.statusCode() + "): " + response.body());
            }

        } catch (Exception e) {
            throw new MessagingException(e.getMessage(), e);
        }
    }
}