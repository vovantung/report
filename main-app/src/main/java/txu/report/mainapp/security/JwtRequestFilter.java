package txu.report.mainapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter {
//public class JwtRequestFilter extends OncePerRequestFilter {
//
//    private final CustomUserDetailsService customUserDetailsService;
//    private final JwtTokenUtil jwtTokenUtil;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        final String requestTokenHeader = request.getHeader("Authorization");
//
//        String username = null;
//        String jwtToken = null;
//        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
//        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
//            jwtToken = requestTokenHeader.substring(7);
//
//            // Decode nhẹ payload chứa thông tin người dùng ở đây thay vì phải xác thực như bên dưới. Có thể làm việc này an toàn vì
//            // Việc xác thực đã được thực hiện bới kong, nếu không phải thực hiện bước xác thực bên dưới thay vi decode nhẹ
//            Map<String, Object> payload = new HashMap<>();
//            try {
//                String[] parts = jwtToken.split("\\.");
//                if (parts.length != 3) {
//                    throw new IllegalArgumentException("Invalid JWT format");
//                }
//
//                String payloadString = new String(Base64.getUrlDecoder().decode(parts[1]));
//                ObjectMapper mapper = new ObjectMapper();
//                payload = mapper.readValue(payloadString, Map.class);
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to decode JWT payload", e);
//            }
//            username = payload.get("sub").toString();
//
////            try {
////                // Ở đây đã thực hiện cả xác thực người dùng với token, với secret
////                // Tuy nhiên, nếu chỉ sử sử dụng để lấy thông tin người dùng từ token
////                // thì chỉ cần decode nhẹ payload token là đủ
////                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
////            } catch (IllegalArgumentException e) {
////                log.error("Unable to get JWT Token");
////            } catch (ExpiredJwtException e) {
////                log.error("JWT Token has expired");
////            } catch (Exception e) {
////                log.error("Can not get user from JWT Token");
////            }
//        } else {
//            logger.warn("JWT Token does not begin with Bearer String");
//        }
//
//        //Once we get the token validate it.
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//            // if token is valid configure Spring Security to manually set authentication
////            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
////                UsernamePasswordAuthenticationToken authToken =
////                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
////                // authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////                // After setting the Authentication in the context, we specify
////                // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
////                SecurityContextHolder.getContext().setAuthentication(authToken);
////            }
//
//            UsernamePasswordAuthenticationToken authToken =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//        }
//        chain.doFilter(request, response);
//    }

}
