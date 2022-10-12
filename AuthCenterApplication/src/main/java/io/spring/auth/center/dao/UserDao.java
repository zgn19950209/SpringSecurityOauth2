package io.spring.auth.center.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.spring.auth.center.entity.UserDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<UserDo> {

    @Select("SELECT DISTINCT tp.`code` FROM `t_user_role` tur \n" +
            "INNER JOIN `t_role_permission` trp ON tur.`role_id` = trp.`role_id`\n" +
            "INNER JOIN `t_permission` tp ON trp.`permission_id` = tp.`id`\n" +
            "WHERE tur.`user_id` = #{userId};")
    List<String> findAllPermissions(@Param("userId") Integer userId);
}
