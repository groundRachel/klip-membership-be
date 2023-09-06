package com.klipwallet.membership.config;

import org.apache.commons.text.CaseUtils;

/**
 * 배포 환경
 */
public enum DeployEnv {
    LOCAL,
    DEV,
    QA,
    STAG,
    PROD;

    public String toDisplay() {
        return CaseUtils.toCamelCase(name(), false, '_');
    }
}
