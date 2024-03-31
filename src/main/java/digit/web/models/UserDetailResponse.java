package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UserDetailResponse {
    @JsonProperty("responseInfo")
    ResponseInfo responseInfo;

    @JsonProperty("user")
    List<User> user;

//    public UserDetailResponse(ResponseInfo responseInfo, List<User> user) {
//        this.responseInfo = responseInfo;
//        this.user = user;
//    }
//
//    public UserDetailResponse() {
//    }
//
//    public ResponseInfo getResponseInfo() {
//        return this.responseInfo;
//    }
//
//    public List<User> getUser() {
//        return this.user;
//    }
}