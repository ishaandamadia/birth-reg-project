package digit.service;

import digit.enrichment.BirthApplicationEnrichment;
import digit.kafka.Producer;
import digit.repository.BirthRegistrationRepository;
import digit.validators.BirthApplicationValidator;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import org.egov.common.contract.request.RequestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BirthRegistrationServiceTest {

    @Mock
    private BirthApplicationValidator validator;

    @Mock
    private BirthApplicationEnrichment enrichmentUtil;

    @Mock
    private UserService userService;

    @Mock
    private BirthRegistrationRepository birthRegistrationRepository;

    @Mock
    private Producer producer;

    @InjectMocks
    private BirthRegistrationService birthRegistrationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterBtRequest() {
        // Setup
        BirthRegistrationRequest request = new BirthRegistrationRequest();
        when(birthRegistrationRepository.getApplications(any())).thenReturn(Collections.emptyList());

        // Invoke
        List<BirthRegistrationApplication> result = birthRegistrationService.registerBtRequest(request);

        // Verify
        assertEquals(0, result.size());
        verify(validator, times(1)).validateBirthApplication(request);
        verify(enrichmentUtil, times(1)).enrichBirthApplication(request);
        verify(userService, times(1)).callUserService(request);
        verify(producer, times(1)).push(anyString(), eq(request));
    }

    @Test
    public void testSearchBtApplications() {
        // Setup
        RequestInfo requestInfo = new RequestInfo();
        BirthApplicationSearchCriteria searchCriteria = new BirthApplicationSearchCriteria();
        when(birthRegistrationRepository.getApplications(any())).thenReturn(Collections.emptyList());

        // Invoke
        List<BirthRegistrationApplication> result = birthRegistrationService.searchBtApplications(requestInfo, searchCriteria);

        // Verify
        assertEquals(0, result.size());
        verify(birthRegistrationRepository, times(1)).getApplications(searchCriteria);
    }

    @Test
    public void testUpdateBtApplication() {
        // Setup
        BirthRegistrationRequest request = new BirthRegistrationRequest();
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        request.setBirthRegistrationApplications(Collections.singletonList(application));

        // Invoke
        BirthRegistrationApplication result = birthRegistrationService.updateBtApplication(request);

        // Verify
        assertEquals(application, result);
        verify(validator, times(1)).validateApplicationExistence(application);
        verify(enrichmentUtil, times(1)).enrichBirthApplicationUponUpdate(request);
        verify(producer, times(1)).push(anyString(), eq(request));
    }
}
