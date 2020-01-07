package com.heshaowei.myproj.flowable.service;

import com.google.common.collect.Maps;
import com.heshaowei.myproj.flowable.bean.TestProcessAssignUsers;
import com.heshaowei.myproj.flowable.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Proc;
import org.flowable.engine.*;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormModel;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.service.IdentityLinkService;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BaseFlowService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private FormService formService;

    @Autowired
    private FormRepositoryService formRepositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Transactional
    public ProcessInstance startProcess(String processDefKey, String businessKey, String startUserId, Map<String, Object> variables, String processInstanceName) {
        identityService.setAuthenticatedUserId(startUserId);

//        variables.put("assignUsers", new TestProcessAssignUsers("heshaowei", "admin"));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefKey, businessKey, variables);
//        Task task = this.getCurTask(processInstance.getId());
//        System.out.println("当前任务："+task);
//        taskService.setAssignee(task.getId(), firstAssign);
        if(null != processInstanceName) {
            runtimeService.setProcessInstanceName(processInstance.getId(), processInstanceName);
        }
        return processInstance;
    }

    @Transactional
    public ProcessInstance startProcess(String processDefKey, String businessKey, String startUserId, Map<String, Object> variables) {
        return startProcess(processDefKey, businessKey, startUserId, variables, null);
    }


    public FormModel getProcessStartForm(String processDefinitionKey){
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
        StartFormData form = formService.getStartFormData(pd.getId());

        FormInfo info = formRepositoryService.getFormModelByKey(form.getFormKey());
        return info.getFormModel();
    }

    public void completeTask(String taskId, String nextUserId, String message) {
        completeTask(taskId, nextUserId, null, null, message);
    }

    public void completeTask(String taskId, String nextUserId, String nextGroupId, String message) {
        completeTask(taskId, nextUserId, nextGroupId, null, message);
    }

    public void completeTask(String taskId, String nextUserId) {
        completeTask(taskId, nextUserId, null, null, null);
    }

    public void completeTask(String taskId, String nextUserId, Map<String, Object> variables) {
        completeTask(taskId, nextUserId, null, variables, null);
    }

    public void completeTask(String taskId, String nextUserId, String nextGroupId, Map<String, Object> variables) {
        completeTask(taskId, nextUserId, nextGroupId, variables, null);
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        completeTask(taskId, null, null, variables, null);
    }

    public void completeTask(String taskId, String nextUserId, String nextGroupId, Map<String, Object> variables, String message) {


        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if(null != message) {
            taskService.addComment(taskId, task.getProcessInstanceId(), "suggestion", message);
        }
        if(null != variables) {
            taskService.setVariablesLocal(taskId, variables);
        }

        //委派任务
        if(null != task.getOwner()) {
            taskService.resolveTask(taskId);
        } else {
            claimTask(taskId);
        }

        String formDefKey = task.getFormKey();
        if(null != formDefKey) {
            FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery().formDefinitionKey(formDefKey).latestVersion().singleResult();
            taskService.setVariablesLocal(taskId, variables);
            taskService.completeTaskWithForm(taskId, formDefinition.getId(), null, variables);
        }else {
            taskService.complete(taskId, variables);
        }

        Task nextTask = getCurTask(task.getProcessInstanceId());
        setNextTaskCandidate(nextTask, nextUserId, nextGroupId);
    }

    public ProcessInstance startProcessWithForm(String processDefKey, String startUserId, Map<String, Object> variables, String processInstanceName) {
        identityService.setAuthenticatedUserId(startUserId);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefKey).latestVersion().singleResult();
        ProcessInstance instance = runtimeService.startProcessInstanceWithForm(processDefinition.getId(), null, variables, processInstanceName);

        return instance;
    }


    /**
     * 将任务委派给其他人
     * @param taskId
     * @param owner
     * @param userId
     */
    public boolean delegateTask(String taskId, String owner, String userId) {
        if(StringUtils.isBlank(owner)) {
            owner = UserUtil.getLoginUsername();
        }
        if(StringUtils.isBlank(owner)) {
            owner = getCandidateOrAssignUser(taskId);
        }
        if(StringUtils.isNotBlank(owner) && StringUtils.isNotBlank(taskId)) {
            taskService.setOwner(taskId, owner);
        }
        if(StringUtils.isNotBlank(taskId)) {
            taskService.delegateTask(taskId, userId);
            return true;
        }
        return false;
    }

    /**
     * 进行任务签收
     * @param taskId
     */
    private void claimTask(String taskId){
        String username = UserUtil.getLoginUsername();
        if(StringUtils.isBlank(username)) {
            username = getCandidateOrAssignUser(taskId);
        }
        if(StringUtils.isNotBlank(taskId) && StringUtils.isNotBlank(username)) {
            taskService.claim(taskId, username);
        }
    }

    private String getCandidateOrAssignUser(String taskId) {
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId).stream().filter(identityLink -> {
            String type = identityLink.getType();
            return IdentityLinkType.CANDIDATE.equals(type) || IdentityLinkType.ASSIGNEE.equals(type);
        }).collect(Collectors.toList());
        String userId = identityLinks.stream().findAny().map(IdentityLinkInfo::getUserId).orElse(null);

        if(null == userId) {
            String groupId = identityLinks.stream().map(IdentityLinkInfo::getGroupId).findAny().orElse(null);
            userId = identityService.createUserQuery().memberOfGroup(groupId).list().stream().findAny().map(User::getId).orElse(null);
        }

        return userId;
    }

    /**
     * 设置下一步任务候选人
     * @param task
     * @param nextUserId
     */
    private void setNextTaskCandidate(Task task, String nextUserId, String nextGroupId){
        if(null != task) {
            if(StringUtils.isNotBlank(nextUserId)) {
                taskService.addCandidateUser(task.getId(), nextUserId);
            }else if(StringUtils.isNotBlank(nextGroupId)) {
                taskService.addCandidateGroup(task.getId(), nextGroupId);
            }
        }
    }

    /**
     * 获取流程实例当前正待处理的任务
     * @param processInstanceId
     * @return
     */
    public Task getCurTask(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
    }

    @Transactional
    public List<Task> getTasks(String userId) {
        return taskService.createTaskQuery().taskCandidateOrAssigned(userId).list();
    }

    public void changeState(String instanceId, String activityId, String newActivityId) {
        runtimeService.createChangeActivityStateBuilder().processInstanceId(instanceId).moveActivityIdTo(activityId, newActivityId).changeState();
    }

    public void removeAllProcessDefinition(String processDefinitionKey) {
        repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).list().forEach(processDefinition -> {
            repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
        });
    }
}
