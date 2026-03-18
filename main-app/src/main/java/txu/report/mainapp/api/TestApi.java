package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.TestRequest;
import txu.report.mainapp.service.AccountService;
import txu.report.mainapp.service.DepartmentService;
import txu.report.mainapp.service.MessageProducer;


@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class TestApi extends AbstractApi {

    private final DepartmentService departmentService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageProducer messageProducer;
    private final AccountService accountService;


//    @PostMapping(value = "get-by-id")
////    @Cacheable(value = "department", key = "#request.id")
//    public DepartmentEntity getById(@RequestBody IdRequest request){
//        return  departmentService.getById(request.getId());
//    }



    @PostMapping(value = "send-message")
    public void send() {
        String str = "Vo Thi Ngoc Uyen";
        kafkaTemplate.send("orders-events", str);
    }

    @PostMapping(value = "send-message-activemq")
    public void send_activemq(@RequestBody TestRequest request) {
        messageProducer.send(request.getStr());
    }

    @GetMapping(value = "/test")
    public String test() {
        return "Phan Thi Xuyen";
    }



}
