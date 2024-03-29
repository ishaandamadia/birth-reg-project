package digit.service;

import digit.config.BTRConfiguration;
import digit.models.coremodels.UserDetailResponse;
import digit.util.UserUtil;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import digit.web.models.User;
import org.egov.common.contract.request.RequestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserUtil userUtils;

    @Mock
    private BTRConfiguration config;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCallUserService() {
        // Setup
        BirthRegistrationRequest request = new BirthRegistrationRequest();
        BirthRegistrationApplication father = new BirthRegistrationApplication();
        father.setId("1");
        father.setFather(new User());
        request.getBirthRegistrationApplications().add(father);

        // Mock searchUser response
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        when(userUtils.userCall(any(), any())).thenReturn(userDetailResponse);

        // Invoke
        userService.callUserService(request);

        // Verify
        verify(userUtils, times(2)).userCall(any(), any());
    }

    @Test
    public void testCreateFatherUser() {
        // Setup
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setFather(new User());

        // Invoke
        User user = userService.createFatherUser(application);

        // Verify
        assertNotNull(user);
    }

    @Test
    public void testCreateMotherUser() {
        // Setup
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setMother(new User());

        // Invoke
        User user = userService.createMotherUser(application);

        // Verify
        assertNotNull(user);
    }

    @Test
    public void testUpsertUser() {
        // Setup
        User user = new User();
        RequestInfo requestInfo = new RequestInfo();

        // Mock userCall response
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        when(userUtils.userCall(any(), any())).thenReturn(userDetailResponse);

        // Invoke
        String accountId = userService.upsertUser(user, requestInfo);

        // Verify
        assertEquals(1, userDetailResponse.getUser().size());
    }

    @Test
    public void testEnrichUser() {
        // Setup
        BirthRegistrationApplication application = new BirthRegistrationApplication();
        application.setFather(new User());
        application.setMother(new User());

        // Mock searchUser response
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        when(userUtils.userCall(any(), any())).thenReturn(userDetailResponse);

        // Invoke
        assertDoesNotThrow(() -> userService.enrichUser(application, new RequestInfo()));
    }

    @Test
    public void testCreateUser() {
        // Setup
        RequestInfo requestInfo = new RequestInfo();
        User user = new User();

        // Mock userCall response
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        when(userUtils.userCall(any(), any())).thenReturn(userDetailResponse);

        // Invoke
        org.egov.common.contract.request.User createdUser = userService.createUser(requestInfo, "tenantId", user);

        // Verify
        assertEquals(1, userDetailResponse.getUser().size());
    }

    @Test
    public void testUpdateUser() {
        // Setup
        User user = new User();
        org.egov.common.contract.request.User userFromSearch = new org.egov.common.contract.request.User();
        RequestInfo requestInfo = new RequestInfo();

        // Mock userCall response
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        when(userUtils.userCall(any(), any())).thenReturn(userDetailResponse);

        // Invoke
        org.egov.common.contract.request.User updatedUser = userService.updateUser(requestInfo, user, userFromSearch);

        // Verify
        assertEquals(1, userDetailResponse.getUser().size());
    }

    @Test
    public void testSearchUser() {
        // Setup
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        when(userUtils.userCall(any(), any())).thenReturn(userDetailResponse);

        // Invoke
        UserDetailResponse result = userService.searchUser("stateLevelTenant", "accountId", "userName");

        // Verify
        assertEquals(userDetailResponse, result);
    }
}
