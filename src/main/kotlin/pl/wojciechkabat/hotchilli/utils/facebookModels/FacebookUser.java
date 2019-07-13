package pl.wojciechkabat.hotchilli.utils.facebookModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.wojciechkabat.hotchilli.utils.facebookModels.responseModels.FacebookUserDeserializer;

@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Builder(builderMethodName = "aFacebookUser")
@JsonDeserialize(using = FacebookUserDeserializer.class)
public class FacebookUser {
    private String facebookId;
    private String firstName;
    private String lastName;
    private String email;
    private FacebookPicture facebookPicture;
    private String gender;
    private String birthday;

    public String getFacebookId() {
        return facebookId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public FacebookPicture getFacebookPicture() {
        return facebookPicture;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }
}
