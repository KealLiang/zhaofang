package com.kealliang;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

// 配置单测时走-test配置
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

}

