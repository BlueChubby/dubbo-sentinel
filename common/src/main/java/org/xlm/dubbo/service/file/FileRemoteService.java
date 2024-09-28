package org.xlm.dubbo.service.file;


import java.util.Map;

public interface FileRemoteService {

    String testPing();

    Map<String, String> queryAllFiles(int count);

    Map<String, String> queryAllFilesPath(int count);
}
