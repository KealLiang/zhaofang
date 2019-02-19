package com.kealliang.repository;

import com.kealliang.ApplicationTests;
import com.kealliang.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lsr
 * @ClassName UserRepositoryTest
 * @Date 2019-01-30
 * @Desc
 * @Vertion 1.0
 */
public class UserRepositoryTest extends ApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindById() {
        long id = 2L;
        User user = userRepository.findById(id).orElse(null);
        Assert.assertEquals("admin", user.getName());
    }
}
