package com.heshaowei.myproj.flowable.service;

import com.heshaowei.myproj.flowable.utils.SpringUtil;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.el.FixedValue;
import org.springframework.web.context.ContextLoader;

import java.util.Map;

public class TestVacationFormDelegate implements JavaDelegate {
    private FixedValue success;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        boolean isSuccess = success.getValue(delegateExecution).equals("true");
        System.out.println("请假结果："+isSuccess);

        Map<String, Object> variables = delegateExecution.getVariables();
        variables.forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
        delegateExecution.setVariable("vacationPassed", isSuccess, false);
    }

    public FixedValue getSuccess() {
        return success;
    }

    public void setSuccess(FixedValue success) {
        this.success = success;
    }
}
