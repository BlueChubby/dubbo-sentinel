package org.xlm.fileservice.dubbo.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.google.common.collect.Maps;
import org.apache.dubbo.config.annotation.DubboService;
import org.xlm.dubbo.service.file.FileRemoteService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@DubboService(version = "1.0.0", group = "${dubbo.consumer.group}")
public class FileRemoteServiceImpl implements FileRemoteService {

    @Override
    public String testPing() {
        return "ping success";
    }
    //注解模式服务端熔断
    private static final String LIMIT_RESOURCE_NAME = "queryAllFiles";

    @PostConstruct
    public void initDegradeRules() {
        // 初始化流控规则
        final List<FlowRule> flowRules = new ArrayList<>();
        final List<DegradeRule> degradeRules = new ArrayList<>();
        // 限流规则
        final FlowRule flowRule = new FlowRule();
        flowRule.setResource(LIMIT_RESOURCE_NAME);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 1 QPS
        flowRule.setCount(100);
        flowRules.add(flowRule);
        // 熔断规则
        final DegradeRule degradeRule = new DegradeRule();
        //过去 10 秒内至少有 20 个请求，其中 5 个异常时触发熔断，熔断持续时间为 2 秒
        degradeRule.setResource(LIMIT_RESOURCE_NAME);
        degradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        degradeRule.setCount(5); // 异常数阈值
        degradeRule.setTimeWindow(2); // 熔断持续时间，单位为秒
        degradeRule.setMinRequestAmount(20); // 最小请求数
        degradeRule.setStatIntervalMs(10 * 1000); // 统计间隔，单位为毫秒
        degradeRules.add(degradeRule);

        FlowRuleManager.loadRules(flowRules);
        DegradeRuleManager.loadRules(degradeRules);
    }


    @SentinelResource(value = LIMIT_RESOURCE_NAME, blockHandler = "handleBlockException", fallback = "fallbackMethod")
    @Override
    public Map<String, String> queryAllFiles(int count) {
        if (count < 15) {
            throw new RuntimeException("请求异常");
        }
        Map<String, String> result = Maps.newHashMap();
        result.put("root", "/home/test/test.txt");
        return result;
    }


    // 熔断触发时的处理方法
    public Map<String, String> handleBlockException(int count, BlockException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Request Blocked: 服务不可用");
        if (ex instanceof DegradeException) {
            response.put("message", "服务熔断");
        }
        return response;
    }

    // 降级处理逻辑（发生异常时触发）
    public Map<String, String> fallbackMethod(int count, Throwable ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Fallback: 服务降级，默认返回");
        return response;
    }

    @Override
    public Map<String, String> queryAllFilesPath(int count) {
        if (count < 15) {
            throw new RuntimeException("请求异常");
        }
        Map<String, String> result = Maps.newHashMap();
        result.put("file_path", "/home/path/path.txt");
        return result;
    }
}
