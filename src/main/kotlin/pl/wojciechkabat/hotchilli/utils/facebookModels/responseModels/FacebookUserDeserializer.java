package pl.wojciechkabat.hotchilli.utils.facebookModels.responseModels;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookUser;

import java.io.IOException;

public class FacebookUserDeserializer extends StdDeserializer<FacebookUser> {

    public FacebookUserDeserializer() {
        this(null);
    }

    public FacebookUserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public FacebookUser deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode productNode = jp.getCodec().readTree(jp);
        return FacebookUser.aFacebookUser()
                .facebookId(productNode.get("id").asText())
                .firstName(productNode.get("first_name").asText())
                .lastName(productNode.get("last_name").asText())
                .email(productNode.get("email") != null ? productNode.get("email").asText() : "")
                .birthday(productNode.get("birthday") != null ? productNode.get("birthday").asText() : null)
                .gender(productNode.get("gender") != null ? productNode.get("gender").asText() : null)
                .build();
    }
}