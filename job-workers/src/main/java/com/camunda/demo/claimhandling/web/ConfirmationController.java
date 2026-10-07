package com.camunda.demo.claimhandling.web;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.search.enums.MessageSubscriptionState;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfirmationController {

  private static final String MESSAGE_NAME = "MessageAdjusterConfirmation";

  private final CamundaClient client;

  public ConfirmationController(CamundaClient client) {
    this.client = client;
  }

  @GetMapping("/api/waiting")
  public List<String> waiting() {
    return List.copyOf(waitingCorrelationKeys());
  }

  @PostMapping("/api/confirm")
  public List<String> confirmAll() {
    Set<String> keys = waitingCorrelationKeys();
    for (String key : keys) {
      client
          .newPublishMessageCommand()
          .messageName(MESSAGE_NAME)
          .correlationKey(key)
          .send()
          .join();
    }
    return List.copyOf(keys);
  }

  private Set<String> waitingCorrelationKeys() {
    Set<String> keys = new LinkedHashSet<>();
    client
        .newMessageSubscriptionSearchRequest()
        .filter(
            f ->
                f.messageName(MESSAGE_NAME)
                    .messageSubscriptionState(MessageSubscriptionState.CREATED))
        .page(p -> p.limit(1000))
        .send()
        .join()
        .items()
        .forEach(s -> keys.add(s.getCorrelationKey()));
    return keys;
  }
}
