package digit.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.service.BirthRegistrationService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("")
public class BirthApiController {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private BirthRegistrationService birthRegistrationService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    public BirthApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @RequestMapping(value = "/birth/registration/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> birthRegistrationV1CreatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the new Birth Registration Application(s) + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody BirthRegistrationRequest body) {
        try {
            List<BirthRegistrationApplication> applications = birthRegistrationService.registerBtRequest(body);
//            ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
            org.egov.common.contract.response.ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
                        BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(applications).responseInfo(responseInfo).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/birth/registration/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> birthRegistrationV1SearchPost(@Parameter(in = ParameterIn.DEFAULT, description = "Parameter to carry Request metadata in the request body", schema = @Schema()) @RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute BirthApplicationSearchCriteria birthApplicationSearchCriteria) {
        try {
            List<BirthRegistrationApplication> applications = birthRegistrationService.searchBtApplications(requestInfoWrapper.getRequestInfo(), birthApplicationSearchCriteria);
            org.egov.common.contract.response.ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);

//            ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
            BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(applications).responseInfo(responseInfo).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/birth/registration/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> birthRegistrationV1UpdatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the new (s) + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody BirthRegistrationRequest body) {
        try {
            BirthRegistrationApplication application = birthRegistrationService.updateBtApplication(body);
            org.egov.common.contract.response.ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);

//            ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);
            BirthRegistrationResponse response = BirthRegistrationResponse.builder().birthRegistrationApplications(Collections.singletonList(application)).responseInfo(responseInfo).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<BirthRegistrationResponse> handleException(Exception e) {
        // Log the error
        Logger logger = LoggerFactory.getLogger(BirthApiController.class);
        logger.error("Error occurred while processing request", e);

        // Create error response
        org.egov.common.contract.response.ResponseInfo errorResponseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(null, false);

//        ResponseInfo errorResponseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(null, false);
        BirthRegistrationResponse errorResponse = BirthRegistrationResponse.builder().responseInfo(errorResponseInfo).build();

        // Determine HTTP status code based on exception type
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e instanceof IllegalArgumentException) {
            // Handle your specific exception
            status = HttpStatus.BAD_REQUEST;
        }

        // Return error response with appropriate status code
        return new ResponseEntity<>(errorResponse, status);
    }
}
