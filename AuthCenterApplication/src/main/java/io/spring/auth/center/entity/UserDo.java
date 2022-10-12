package io.spring.auth.center.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author kdyzm
 */
@Data
@TableName("t_user")
public class UserDo {

    private Integer id;

    private String username;

    private String password;

    private String fullname;

    private String mobile;

    private String email;
}
