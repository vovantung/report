package txu.report.mainapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
//@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


//    private final JwtRequestFilter jwtRequestFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
////                .csrf(csrf -> csrf.disable())
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/public/**", "/authenticate", "/**").permitAll()
////                        .requestMatchers("/public/**", "/authenticate").permitAll()
////                        .requestMatchers( "/product/**").hasRole("admin")
//                        .anyRequest().authenticated()
//                );
////                .formLogin(form -> form
////                        .loginPage("/login")
////                        .permitAll()
////                )
////                .logout(logout -> logout
////                        .logoutUrl("/logout")
////                        .permitAll()
////                );
//
////        http.exceptionHandling(x -> x.authenticationEntryPoint(jwtAuthenticationEntryPoint))
////                .sessionManagement(y -> y.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        // Filter cho phép user đã xác thực thành công
//        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }



}