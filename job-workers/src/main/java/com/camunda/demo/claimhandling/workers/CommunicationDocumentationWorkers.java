package com.camunda.demo.claimhandling.workers;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Workflow 6/6: Communication & Documentation (bpmn/communication-documentation.bpmn). */
@Component
public class CommunicationDocumentationWorkers {

  @JobWorker(type = "generate-claim-folder")
  public Map<String, Object> generateClaimFolder(@Variable("conversation") Map<String, Object> conversation) {
    String sessionId = Vars.str(conversation, "session_id", "COG-0000-00-00-0000");
    String suffix = sessionId.substring(sessionId.lastIndexOf('-') + 1);
    String claimFolderId = "CL-" + Year.now() + "-" + suffix;
    return Map.of("claimFolderId", claimFolderId);
  }

  @JobWorker(type = "create-compliance-audit-entry")
  public Map<String, Object> createComplianceAuditEntry(@Variable("claimFolderId") String claimFolderId) {
    String auditEntryId = "AUDIT-" + claimFolderId.replace("CL-", "") + "-001";
    return Map.of(
        "auditEntryId", auditEntryId,
        "auditEntryCreatedAt", Instant.now().toString());
  }

  @JobWorker(type = "schedule-adjuster-callback")
  public Map<String, Object> scheduleAdjusterCallback() {
    // Meaningful test default: Sunday-morning callback within 2 hours, per the demo narrative.
    Instant callbackAt = Instant.now().plus(2, ChronoUnit.HOURS);
    return Map.of(
        "callbackScheduledAt", callbackAt.toString(),
        "callbackWindow", "within 2 hours");
  }

  @JobWorker(type = "send-status-sms")
  public Map<String, Object> sendStatusSms(
      @Variable("customer") Map<String, Object> customer, @Variable("claimFolderId") String claimFolderId) {
    String firstName = Vars.str(customer, "name", "there").split(" ")[0];
    String trackingLink = "https://claims.example-insurer.com/track/" + claimFolderId;
    String message = "Hi " + firstName + ", your claim " + claimFolderId
        + " has been filed and assigned. Track status: " + trackingLink;

    return Map.of(
        "smsSent", true,
        "smsMessage", message,
        "trackingLink", trackingLink,
        "smsSentAt", Instant.now().toString());
  }
}
