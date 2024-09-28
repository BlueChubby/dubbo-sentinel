package org.xlm.userservice.starter;

import com.alibaba.csp.sentinel.adapter.dubbo.config.DubboAdapterGlobalConfig;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.xlm.dubbo.service.file.FileRemoteService;
import org.xlm.userservice.dubbo.fallback.CustomDubboFallback;
import org.xlm.utils.DubboServiceUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class UserServiceStarter implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(UserServiceStarter.class);

    @DubboReference(version = "1.0.0", group = "${dubbo.consumer.group}")
    private FileRemoteService fileRemoteService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String pingResult = fileRemoteService.testPing();
        log.warn("dubbo service ping result:{}", pingResult);
        testSentinelWithAnnotation();
        testSentinelWithDubboAdapter();
    }

    private void testSentinelWithAnnotation() {
        for (int i = 0; i < 100; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
                Map<String, String> allFiles = fileRemoteService.queryAllFiles(i);
                log.info("file service queryAllFiles 响应结果: {}", allFiles);
            } catch (Exception e) {
                //log.error("请求异常:",e);
                log.error("请求异常:{}", e.getMessage());
            }
        }
        /*
         * 过去 10 秒内至少有 20 个请求，其中 5 个异常时触发熔断，熔断持续时间为 2 秒
         * 1.file service 响应结果: {message=Fallback: 服务降级，默认返回}
         * 2.file service 响应结果: {root=/home/test/test.txt}
         * 3.file service 响应结果: {message=服务熔断}   2秒熔断过后正常响应
         * 4.file service 响应结果: {root=/home/test/test.txt}
         * */
    }

    private void testSentinelWithDubboAdapter() {
        for (int i = 0; i < 100; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
                Map<String, String> allFiles = fileRemoteService.queryAllFilesPath(i);
                log.info("file service queryAllFilesPath 响应结果: {}", allFiles);
            } catch (Exception e) {
                //log.error("rpc 接口异常:", e);
                log.error("rpc 接口异常: {}", e.getMessage());
            }
        }
    }

    private static final String DUBBO_REMOTE_SERVICE = DubboServiceUtil.getMethodSignature(FileRemoteService.class, "queryAllFilesPath", int.class);

    @PostConstruct
    public void initDegradeRules() {
        // 初始化流控规则
        final List<FlowRule> flowRules = new ArrayList<>();
        final List<DegradeRule> degradeRules = new ArrayList<>();
        // 限流规则
        final FlowRule flowRule = new FlowRule();
        flowRule.setResource(DUBBO_REMOTE_SERVICE);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 1 QPS
        flowRule.setCount(10);
        flowRules.add(flowRule);
        // 熔断规则
        final DegradeRule degradeRule = new DegradeRule();
        //过去 10 秒内至少有 20 个请求，其中 5 个异常时触发熔断，熔断持续时间为 2 秒
        degradeRule.setResource(DUBBO_REMOTE_SERVICE);
        degradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        degradeRule.setCount(5); // 异常数阈值
        degradeRule.setTimeWindow(2); // 熔断持续时间，单位为秒
        degradeRule.setMinRequestAmount(20); // 最小请求数
        degradeRule.setStatIntervalMs(10 * 1000); // 统计间隔，单位为毫秒
        degradeRules.add(degradeRule);

        FlowRuleManager.loadRules(flowRules);
        DegradeRuleManager.loadRules(degradeRules);
        // 全局 Fallback 函数
        DubboAdapterGlobalConfig.setConsumerFallback(new CustomDubboFallback());
    }

}
