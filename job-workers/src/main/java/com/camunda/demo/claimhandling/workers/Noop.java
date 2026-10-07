package com.camunda.demo.claimhandling.workers;

import io.camunda.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

@Component
public class Noop {

  @JobWorker(type = "noop")
  public void checkPolicyStatus() {
  }
}
