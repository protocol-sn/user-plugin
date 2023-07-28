package coop.protocolSN.userPlugin.data;

import coop.protocolSN.userPlugin.data.entity.NodeUser;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public class NodeUserRepositoryTest {
    public static final String USER_ID = "facccd44-3b2b-40df-80f0-7bd286bd62df";

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("user")
            .withUsername("postgres")
            .withPassword("password");

    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void testInsert() {
        NodeUser nodeUser = new NodeUser(null, "user", "user@site.com", false);
        NodeUser saved = userRepository.save(nodeUser);

        Assertions.assertNotNull(saved.getUserId());
        Assertions.assertEquals(saved.getUsername(), nodeUser.getUsername());
        Assertions.assertEquals(saved.getEmail(), nodeUser.getEmail());
        Assertions.assertEquals(saved.isVerified(), nodeUser.isVerified());
    }

    @Test
    public void testGet() {
        NodeUser nodeUser = new NodeUser(null, "user", "user@site.com", false);
        NodeUser saved = userRepository.save(nodeUser);

        NodeUser result = userRepository.findById(saved.getUserId()).get();

        Assertions.assertNotNull(result.getUserId());
        Assertions.assertEquals(result.getUsername(), saved.getUsername());
        Assertions.assertEquals(result.getEmail(), saved.getEmail());
        Assertions.assertEquals(result.isVerified(), saved.isVerified());
    }
}
