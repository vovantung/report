package txu.report.mainapp.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
//
//public class CorsFilter implements Filter {
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) res;
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, *");
//        response.setHeader("Access-Control-Max-Age", "300");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With, *");
//        chain.doFilter(req, res);
//    }
//}

@Component
//public class CorsFilter_ {
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000"); // Không dùng '*'
        response.setHeader("Access-Control-Allow-Origin", "*"); // Không dùng '*'
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Content-Length, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");

        // Khi sử dụng kong với các cấu hình đã xử lý request options rồi thì không cần đến xử lý này.
        // Tuy nhiên, với quá trình test không qua kong thì cần xử lý request option này.
        // Nếu không xử lý request option và trả về kết quả 200 sớm, thì request sẽ đi qua filter của spring security và bị xử lý
        // chặn ở đó, khiến trình duyệt sẽ không gửi request thật hoặc gửi request thật không chứa token. Gây ảnh hướng quá trình ấy thông tin
        // từ token, mặc dù đây không làm nhiệm vụ xác thực phân quyền, nhưng vẫn cần thông tin người dùng chứa trong token cho một só xử lý nghiệp
        // vụ cần tính bảo mật, chẳng hạn chỉ cho người dùng truy cập những thông tin liên quan họ

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}

