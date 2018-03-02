package org.softwire.training.db.daos.searchcriteria;

import java.util.Collections;
import java.util.Map;

public final class AgentCallsignSearchCriterion extends ReportSearchCriterion {

    private static final String CALL_SIGN_BINDING_NAME = "call_sign_sc_call_sign";
    private final String callSign;

    public AgentCallsignSearchCriterion(String callSign) {
        this.callSign = callSign;
    }

    @Override
    public String getSqlForWhereClause() {
        return "call_sign = :" + CALL_SIGN_BINDING_NAME;
    }

    @Override
    public Map<String, Object> getBindingsForSql() {
        return Collections.singletonMap(CALL_SIGN_BINDING_NAME, callSign);
    }
}
