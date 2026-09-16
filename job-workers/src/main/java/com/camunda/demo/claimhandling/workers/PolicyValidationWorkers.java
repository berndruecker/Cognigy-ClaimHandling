package com.camunda.demo.claimhandling.workers;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/** Workflow 1/6: Policy Validation (bpmn/policy-validation.bpmn). */
@Component
public class PolicyValidationWorkers {

  // Coverage types this (fictitious) policy wording excludes outright, regardless of tier.
  private static final Set<String> EXCLUDED_COVERAGE_TYPES = Set.of("flood", "earthquake", "sewer_backup", "neglect");

  @JobWorker(type = "check-policy-status")
  public Map<String, Object> checkPolicyStatus(
      @Variable("customer") Map<String, Object> customer, @Variable("claim") Map<String, Object> claim) {
    String policyNumber = Vars.str(customer, "policy_number", null);
    String coverageType = Vars.str(claim, "coverage_type", "").toLowerCase(Locale.ROOT);

    String policyStatus = (policyNumber != null && !policyNumber.isBlank()) ? "ACTIVE" : "LAPSED";
    boolean exclusionsApply = EXCLUDED_COVERAGE_TYPES.contains(coverageType);

    return Map.of(
        "policy_status", policyStatus,
        "exclusions_apply", exclusionsApply);
  }
}
