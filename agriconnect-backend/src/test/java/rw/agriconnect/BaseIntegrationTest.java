//package rw.agriconnect;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import rw.agriconnect.config.TestConfig;
//
//@SpringBootTest(classes = {AgriConnectBackendApplication.class, TestConfig.class})
//@ActiveProfiles("test")
//public abstract class BaseIntegrationTest {
//
//    @Autowired
//    protected WebApplicationContext webApplicationContext;
//
//    protected MockMvc mockMvc;
//
//    @BeforeEach
//    void setup() {
//        this.mockMvc = MockMvcBuilders
//                .webAppContextSetup(webApplicationContext)
//                .build();
//    }
//}