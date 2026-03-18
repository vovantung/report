package txu.report.mainapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;


public class JwtUtils {

    public static Map<String,Object> decode(String token) throws Exception {

        String[] parts = token.split("\\.");

        String payload = new String(
                Base64.getUrlDecoder().decode(parts[1])
        );

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(payload, Map.class);
    }
}
