package org.xlm.userservice.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.xlm.dubbo.service.file.FileRemoteService;
import org.xlm.userservice.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @DubboReference(version = "1.0.0", group = "${dubbo.consumer.group}")
    private FileRemoteService fileRemoteService;

    @Override
    public List<String> getUsers() {
        Map<String, String> allFiles = fileRemoteService.queryAllFiles(0);
        return Collections.emptyList();
    }
}
