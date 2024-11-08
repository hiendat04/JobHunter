package vn.hiendat04.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hiendat04.jobhunter.util.error.IdInvalidException;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() throws IdInvalidException {
        if(true){
            throw new IdInvalidException("Thua roi m oi");
        }
        
        return "Hello World (Hỏi Dân IT & Eric)";
    }
}
