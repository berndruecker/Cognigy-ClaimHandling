This repo contains the Camunda 8 Demo resources to showcase the interaction between Cognigy (as the Engagement layer) and Camunda ( as the Orchestration Layer).

The demo use-case:

Sarah discovers a burst pipe in her basement after a freeze—water damage, soggy insulation, potential mold risk. It's Saturday evening. She's stressed and needs to file a claim immediately to protect her property. She calls the hotline and explains what happened. The call is answered by Cognigy. Cognigy will authenticate Sarah as an insurance holder, collect details regarding the damage and will forward all relevant data to Camunda.

The Magic: Cognigy sends a structured JSON payload to Camunda via REST webhook.

{
  "event": "claim_initiated",
  "timestamp": "2026-09-08T20:06:00Z",
  "customer": {
    "id": "CUST-12345",
    "name": "Sarah Johnson",
    "policy_number": "AZ-HOME-7841029",
    "phone": "+1-555-0123",
    "email": "sarah.johnson@email.com"
  },
  "claim": {
    "type": "property_damage",
    "coverage_type": "water_damage",
    "incident_date": "2026-09-08T19:30:00Z",
    "description": "Burst pipe in basement; water damage to insulation and drywall",
    "estimated_loss": 15000,
    "hazards_identified": ["mold_risk", "structural_damage_potential"],
    "safety_concern": false,
    "policy_tier": "premium"
  },
  "conversation": {
    "session_id": "COG-2026-09-08-4521",
    "duration_seconds": 270,
    "sentiment": "anxious_but_cooperative",
    "agent_id": "cognigy_ai_agent_01"
  }
}

Camunda receives this and:

✅ Creates a unique process instance (claim workflow)
✅ Validates the claim against policy rules
✅ Routes to the right adjuster queue (based on claim type, region, coverage)
✅ Initiates parallel workflows (fraud check, coverage verification, emergency response)



The moment Camunda receives the claim, it orchestrates 6 parallel workflows while Sarah is still on the call (or immediately after).

Workflow 1: Policy Validation (30 seconds)

Verify policy is active and in force
Check coverage limits for water damage
Confirm no exclusions apply
Decision: If policy is valid → proceed; if lapsed or excluded → escalate to human review

Workflow 2: Coverage Assessment (2 minutes)

Apply business rules (DMN) to determine coverage %
Calculate deductible applicability
Estimate reserve amount (payment obligation)
Flag high-value claims for special handling

Workflow 3: Fraud Detection (1 minute)

Pattern matching: Previous claims on same property?
Timing anomaly: Claim filed night of incident (legitimate) vs. 3 months later (suspicious)?
Automatic check against fraud database
AI agent (Camunda's LLM-based reasoning) scores risk
If high-risk: Route to fraud analyst; pause processing
If low-risk: Continue

Workflow 4: Adjuster Assignment (1 minute)

Business rules route to appropriate adjuster
Property damage specialist in her region?
Availability score (not overwhelmed)?
Previous customer satisfaction with this adjuster?
Outcome: Adjuster "ADJUST-sarah-jones" assigned; notification queued

Workflow 5: Emergency Response Coordination (2 minutes)

Claim type = water damage → Trigger emergency services?
Check if emergency mitigation is needed (water extraction, mold prevention)
If yes: Contact preferred vendor network, authorize emergency dispatch, capture mitigation costs
Outcome: If needed, emergency crew dispatched; Sarah notified in real-time via SMS

Workflow 6: Communication & Documentation (ongoing)

Generate claim folder in document management system
Create audit trail entry for compliance (regulatory requirement)
Schedule adjuster call: "Adjuster will call you within 2 hours" (Sunday morning, not Monday)
Send Sarah a detailed SMS: "Your claim CL-2026-SAT-4521 has been filed and assigned to adjuster Jones. She'll call you at [time]. View your claim status: [link]"

All 6 workflows run sequentially. Camunda handles retries, timeouts, and exception paths automatically.

