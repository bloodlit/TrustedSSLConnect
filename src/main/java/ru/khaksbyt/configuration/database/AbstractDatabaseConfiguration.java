package ru.khaksbyt.configuration.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractDatabaseConfiguration {

    public abstract DataSourceProperties dataSourceProperties();

    public abstract HikariDataSource dataSource();

    public abstract LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, DataSource dataSource);

    public abstract PlatformTransactionManager platformTransactionManager(EntityManagerFactory entityManagerFactory);

    @Data
    protected static class AdditionalJpaProperties {
        private String dialect;
        private Boolean show_sql = false;
        private Boolean format_sql = false;
        private String hbm2ddl = "none";
        private Boolean autocommit = true;

        private String current_session_context_class = "org.springframework.orm.hibernate5.SpringSessionContext";

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("hibernate.dialect", this.dialect);
            map.put("hibernate.show_sql", this.show_sql.toString());
            map.put("hibernate.format_sql", this.format_sql.toString());
            map.put("hibernate.hbm2ddl.auto", this.hbm2ddl);
            map.put("hibernate.connection.autocommit", this.autocommit.toString());
            map.put("hibernate.current_session_context_class", current_session_context_class);
            return map;
        }
    }
}
