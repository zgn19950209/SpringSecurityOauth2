package io.spring.resource.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Description:
 * @Created 2022/10/12 15:33
 **/
@RestController
@Slf4j
public class OrderController {

    /**
     * 由于4.4启用了prePostEnabled，所以这里可以使用@PreAuthorize注解对资源安全请求进行管理。
     *
     * @return
     */
    @GetMapping("/r1")
    @PreAuthorize("hasAnyAuthority('p1')")
    public String r1() {
        return "访问资源r1";
    }
}
