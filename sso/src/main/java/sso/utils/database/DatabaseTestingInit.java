package sso.utils.database;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;

@Configuration
@Profile("testing")
public class DatabaseTestingInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DataSource dataSource;

    public DatabaseTestingInit(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    void initDatabase() {
        try(Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("database/testing/schema.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("database/testing/data.sql"));
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            LOGGER.error("ERROR initializing database: " + sw.toString());
        }
    }
}
