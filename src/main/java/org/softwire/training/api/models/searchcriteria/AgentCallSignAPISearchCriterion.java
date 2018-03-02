package org.softwire.training.api.models.searchcriteria;

import org.softwire.training.db.daos.searchcriteria.AgentCallsignSearchCriterion;

public class AgentCallSignAPISearchCriterion extends ApiReportSearchCriterionBase {

    public AgentCallSignAPISearchCriterion(String callSign) {
        super(new AgentCallsignSearchCriterion(callSign));
    }
}
