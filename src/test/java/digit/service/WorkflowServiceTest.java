package digit.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.BTRConfiguration;
import digit.repository.ServiceRequestRepository;
import digit.web.models.*;
import org.egov.common.contract.request.RequestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WorkflowServiceTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ServiceRequestRepository repository;

    @Mock
    private BTRConfiguration config;

    @InjectMocks
    private WorkflowService workflowService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateWorkflowStatus() {
        // Setup
        BirthRegistrationRequest birthRegistrationRequest = new BirthRegistrationRequest();
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setWorkflow(new Workflow());
        birthRegistrationRequest.getBirthRegistrationApplications().add(application);

        // Mock getProcessInstanceForBTR response
        ProcessInstance processInstance = new ProcessInstance();

        // Mock callWorkFlow method
        when(workflowService.callWorkFlow(any())).thenReturn(new State());

        // Invoke
        assertDoesNotThrow(() -> workflowService.updateWorkflowStatus(birthRegistrationRequest));

        // Verify
       verify(workflowService, times(1)).callWorkFlow(any());
    }

    @Test
    public void testCallWorkFlow() {
        // Setup
        ProcessInstanceRequest request = new ProcessInstanceRequest();
        ProcessInstanceResponse response = new ProcessInstanceResponse();
        response.setProcessInstances(Collections.singletonList(new ProcessInstance()));
        when(repository.fetchResult(any(), any())).thenReturn(response);

        // Invoke
        State state = workflowService.callWorkFlow(request);

        // Verify
        assertNotNull(state);
    }

    @Test
    public void testGetProcessInstanceForBTR() {
        // Setup
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setTenantId("tenantId");
        application.setApplicationNumber("appNumber");
        application.setWorkflow(new Workflow());

        // Mock repository response
        when(repository.fetchResult(any(), any())).thenReturn(new Object());

        // Invoke

        // Verify
    }

    @Test
    public void testGetCurrentWorkflow() {
        // Setup
        RequestInfo requestInfo = new RequestInfo();
        String tenantId = "tenantId";
        String businessId = "businessId";

        // Mock repository response
        when(repository.fetchResult(any(), any())).thenReturn(new Object());

        // Invoke
        ProcessInstance processInstance = workflowService.getCurrentWorkflow(requestInfo, tenantId, businessId);

        // Verify
        assertNotNull(processInstance);
    }

    @Test
    public void testGetProcessInstanceForBirthRegistrationPayment() {
        // Setup
        BirthRegistrationRequest updateRequest = new BirthRegistrationRequest();
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setTenantId("tenantId");
        application.setApplicationNumber("appNumber");
        updateRequest.setRequestInfo(new RequestInfo());
        updateRequest.getBirthRegistrationApplications().add(application);

        // Invoke
        ProcessInstanceRequest processInstanceRequest = workflowService.getProcessInstanceForBirthRegistrationPayment(updateRequest);

        // Verify
        assertNotNull(processInstanceRequest);
        assertEquals(1, processInstanceRequest.getProcessInstances().size());
    }
}
