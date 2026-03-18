package txu.report.mainapp.base;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import txu.common.exception.TxException;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class AbstractApi {

    private static final String HTTP_RESPONSE_ERROR_BODY_JSON_FORMAT = "{ \"errorType\": \"%s\", \"errorMessage\": \"%s\" }";


//    @ExceptionHandler(ForbiddenActionException.class)
//    @ResponseBody
//    public void handleForbiddenActionException(Throwable tr, HttpServletResponse response) throws IOException {
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        writeError(response, tr);
//    }
//
//    @ExceptionHandler({InvalidCredentialException.class, AuthenticationException.class})
//    @ResponseBody
//    public void handleInvalidCredentialException(Throwable tr, HttpServletResponse response) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        writeError(response, tr);
//    }
//
//    @ExceptionHandler(ConflictException.class)
//    @ResponseBody
//    public void handleConflictParam(
//            Throwable exception, HttpServletResponse response) throws IOException {
//
//        response.setStatus(HttpServletResponse.SC_CONFLICT);
//        writeError(response, exception);
//    }

    /**
     * For missing required parameters.
     */
//    @ExceptionHandler(ServletRequestBindingException.class)
//    @ResponseBody
//    public void handleServletRequestBindingException(
//            ServletRequestBindingException exception, HttpServletResponse response) throws IOException {
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        writeError(response, exception);
//    }
//
//    /**
//     * For input wrong parameter value such as 'abc' for boolean, '12a' for integer.
//     */
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    @ResponseBody
//    public void handleArgumentTypeMismatchException(
//            MethodArgumentTypeMismatchException exception, HttpServletResponse response) throws IOException {
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        String message = StringUtils.join("Failed to convert value of parameter '", exception.getName(),
//                "' to type ", exception.getRequiredType().getSimpleName());
//        writeError(response, exception.getClass().getSimpleName(), message);
//    }
//
//    /**
//     * For missing required multipart parameter.
//     */
//    @ExceptionHandler(MissingServletRequestPartException.class)
//    @ResponseBody
//    public void handleMissingServletRequestPartException(
//            MissingServletRequestPartException exception, HttpServletResponse response) throws IOException{
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        writeError(response, exception);
//    }
//
//    /**
//     * For uploading too big file error (exceed 35MB)
//     */
//    @ExceptionHandler(MultipartException.class)
//    @ResponseBody
//    public void handleMultipartException(
//            MultipartException exception, HttpServletResponse response) throws IOException{
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        writeError(response, exception);
//    }
//
//    /**
//     * The json format is incorrect.
//     */
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    @ResponseBody
//    public void handleHttpMessageNotReadableException(
//            HttpMessageNotReadableException exception, HttpServletResponse response) throws IOException {
//
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        writeError(response, exception);
//    }
    @ExceptionHandler(TxException.class)
    @ResponseBody
    public void handleTxRuntimeException(TxException exceptionRaised, HttpServletResponse response) throws IOException {
        if (exceptionRaised.getStatusCode() >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            log.error("Exception handled by spring, will return status code error " + exceptionRaised.getStatusCode() + ":\r\n" + exceptionRaised.getMessage());
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Exception handled by spring, will return status code error " + exceptionRaised.getStatusCode() + ":\n" + exceptionRaised.getMessage());
            }
        }
        response.setStatus(exceptionRaised.getStatusCode());
        writeError(response, exceptionRaised);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public void handleException(Exception e, HttpServletResponse response) throws IOException {
        log.error("Exception handled by Spring, return status code 500:\r\n" + e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        writeError(response, e);
    }

    private void writeError(HttpServletResponse response, Throwable throwable) throws IOException {
        writeError(response, throwable.getClass().getSimpleName(), throwable.getMessage());
    }

//    private void writeError(HttpServletResponse response, String className, String message) throws IOException {
//        String body = String.format(
//                HTTP_RESPONSE_ERROR_BODY_JSON_FORMAT, className, message);
//        response.setContentType("application/json;charset=UTF-8");
//        IOUtils.write(body, response.getOutputStream(), StandardCharsets.UTF_8);
//    }

    // Trong Java, bạn có thể dùng thư viện Jackson hoặc Gson để đảm bảo build JSON đúng chuẩn thay vì tự format string
    // Cách này giúp build json đúng định dạng khi chuỗi đầu vào có chứa các ký tự đặc biệt (nhất là khi dùng ex.getMessage()
    // Json nếu trả về koông đúng định dạng sẽ làm frontend gặp lỗi khi parse json khi fetch

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private void writeError(HttpServletResponse response, String className, String message) throws IOException {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorType", className);
        errorMap.put("errorMessage", message);

        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getOutputStream(), errorMap);
    }

}
