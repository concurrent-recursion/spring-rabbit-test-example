package com.example.junitstuff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;


@Slf4j
public class TenantContext {
    private static final ThreadLocal<String> currentTenant  = new ThreadLocal<>();

    public String getTenantId() {
        return currentTenant.get();
    }

    public void setTenantId(@NonNull String tenantId) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        log.trace("Setting TenantId to '{}'",tenantId);
        currentTenant.set(tenantId);
    }

    public void clear(){
        log.trace("Clearing TenantContext");
        currentTenant.remove();
    }
}
