package txu.report.mainapp;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import txu.common.grpc.GrpcServer;
import txu.report.mainapp.grpc.HrmGrpcService;

import java.io.IOException;
import java.util.TimeZone;


@SpringBootApplication
@EnableCaching
public class MainAppApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        // SpringApplication.run(MainAppApplication.class, args);
        // Mặc định khi triển khai lên k8s, ứng dụng sẽ chạy với TimeZome mặc định (UTC), tức sẽ +/- thêm giờ khi map
        // dữ liệu từ cơ sở dữ liệu (gây sai lệnh giờ so với cơ sở dữ liệu). Do đó cần đặc Time Zone cho ứng dụng tương ứng với CSDL
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        GrpcServer.start(MainAppApplication.class, HrmGrpcService.class);
    }

}
