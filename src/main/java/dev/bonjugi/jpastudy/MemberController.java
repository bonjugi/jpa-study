package dev.bonjugi.jpastudy;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

  @CrossOrigin(origins = {
    "http://localhost:8259",
    "https://location.dev1.meshdev.io",
    "https://location.qa1.meshdev.io",
    "https://location.qa2.meshdev.io",
    "https://location.qa3.meshdev.io",
    "https://location.vroong.com"},
    allowedHeaders = {"X-PINGOTHER", "Content-Type"},
    methods = {POST, GET, OPTIONS},
    maxAge = 86400
    )
  @GetMapping("/hello")
  public String hello() {
    return "hello";
  }
}
