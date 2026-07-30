package za.co.ice.tamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {

    private static final Set<String> HTTP_METHODS =
            Set.of("get", "post", "put", "patch", "delete", "options", "head", "trace");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void everyOperationHasAShortSummary() throws Exception {
        String document = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(document);
        var operations = root.path("paths").properties().stream()
                .flatMap(path -> path.getValue().properties().stream())
                .filter(property -> HTTP_METHODS.contains(property.getKey()))
                .map(java.util.Map.Entry::getValue)
                .toList();

        assertEquals(28, operations.size());
        operations.forEach(operation ->
                assertFalse(operation.path("summary").asString().isBlank()));

        assertEquals(
                "Authenticate a user and return a JWT.",
                root.at("/paths/~1api~1auth~1login/post/summary").asString());
        assertEquals(
                "Return the authenticated user's profile.",
                root.at("/paths/~1api~1users~1me/get/summary").asString());
        assertEquals(
                "Return platform activity metrics.",
                root.at("/paths/~1api~1admin~1metrics/get/summary").asString());
    }
}
