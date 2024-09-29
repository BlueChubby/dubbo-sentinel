package org.xlm.userservice;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.xlm.dubbo.service.file.FileRemoteService;
import org.xlm.userservice.service.UserService;
import org.xlm.userservice.service.impl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceApplicationTests {

    @Mock
    FileRemoteService fileRemoteService;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void contextLoads() {
        Mockito.when(fileRemoteService.queryAllFiles(Mockito.any(int.class))).thenReturn(Maps.newHashMap());
        List<String> users = userService.getUsers();
        System.out.println("users = " + users);
    }

}
