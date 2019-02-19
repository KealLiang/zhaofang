package com.kealliang.service.impl;

import com.kealliang.base.ServiceResult;
import com.kealliang.dto.UserDTO;
import com.kealliang.entity.Role;
import com.kealliang.entity.User;
import com.kealliang.repository.RoleRepository;
import com.kealliang.repository.UserRepository;
import com.kealliang.service.UserService;
import com.kealliang.utils.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lsr
 * @ClassName UserServiceImpl
 * @Date 2019-01-31
 * @Desc
 * @Vertion 1.0
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public User findUserByUsername(String userName) {
        User user = userRepository.findUserByName(userName);
        if (user == null) {
            return null;
        }

        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        if (CollectionUtils.isEmpty(roles)) {
            throw new DisabledException("权限数据异常！");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorityList(authorities);
        return user;
    }

    @Override
    public ServiceResult<UserDTO> findById(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ServiceResult.notFound();
        }
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ServiceResult.of(userDTO);
    }
}
