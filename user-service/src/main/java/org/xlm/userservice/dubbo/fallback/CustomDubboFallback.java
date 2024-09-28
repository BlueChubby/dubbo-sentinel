package org.xlm.userservice.dubbo.fallback;

import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DubboFallback;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;

import java.util.HashMap;
import java.util.Map;

public class CustomDubboFallback implements DubboFallback {

    @Override
    public Result handle(Invoker<?> invoker, Invocation invocation, BlockException ex) {
        String message = dealWithBlockEx(ex);
        //System.out.println("服务被限流或熔断，进入降级处理逻辑：" + message);
        // 返回一个默认的响应数据，例如返回一个默认的 HashMap
        Map<String, String> defaultResponse = new HashMap<>();
        defaultResponse.put("message", message);
        defaultResponse.put("status", "-1");
        // 返回默认响应，而不是抛出异常
        return AsyncRpcResult.newDefaultAsyncResult(defaultResponse, invocation);
    }

    private static String dealWithBlockEx(BlockException ex) {
        String message;
        if (ex instanceof FlowException) {
            message = "请求过多，已被限流";
        } else if (ex instanceof DegradeException) {
            message = "服务熔断，暂时不可用";
        } else if (ex instanceof ParamFlowException) {
            message = "热点参数限流，访问频率过高";
        } else if (ex instanceof SystemBlockException) {
            message = "系统保护限流，资源使用率过高";
        } else if (ex instanceof AuthorityException) {
            message = "访问未授权，禁止访问";
        } else {
            message = "请求被限流或降级，原因：" + ex.getMessage();
        }
        return message;
    }
}