package pl.wojciechkabat.hotchilli.utils.facebookModels;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "aFacebookPicture")
public class FacebookPicture {
    private int height;
    private int width;
    private String url;
}
