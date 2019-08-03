package pl.wojciechkabat.hotchilli.utils.facebookModels;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "aFacebookPhoto")
public class FacebookPhoto {
    private Long id;
    private String source;
}
