package digit.service;

import digit.config.BTRConfiguration;
import digit.models.coremodels.UserDetailResponse;
import digit.util.UserUtil;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserUtil userUtils;
    private final BTRConfiguration config;

    @Autowired
    public UserService(UserUtil userUtils, BTRConfiguration config) {
        this.userUtils = userUtils;
        this.config = config;
    }

    public void callUserService(BirthRegistrationRequest request) {
        request.getBirthRegistrationApplications().forEach(application -> {
            User user = createFatherUser(application);
            application.getFather().setUuid((upsertUser(user, request.getRequestInfo())));

        });

        request.getBirthRegistrationApplications().forEach(application -> {

            User user = createMotherUser(application);
            application.getMother().setUuid((upsertUser(user, request.getRequestInfo())));

        });
    }

    User createFatherUser(BirthRegistrationApplication application) {
        User father = application.getFather();
        User user = User.builder().userName(father.getUserName())
                .name(father.getName())
                .mobileNumber(father.getMobileNumber())
                .emailId(father.getEmailId())
                .altContactNumber(father.getAltContactNumber())
                .tenantId(father.getTenantId())
                .type(father.getType())
                .roles(father.getRoles())
                .build();
        return user;
    }

    User createMotherUser(BirthRegistrationApplication application) {
        User mother = application.getMother();
        User user = User.builder().userName(mother.getUserName())
                .name(mother.getName())
                .mobileNumber(mother.getMobileNumber())
                .emailId(mother.getEmailId())
                .altContactNumber(mother.getAltContactNumber())
                .tenantId(mother.getTenantId())
                .type(mother.getType())
                .roles(mother.getRoles())
                .build();
        return user;
    }

    String upsertUser(User user, RequestInfo requestInfo) {
        String tenantId = user.getTenantId();
        org.egov.common.contract.request.User userServiceResponse = null;


        UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId), null, user.getMobileNumber());

        //            if (!userDetailResponse.getUser().isEmpty()) {
//                org.egov.common.contract.request.User userFromSearch = userDetailResponse.getUser().get(0);
//                if (!user.getUserName().equalsIgnoreCase(userFromSearch.getUserName())) {
//                    userServiceResponse = updateUser(requestInfo, user, userFromSearch);
//                } else {
//                    userServiceResponse = userFromSearch;
//                }
//            } else {
//                userServiceResponse = createUser(requestInfo, tenantId, user);
//            }

        return userDetailResponse.getUser().get(0).getUuid();
    }


    void enrichUser(BirthRegistrationApplication application, RequestInfo requestInfo) {
        String tenantId = application.getTenantId();

        enrichParentUser(application.getFather(), tenantId);
        enrichParentUser(application.getMother(), tenantId);
    }

    private void enrichParentUser(User parent, String tenantId) {
        String accountId = parent.getId() + "";
        UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId), accountId, null);

        if (CollectionUtils.isEmpty(userDetailResponse.getUser())) {
            throw new CustomException("INVALID_ACCOUNTID", "No user exists for the given accountId: " + accountId);
        }

        org.egov.common.contract.request.User userFromSearch = userDetailResponse.getUser().get(0);
        parent.setId(Integer.valueOf(userFromSearch.getUuid()));
    }

    org.egov.common.contract.request.User createUser(RequestInfo requestInfo, String tenantId, User userInfo) {
        userUtils.addUserDefaultFields(userInfo.getMobileNumber(),tenantId, userInfo);
        StringBuilder uri = new StringBuilder(config.getUserHost())
                .append(config.getUserContextPath())
                .append(config.getUserCreateEndpoint());

        CreateUserRequest user = new CreateUserRequest(requestInfo, userInfo);
        UserDetailResponse userDetailResponse = userUtils.userCall(user, uri);

        if (CollectionUtils.isEmpty(userDetailResponse.getUser())) {
            throw new CustomException("USER_CREATION_FAILED", "Failed to create user.");
        }

        return userDetailResponse.getUser().get(0);
    }

    org.egov.common.contract.request.User updateUser(RequestInfo requestInfo, User user, org.egov.common.contract.request.User userFromSearch) {
        User userBis = new User();
        userBis.setName(user.getName());
        userBis.setActive(true);

        StringBuilder uri = new StringBuilder(config.getUserHost())
                .append(config.getUserContextPath())
                .append(config.getUserUpdateEndpoint());

        UserDetailResponse userDetailResponse = userUtils.userCall(new CreateUserRequest(requestInfo, userBis), uri);

        if (CollectionUtils.isEmpty(userDetailResponse.getUser())) {
            throw new CustomException("USER_UPDATE_FAILED", "Failed to update user.");
        }

        return userDetailResponse.getUser().get(0);
    }

    public UserDetailResponse searchUser(String stateLevelTenant, String accountId, String userName){
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType("CITIZEN");
        userSearchRequest.setTenantId(stateLevelTenant);

        if(StringUtils.isEmpty(accountId) && StringUtils.isEmpty(userName)) {
            return null;
        }

        if(!StringUtils.isEmpty(accountId)) {
            userSearchRequest.setUuid(Collections.singletonList(accountId));
        }

        if(!StringUtils.isEmpty(userName)) {
            userSearchRequest.setUserName(userName);
        }

        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        UserDetailResponse userDetailResponse = userUtils.userCall(userSearchRequest, uri);

        if (CollectionUtils.isEmpty(userDetailResponse.getUser())) {
            throw new CustomException("USER_SEARCH_FAILED", "No user found.");
        }

        return userDetailResponse;
    }
}