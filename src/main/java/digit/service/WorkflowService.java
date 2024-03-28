package digit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.BTRConfiguration;
import digit.models.coremodels.*;
import digit.repository.ServiceRequestRepository;
import digit.web.models.*;
import digit.web.models.BusinessService;
import digit.web.models.BusinessServiceResponse;
import digit.web.models.ProcessInstance;
import digit.web.models.ProcessInstanceRequest;
import digit.web.models.ProcessInstanceResponse;
import digit.web.models.RequestInfoWrapper;
import digit.web.models.State;
import digit.web.models.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class WorkflowService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private BTRConfiguration config;

    public void updateWorkflowStatus(BirthRegistrationRequest birthRegistrationRequest) {
        birthRegistrationRequest.getBirthRegistrationApplications().forEach(application -> {
            try {
                ProcessInstance processInstance = getProcessInstanceForBTR(application, birthRegistrationRequest.getRequestInfo());
                ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(birthRegistrationRequest.getRequestInfo(), Collections.singletonList(processInstance));
                callWorkFlow(workflowRequest);
            } catch (Exception e) {
                log.error("Error updating workflow status: {}", e.getMessage());
                throw new CustomException();
            }
        });
    }

    public State callWorkFlow(ProcessInstanceRequest workflowReq) {
        try {
            StringBuilder url = new StringBuilder(config.getWfHost().concat(config.getWfTransitionPath()));
            Object optional = repository.fetchResult(url, workflowReq);
            ProcessInstanceResponse response = mapper.convertValue(optional, ProcessInstanceResponse.class);
            return response.getProcessInstances().get(0).getState();
        } catch (Exception e) {
            log.error("Error calling workflow: {}", e.getMessage());
            throw new CustomException();
        }
    }

    private ProcessInstance getProcessInstanceForBTR(BirthRegistrationApplication application, RequestInfo requestInfo) {
        try {
            Workflow workflow = application.getWorkflow();

            ProcessInstance processInstance = new ProcessInstance();
            processInstance.setBusinessId(application.getApplicationNumber());
            processInstance.setAction(workflow.getAction());
            processInstance.setModuleName("birth-services");
            processInstance.setTenantId(application.getTenantId());
            processInstance.setBusinessService("BTR");
            processInstance.setDocuments(workflow.getDocuments());
            processInstance.setComment(workflow.getComment());

            if (!CollectionUtils.isEmpty(workflow.getAssignees())) {
                List<User> users = new ArrayList<>();

                workflow.getAssignees().forEach(uuid -> {
                    User user = new User();
                    user.setUuid(uuid);
                    users.add(user);
                });

                processInstance.setAssignes(users);
            }

            return processInstance;
        } catch (Exception e) {
            log.error("Error getting process instance for BTR: {}", e.getMessage());
            throw new CustomException();
        }
    }

    public ProcessInstance getCurrentWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {
        try {
            RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
            StringBuilder url = getSearchURLForProcessInstanceWithParams(tenantId, businessId);
            Object res = repository.fetchResult(url, requestInfoWrapper);
            ProcessInstanceResponse response = mapper.convertValue(res, ProcessInstanceResponse.class);
            if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0) != null)
                return response.getProcessInstances().get(0);
            return null;
        } catch (Exception e) {
            log.error("Error getting current workflow: {}", e.getMessage());
            throw new CustomException();
        }
    }

    private BusinessService getBusinessService(BirthRegistrationApplication application, RequestInfo requestInfo) {
        try {
            String tenantId = application.getTenantId();
            StringBuilder url = getSearchURLWithParams(tenantId, "BTR");
            RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
            Object result = repository.fetchResult(url, requestInfoWrapper);
            BusinessServiceResponse response = mapper.convertValue(result, BusinessServiceResponse.class);
            if (CollectionUtils.isEmpty(response.getBusinessServices()))
                throw new CustomException();
            return response.getBusinessServices().get(0);
        } catch (Exception e) {
            log.error("Error getting business service: {}", e.getMessage());
            throw new CustomException();
        }
    }

    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {
        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(config.getWfBusinessServiceSearchPath());
        url.append("?tenantId=").append(tenantId);
        url.append("&businessServices=").append(businessService);
        return url;
    }
    private StringBuilder getSearchURLForProcessInstanceWithParams(String tenantId, String businessService) {
        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(config.getWfProcessInstanceSearchPath());
        url.append("?tenantId=").append(tenantId);
        url.append("&businessIds=").append(businessService);
        return url;
    }

    public ProcessInstanceRequest getProcessInstanceForBirthRegistrationPayment(BirthRegistrationRequest updateRequest) {
        try {
            BirthRegistrationApplication application = updateRequest.getBirthRegistrationApplications().get(0);
            ProcessInstance process = ProcessInstance.builder()
                    .businessService("BTR")
                    .businessId(application.getApplicationNumber())
                    .comment("Payment for birth registration processed")
                    .moduleName("birth-services")
                    .tenantId(application.getTenantId())
                    .action("PAY")
                    .build();
            return ProcessInstanceRequest.builder()
                    .requestInfo(updateRequest.getRequestInfo())
                    .processInstances(Arrays.asList(process))
                    .build();
        } catch (Exception e) {
            log.error("Error getting process instance for birth registration payment: {}", e.getMessage());
            throw new CustomException();
        }
    }
}
