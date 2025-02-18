package com.github.bekk.exampleapp;

import static com.github.bekk.exampleapp.tasks.OneTimeTaskExample.ONE_TIME_TASK;
import static org.assertj.core.api.Assertions.assertThat;

import no.bekk.dbscheduler.ui.controller.TaskAdminController;
import no.bekk.dbscheduler.ui.model.GetTasksResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {ExampleAppWebFlux.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SmokeTest {

  @Autowired TaskAdminController controller;
  @LocalServerPort private Integer serverPort;
  private String baseUrl;
  @Autowired private TestRestTemplate restTemplate;

  @Test
  public void testContextLoads() throws Exception {
    assertThat(controller).isNotNull();
  }

  @Test
  public void testGetTasksReturnsStatusOK() throws Exception {
    ResponseEntity<GetTasksResponse> result =
        this.restTemplate.getForEntity(
            baseUrl
                + "/db-scheduler-api/tasks/all?filter=ALL&pageNumber=0&size=10&sorting=DEFAULT&asc=true&searchTerm="
                + ONE_TIME_TASK.getTaskName(),
            GetTasksResponse.class);
    Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
    assertThat(result.getBody().getItems()).hasSizeGreaterThan(0);
  }

  @Test
  public void testGetTasksReturnsExampleOneTimeTask() throws Exception {
    ResponseEntity<GetTasksResponse> result =
        this.restTemplate.getForEntity(
            baseUrl
                + "/db-scheduler-api/tasks/all?filter=ALL&pageNumber=0&size=10&sorting=DEFAULT&asc=true&searchTerm="
                + ONE_TIME_TASK.getTaskName(),
            GetTasksResponse.class);
    Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
    result.getBody().getItems().forEach(t -> System.out.println(t.getTaskName()));
    assertThat(result.getBody().getItems())
        .anyMatch(taskModel -> taskModel.getTaskName().equals(ONE_TIME_TASK.getTaskName()));
  }

  @BeforeEach
  public void setUp() {
    baseUrl = "http://localhost:" + serverPort;
  }
}
