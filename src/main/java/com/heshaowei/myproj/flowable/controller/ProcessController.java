package com.heshaowei.myproj.flowable.controller;

import com.google.common.collect.Maps;
import com.heshaowei.myproj.flowable.common.GlobalConstant;
import com.heshaowei.myproj.flowable.service.BaseFlowService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.flowable.engine.impl.form.FormPropertyImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormModel;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.form.rest.service.api.form.FormModelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(GlobalConstant.API_VERSION+"/process")
public class ProcessController {
    @Autowired
    private BaseFlowService flowService;

    @GetMapping("/getStartFormModel")
    public ResponseEntity<FormModel> getStartFormModel(String processDefinitionKey){
        FormModel formModel = flowService.getProcessStartForm(processDefinitionKey);
        return ResponseEntity.ok(formModel);
    }

    @PostMapping("/startProcessWithForm")
    public ResponseEntity<String> startProcessWithForm(String processDefinitionKey, @RequestBody SimpleFormModel formModel){
        Map<String, Object> formProperties = Maps.newHashMap();
        formModel.getFields().forEach(formField -> {
            if(!formField.getType().equals("headline-with-line")) {
                if(formField.getType().equals("people")) {
                    Map<String, Object> people = Maps.newHashMap();
                    people.put("id", formField.getValue());
                    formProperties.put(formField.getId(), people);
                }else {
                    formProperties.put(formField.getId(), formField.getValue());
                }
            }
        });
        ProcessInstance processInstance = flowService.startProcessWithForm(processDefinitionKey, null, formProperties, "我呀请呀假呀("+ DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") +")");
        return ResponseEntity.ok(processInstance.getId());
    }
}
