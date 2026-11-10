package gym_plus.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gym_plus.model.Usuario;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final ObjectMapper mapper;
    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(ObjectMapper mapper,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds:28800}") long expirationSeconds) {
        this.mapper = mapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
        if (this.secret.length < 32) throw new IllegalStateException("app.jwt.secret deve ter pelo menos 32 caracteres.");
    }

    public String gerar(Usuario usuario, String tipo) {
        try {
            String header = encode(mapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
            String payload = encode(mapper.writeValueAsBytes(Map.of(
                "sub", usuario.getEmail(), "role", tipo,
                "iat", Instant.now().getEpochSecond(),
                "exp", Instant.now().plusSeconds(expirationSeconds).getEpochSecond())));
            String content = header + "." + payload;
            return content + "." + sign(content);
        } catch (Exception e) { throw new IllegalStateException("Falha ao gerar token.", e); }
    }

    public Map<String, Object> validar(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !constantTimeEquals(sign(parts[0] + "." + parts[1]), parts[2])) return null;
            Map<String, Object> claims = mapper.readValue(Base64.getUrlDecoder().decode(parts[1]), Map.class);
            Object exp = claims.get("exp");
            if (!(exp instanceof Number) || ((Number) exp).longValue() <= Instant.now().getEpochSecond()) return null;
            return claims;
        } catch (Exception e) { return null; }
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return encode(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }
    private String encode(byte[] value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value); }
    private boolean constantTimeEquals(String a, String b) {
        return java.security.MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
