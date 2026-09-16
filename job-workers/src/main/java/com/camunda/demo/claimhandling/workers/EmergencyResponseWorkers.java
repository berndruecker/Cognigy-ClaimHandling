package com.camunda.demo.claimhandling.workers;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

/** Workflow 5/6: Emergency Response Coordination (bpmn/emergency-response-coordination.bpmn). */
@Component
public class EmergencyResponseWorkers {

  // Keyed by emergencyMitigation.vendor_type, produced by the emergencyMitigation DMN decision.
  private static final Map<String, String> VENDOR_NAMES = Map.of(
      "water_extraction", "Rapid Dry Restoration Co.",
      "mold_remediation", "CleanAir Mold Remediation",
      "structural_repair", "SafeHome Structural Repair");

  private static final Map<String, Double> VENDOR_COSTS = Map.of(
      "water_extraction", 1200.00,
      "mold_remediation", 2200.00,
      "structural_repair", 4500.00);

  private static final String DEFAULT_VENDOR_TYPE = "water_extraction";

  @JobWorker(type = "contact-vendor-network")
  public Map<String, Object> contactVendorNetwork(
      @Variable("emergencyMitigation") Map<String, Object> emergencyMitigation) {
    String vendorType = Vars.str(emergencyMitigation, "vendor_type", DEFAULT_VENDOR_TYPE);
    String vendorName = VENDOR_NAMES.getOrDefault(vendorType, "General Contractor Network");
    String vendorContactId = "VENDOR-" + vendorType.toUpperCase(Locale.ROOT).replace('_', '-') + "-01";

    return Map.of(
        "vendorName", vendorName,
        "vendorContactId", vendorContactId);
  }

  @JobWorker(type = "authorize-emergency-dispatch")
  public Map<String, Object> authorizeEmergencyDispatch() {
    return Map.of(
        "vendorDispatched", true,
        "dispatchEtaMinutes", 45);
  }

  @JobWorker(type = "capture-mitigation-cost")
  public Map<String, Object> captureMitigationCost(
      @Variable("emergencyMitigation") Map<String, Object> emergencyMitigation) {
    String vendorType = Vars.str(emergencyMitigation, "vendor_type", DEFAULT_VENDOR_TYPE);
    double cost = VENDOR_COSTS.getOrDefault(vendorType, 800.00);
    return Map.of("mitigationCost", cost);
  }

  @JobWorker(type = "notify-customer-sms")
  public Map<String, Object> notifyCustomerSms(
      @Variable("customer") Map<String, Object> customer, @Variable("vendorName") String vendorName) {
    String firstName = Vars.str(customer, "name", "there").split(" ")[0];
    String message = "Hi " + firstName + ", " + vendorName
        + " has been dispatched to your property for emergency mitigation. We'll keep you updated.";

    return Map.of(
        "smsSent", true,
        "smsMessage", message,
        "smsSentAt", Instant.now().toString());
  }
}
