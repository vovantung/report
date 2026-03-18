package txu.report.mainapp.security;

import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


//    private final AuthenticationManager authenticationManager;
//    private final CustomUserDetailsService customUserDetailsService;
//    private final JwtTokenUtil jwtTokenUtil;
//
//    public JwtResponse authenticateUer(String username, String password) {
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//        if (userDetails == null) {
//            return null;
//        }
//
////        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
////        passwordEncoder.encode(password);
////        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
////            throw new BadParameterException("Password is not correct!");
////        }
//        // Xác thực người dùng đã được load vào UserDetails, tuy nhiên trong trường này không cần
//        // authenticate người dùng vì chỉ cần kiểm tra có phải người dùng hợp lệ hay không để tạo
//        // token đăng nhập cho người dùng này dùng trong các request tiếp sau đó.
//        Authentication authentication = authenticate(username, password);
//
//        return new JwtResponse(jwtTokenUtil.generateToken(userDetails));
//    }
//
//    public Authentication authenticate(String username, String password) throws AuthenticationException {
//        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
//        return authenticationManager.authenticate(authentication);
//    }

}