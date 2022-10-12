package io.spring.auth.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.spring.auth.center.dao.UserDao;
import io.spring.auth.center.dto.UserDTO;
import io.spring.auth.center.entity.UserDo;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 自定义用户信息
 * @Created 2022/10/12 2:52
 **/

@Service
public class CustomizeUserDetailsServiceImpl extends ServiceImpl<UserDao, UserDo>  implements UserDetailsService {
    @Resource
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDo tUserDo = userDao.selectOne(new LambdaQueryWrapper<UserDo>().eq(UserDo::getUsername, username));
        if (Objects.isNull(tUserDo)) {
            throw new UsernameNotFoundException(username + "账号不存在");
        }
        List<String> allPermissions = userDao.findAllPermissions(tUserDo.getId());
        String[] array = allPermissions.toArray(new String[allPermissions.size()]);

        UserDTO userDetailsExpand = new UserDTO(tUserDo.getUsername(), tUserDo.getPassword(), AuthorityUtils.createAuthorityList(array));
        userDetailsExpand.setId(tUserDo.getId());
        userDetailsExpand.setEmail(tUserDo.getEmail());
        userDetailsExpand.setMobile(tUserDo.getMobile());
        userDetailsExpand.setFullname(tUserDo.getFullname());
        return userDetailsExpand;
    }
}
