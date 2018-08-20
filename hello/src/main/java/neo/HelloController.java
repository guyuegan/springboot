package neo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController指明类中所有方法返回JSON串，否则要指定视图
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(){
        return "hello world";
    }
}
