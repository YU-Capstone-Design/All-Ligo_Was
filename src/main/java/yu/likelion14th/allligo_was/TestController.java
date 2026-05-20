package yu.likelion14th.allligo_was;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Backend server is running!";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}