package enterprise_risk_api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9999/keys")
@AutoConfigureMockMvc
class EnterpriseRiskApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rejectsAnUnauthenticatedRiskRequest() throws Exception {
		mockMvc.perform(get("/api/risk-status"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void returnsRiskStatusForAnAuthenticatedJwt() throws Exception {
		mockMvc.perform(get("/api/risk-status").with(jwt().jwt(token -> token.subject("auth0|test-user"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("protected"))
				.andExpect(jsonPath("$.subject").value("auth0|test-user"));
	}

}
