package com.accantosystems.stratoss.vnfmdriver.web.alm;

import static com.accantosystems.stratoss.vnfmdriver.test.TestConstants.TEST_DL_NO_AUTH;
import static com.accantosystems.stratoss.vnfmdriver.test.TestConstants.TEST_EXCEPTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.accantosystems.stratoss.vnfmdriver.model.alm.ExecutionAcceptedResponse;
import com.accantosystems.stratoss.vnfmdriver.model.alm.ExecutionRequest;
import com.accantosystems.stratoss.vnfmdriver.model.web.ErrorInfo;
import com.accantosystems.stratoss.vnfmdriver.service.LifecycleManagementService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "test" })
public class LifecycleControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private LifecycleManagementService lifecycleManagementService;

    @Test
    public void testExecuteLifecycle() {
        final ExecutionRequest executionRequest = new ExecutionRequest();
        executionRequest.setLifecycleName("Install");
        executionRequest.setDeploymentLocation(TEST_DL_NO_AUTH);

        when(lifecycleManagementService.executeLifecycle(any())).thenReturn(new ExecutionAcceptedResponse(UUID.randomUUID().toString()));

        final ResponseEntity<ExecutionAcceptedResponse> responseEntity = testRestTemplate.postForEntity("/api/lifecycle/execute", executionRequest, ExecutionAcceptedResponse.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(responseEntity.getHeaders().getContentType()).isNotNull();
        assertThat(responseEntity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getRequestId()).isNotEmpty();
    }

    @Test
    public void testExecuteLifecycleReturnsErrorInfo() {
        final ExecutionRequest executionRequest = new ExecutionRequest();
        executionRequest.setLifecycleName("Install");
        executionRequest.setDeploymentLocation(TEST_DL_NO_AUTH);

        when(lifecycleManagementService.executeLifecycle(any())).thenThrow(new RuntimeException(TEST_EXCEPTION_MESSAGE));

        final ResponseEntity<ErrorInfo> responseEntity = testRestTemplate.postForEntity("/api/lifecycle/execute", executionRequest, ErrorInfo.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getHeaders().getContentType()).isNotNull();
        assertThat(responseEntity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getLocalizedMessage()).isEqualTo(TEST_EXCEPTION_MESSAGE);
    }

}