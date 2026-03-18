package txu.report.mainapp.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService  {
//public class CustomUserDetailsService implements UserDetailsService {

//    private final AccountDao accountDao;
//
//    @Override
//    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        AccountEntity user = accountDao.getByUsername(username);
//
//        if (user == null) {
//            log.error("User not found");
//            return null;
//        }
//        // Bỏ roles
//
//////        String[] roles = user.getRole().split(",");
////        String[] roles = user.getRole().getName().split(","); // Tạm giữ logic cũ, ở đây là môt chuỗi gồm các role cách nhau  bởi dâu phẩy
////        // Trên thực tế đây là một role duy nhất
//
////        List<GrantedAuthority> authorities = Arrays.stream(roles)
////                .map(String::trim)
////                .map(SimpleGrantedAuthority::new)
////                .collect(Collectors.toList());
//
////        return User.withUsername(user.getUsername()).password(user.getPassword()).roles(roles).build();
//
////        return new CustomUserDetails(user.getId(),user.getUsername(),user.getPassword(), user.getEmail(), user.getDepartment().getId(), authorities);
//        return new CustomUserDetails(user.getId(),user.getUsername(),user.getPassword(), user.getEmail(), user.getDepartment().getId());
//    }

}
