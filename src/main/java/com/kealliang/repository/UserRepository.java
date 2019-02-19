package com.kealliang.repository;

import com.kealliang.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * @author lsr
 * @ClassName UserRepository
 * @Date 2019-01-30
 * @Desc repository映射接口
 * @Vertion 1.0
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserByName(String name);
}
