package com.heshaowei.myproj.flowable;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.heshaowei.myproj.flowable.service.BaseFlowService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.flowable.engine.IdentityService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.form.api.FormModel;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FlowableApplication.class)
public class TestProcessTest {
    private Gson gson = new Gson();

    @Autowired
    private BaseFlowService flowService;

    @Autowired
    private IdentityService identityService;


    @Test
    public void createUser() {
        User user = identityService.newUser("heshaowei");
        user.setDisplayName("何少伟");
        user.setFirstName("少伟");
        user.setLastName("何");
        user.setEmail("heshaowei_code@163.com");
        user.setPassword("123456");
        identityService.saveUser(user);
        identityService.createMembership(user.getId(), "ITDepartment");

        user = identityService.newUser("xiaohong");
        user.setDisplayName("小红");
        user.setFirstName("红");
        user.setLastName("小");
        user.setEmail("xiaohong@163.com");
        user.setPassword("123456");
        identityService.saveUser(user);
        identityService.createMembership(user.getId(), "ITDepartment");

        user = identityService.newUser("xiaobai");
        user.setDisplayName("小白");
        user.setFirstName("白");
        user.setLastName("小");
        user.setEmail("xiaobai@163.com");
        user.setPassword("123456");
        identityService.saveUser(user);
        identityService.createMembership(user.getId(), "executiveDepartment");

        user = identityService.newUser("xiaohei");
        user.setDisplayName("小黑");
        user.setFirstName("黑");
        user.setLastName("小");
        user.setEmail("xiaohei@163.com");
        user.setPassword("123456");
        identityService.saveUser(user);
        identityService.createMembership(user.getId(), "managerDepartment");
    }

    @Test
    public void createGroup() {
        Group group = identityService.newGroup("executiveDepartment");
        group.setName("行政部门");
        group.setType("department");
        identityService.saveGroup(group);

        group = identityService.newGroup("ITDepartment");
        group.setName("IT信息部门");
        group.setType("department");
        identityService.saveGroup(group);

        group = identityService.newGroup("managerDepartment");
        group.setName("总经理办公室");
        group.setType("manage");
        identityService.saveGroup(group);
    }

    @Test
    public void removeUser(){
        identityService.deleteUser("heshaowei");
        identityService.deleteMembership("heshaowei", "ITDepartment");

        identityService.deleteUser("xiaohong");
        identityService.deleteMembership("xiaohong", "ITDepartment");

        identityService.deleteUser("xiaobai");
        identityService.deleteMembership("xiaobai", "executiveDepartment");

        identityService.deleteUser("xiaohei");
        identityService.deleteMembership("xiaohei", "managerDepartment");
    }

    @Test
    public void test() throws ParseException {
        String businessKey = DateFormatUtils.format(new Date(), "yyyyMMddHHmm");
        Map<String, Object> formValues = Maps.newHashMap();
        formValues.put("vacationType", "事假");
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        formValues.put("startDate", formatter.parseLocalDate("2019-12-26 14:00").toString());
        formValues.put("endDate", formatter.parseLocalDate("2019-12-27 14:00").toString());
        formValues.put("days", 3);
        formValues.put("reason", "肚子饿了");
        formValues.put("validFile", "");
        Map<String, Object> firstLeader = Maps.newHashMap();
        firstLeader.put("id", "xiaohong");
        formValues.put("firstLeader", firstLeader);
        ProcessInstance pi = flowService.startProcessWithForm("TestProcess", "heshaowei", formValues, "请个假（"+businessKey+"）");
        System.out.println(pi);

        List<Task> tasks = flowService.getTasks("xiaohong");
        System.out.println(tasks);

        //直属领导审批
        Task task = flowService.getCurTask(pi.getId());
        Map<String, Object> taskVariables = Maps.newHashMap();
        taskVariables.put("isAgree", "同意");
        taskVariables.put("remark", "同意请假，快去快回！");
        flowService.completeTask(task.getId(), taskVariables);

        //行政审批
        task = flowService.getCurTask(pi.getId());
        System.out.println(task);
        taskVariables.clear();
        taskVariables.put("isAgree", "同意");
        taskVariables.put("remark", "行吧！");
        flowService.completeTask(task.getId(), taskVariables);

        task = flowService.getCurTask(pi.getId());
        System.out.println(task);

        //委派任务
        flowService.delegateTask(task.getId(), null, "xiaoliu");
        //完成委派任务
        taskVariables.clear();
        taskVariables.put("isAgree", "不同意");
        taskVariables.put("remark", "不行吧！");
        flowService.completeTask(task.getId(), taskVariables);

        task = flowService.getCurTask(pi.getId());
        System.out.println(task);
    }

    @Test
    public void from() {
        FormModel formModel = flowService.getProcessStartForm("TestProcess");
        System.out.println(gson.toJson(formModel));
    }

    @Test
    @Rollback(false)
    public void removeProcessDeploy() {
        flowService.removeAllProcessDefinition("TestProcess");
    }

}
