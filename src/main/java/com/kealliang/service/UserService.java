package com.kealliang.service;

import com.kealliang.base.ServiceResult;
import com.kealliang.dto.UserDTO;
import com.kealliang.entity.User;

/**
 * @author lsr
 * @ClassName UserService
 * @Date 2019-01-31
 * @Desc
 * @Vertion 1.0
 */
public interface UserService {

    User findUserByUsername(String userName);

    ServiceResult<UserDTO> findById(Long userId);
}
