# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

This repo contains the Camunda 8 demo resources showcasing the interaction between Cognigy (the engagement layer) and Camunda (the orchestration layer). The demo is not yet built — this file is the starting context for the codebase as it's created.

## Demo use case

Sarah discovers a burst pipe in her basement after a freeze (water damage, soggy insulation, mold risk). It's Saturday evening and she calls the insurance hotline to file a claim immediately.

1. Cognigy answers the call, authenticates Sarah as a policyholder, and collects damage details.
2. Cognigy forwards the collected data to Camunda as a structured JSON payload via REST webhook (event `claim_initiated`) — see payload shape below.
3. Camunda creates a process instance and then works through six workflows in sequence — policy validation, coverage assessment, fraud detection, adjuster assignment, emergency response, and communication/documentation — while Sarah is still on the call or immediately after.

### Webhook payload shape (Cognigy → Camunda)

```json
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
```

## Camunda orchestration: 6 sequential workflows

Once Camunda receives the claim, it orchestrates these workflows. Camunda handles retries, timeouts, and exception paths automatically.

1. **Policy Validation** (~30s) — verify policy is active, check coverage limits for water damage, confirm no exclusions apply. Valid → proceed; lapsed/excluded → escalate to human review.
2. **Coverage Assessment** (~2min) — apply DMN business rules to determine coverage %, calculate deductible, estimate reserve amount, flag high-value claims.
3. **Fraud Detection** (~1min) — pattern match against prior claims on the property, check timing anomalies, run against fraud database, score risk via LLM-based reasoning. High-risk → route to fraud analyst and pause; low-risk → continue.
4. **Adjuster Assignment** (~1min) — route via business rules using region, adjuster availability, and prior customer satisfaction. Outcome: adjuster assigned and notified.
5. **Emergency Response Coordination** (~2min) — determine if emergency mitigation (water extraction, mold prevention) is needed; if so, contact vendor network, authorize dispatch, capture costs, notify Sarah via SMS.
6. **Communication & Documentation** (ongoing) — generate claim folder, create compliance audit trail entry, schedule adjuster callback, send Sarah a status SMS with claim ID and tracking link.

## Notes for future sessions

- No code, build tooling, or repo structure exists yet — update this file with actual commands (build/lint/test) and architecture once the implementation starts.
- Keep the Cognigy ↔ Camunda integration boundary explicit: Cognigy owns conversation/authentication; Camunda owns orchestration/business rules once the webhook fires.
- `project.md` holds the original informal scoping notes this file was distilled from. If it's edited with new or conflicting details, reconcile them back into this file rather than treating `project.md` as a second source of truth.
