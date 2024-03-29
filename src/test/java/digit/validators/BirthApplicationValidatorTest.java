package digit.validators;

import digit.repository.BirthRegistrationRepository;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class BirthApplicationValidatorTest {

    @Mock
    private BirthRegistrationRepository repository;

    @InjectMocks
    private BirthApplicationValidator validator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidateBirthApplication_WithValidRequest() {
        // Setup
        BirthRegistrationRequest request = new BirthRegistrationRequest();
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setTenantId("tenantId");
        request.getBirthRegistrationApplications().add(application);

        // Invoke and Verify
        assertDoesNotThrow(() -> validator.validateBirthApplication(request));
    }

    @Test
    public void testValidateBirthApplication_WithMissingTenantId() {
        // Setup
        BirthRegistrationRequest request = new BirthRegistrationRequest();
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        request.getBirthRegistrationApplications().add(application);

        // Invoke and Verify
        CustomException exception = assertThrows(CustomException.class, () -> validator.validateBirthApplication(request));
        assertEquals("EG_BT_APP_ERR", exception.getCode());
        assertEquals("tenantId is mandatory for creating birth registration applications", exception.getMessage());
    }

    @Test
    public void testValidateApplicationExistence_WithExistingApplication() {
        // Setup
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setApplicationNumber("appNumber");
        when(repository.getApplications(any())).thenReturn(Collections.singletonList(application));

        // Invoke
        BirthRegistrationApplication result = validator.validateApplicationExistence(application);

        // Verify
        assertNotNull(result);
        assertEquals("appNumber", result.getApplicationNumber());
    }

    @Test
    public void testValidateApplicationExistence_WithNonExistingApplication() {
        // Setup
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setApplicationNumber("appNumber");
        when(repository.getApplications(any())).thenReturn(Collections.emptyList());

        // Invoke and Verify
        CustomException exception = assertThrows(CustomException.class, () -> validator.validateApplicationExistence(application));
        assertEquals("EG_BT_APP_ERR", exception.getCode());
        assertEquals("Application not found with number: appNumber", exception.getMessage());
    }
}
