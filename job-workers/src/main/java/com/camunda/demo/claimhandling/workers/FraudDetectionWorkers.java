package com.camunda.demo.claimhandling.workers;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Workflow 3/6: Fraud Detection (bpmn/fraud-detection.bpmn). */
@Component
public class FraudDetectionWorkers {

  @JobWorker(type = "check-prior-claims")
  public Map<String, Object> checkPriorClaims(@Variable("claim") Map<String, Object> claim) {
    // Meaningful test default: Sarah is a first-time claimant on this property.
    int priorClaimsCount = 0;

    long timingAnomalyDays = 0;
    String incidentDateRaw = Vars.str(claim, "incident_date", null);
    if (incidentDateRaw != null) {
      try {
        Instant incidentDate = Instant.parse(incidentDateRaw);
        timingAnomalyDays = Math.max(0, Duration.between(incidentDate, Instant.now()).toDays());
      } catch (DateTimeParseException e) {
        // Unparsable incident date - treat as reported immediately (no anomaly).
      }
    }

    return Map.of(
        "prior_claims_count", priorClaimsCount,
        "timing_anomaly_days", timingAnomalyDays);
  }

  @JobWorker(type = "check-fraud-database")
  public Map<String, Object> checkFraudDatabase() {
    // Meaningful test default: customer is not listed in the fraud database.
    return Map.of("fraud_database_hit", false);
  }

  @JobWorker(type = "llm-fraud-risk-assessment")
  public Map<String, Object> assessFraudRisk(@Variable("fraudIndicators") Map<String, Object> fraudIndicators) {
    // Placeholder for the future AI Agent sub-process (see camunda-ai-agents skill):
    // for now, confirm the deterministic pre-check's preliminary risk band as-is.
    String riskLevel = Vars.str(fraudIndicators, "preliminary_risk_level", "LOW");
    return Map.of("fraudRiskLevel", riskLevel);
  }
}
