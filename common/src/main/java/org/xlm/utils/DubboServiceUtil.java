package org.xlm.utils;

import java.lang.reflect.Method;

public class DubboServiceUtil {
    public static String getMethodSignature(Class<?> serviceClass, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            StringBuilder signature = new StringBuilder();
            signature.append(serviceClass.getName()).append(":").append(method.getName()).append("(");
            for (int i = 0; i < parameterTypes.length; i++) {
                signature.append(parameterTypes[i].getSimpleName());
                if (i < parameterTypes.length - 1) {
                    signature.append(",");
                }
            }
            signature.append(")");
            return signature.toString();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found", e);
        }
    }
    
    public static void main(String[] args) {
        String signature = getMethodSignature(
            org.xlm.dubbo.service.file.FileRemoteService.class, 
            "queryAllFilesPath", 
            int.class
        );
        System.out.println(signature); // 输出: org.xlm.dubbo.service.file.FileRemoteService:queryAllFilesPath(int)
    }
}
