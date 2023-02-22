package by.iba.vfdbapi.dao;

import by.iba.vfdbapi.dto.PingStatusDTO;
import by.iba.vfdbapi.dto.dbs.RedshiftConnectionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedShiftConnectorTest {

    private RedShiftConnector connector;

    @BeforeEach
    void setUp() {
        connector = new RedShiftConnector();
    }

    @SneakyThrows
    @Test
    void testPing() {
        String jsonString = "{\n" +
                "        \"host\": \"my.host.rs\",\n" +
                "        \"port\": \"1111\",\n" +
                "        \"user\": \"usr\",\n" +
                "        \"password\": \"pass\",\n" +
                "        \"accessKey\": \"access\",\n" +
                "        \"secretKey\": \"secret\",\n" +
                "        \"ssl\": \"true\"\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        RedshiftConnectionDTO dto = mapper.treeToValue(node, RedshiftConnectionDTO.class);

        Properties properties = new Properties();
        properties.setProperty("user", dto.getUser());
        properties.setProperty("password", dto.getPassword());
        properties.setProperty("ssl", dto.getSsl().toString());
        properties.setProperty("connectTimeout", "2");
        properties.setProperty("AccessKeyID", dto.getAccessKey());
        properties.setProperty("SecretAccessKey", dto.getSecretKey());

        @Cleanup MockedStatic<DriverManager> manager = mockStatic(DriverManager.class);
        manager.when(() -> DriverManager.getConnection("jdbc:redshift:iam://my.host.rs:1111/", properties))
                .thenThrow(SQLException.class);
        PingStatusDTO actual = connector.ping(dto);
        assertFalse(actual.isStatus(), "Ping() should return false, because of SQLException!");
    }
}
