package com.camunda.demo.claimhandling.workers;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Workflow 4/6: Adjuster Assignment (bpmn/adjuster-assignment.bpmn). */
@Component
public class AdjusterAssignmentWorkers {

  @JobWorker(type = "get-adjuster-availability")
  public Map<String, Object> getAdjusterAvailability() {
    // Meaningful test default: a senior adjuster is comfortably available (>= 70 threshold in the DMN table).
    return Map.of("availability_score", 82);
  }

  @JobWorker(type = "get-customer-satisfaction-history")
  public Map<String, Object> getCustomerSatisfactionHistory() {
    // Meaningful test default: strong prior CSAT (>= 80 threshold in the DMN table).
    return Map.of("satisfaction_score", 88);
  }

  @JobWorker(type = "notify-adjuster")
  public Map<String, Object> notifyAdjuster(@Variable("adjusterAssignment") Map<String, Object> adjusterAssignment) {
    String queue = Vars.str(adjusterAssignment, "adjuster_queue", "NATIONAL-GENERAL-QUEUE");
    String adjusterId = "ADJUST-" + queue.toLowerCase(Locale.ROOT).replace('_', '-') + "-01";

    return Map.of(
        "adjusterId", adjusterId,
        "adjusterName", "Jordan Reyes",
        "adjusterNotified", true,
        "adjusterNotifiedAt", Instant.now().toString());
  }
}
