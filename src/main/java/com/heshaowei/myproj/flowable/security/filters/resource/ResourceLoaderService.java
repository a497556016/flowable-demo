package com.heshaowei.myproj.flowable.security.filters.resource;

import org.springframework.security.access.ConfigAttribute;

import java.util.List;
import java.util.Map;

public interface ResourceLoaderService {
    Map<String, List<? extends ConfigAttribute>> loadResources();
}
