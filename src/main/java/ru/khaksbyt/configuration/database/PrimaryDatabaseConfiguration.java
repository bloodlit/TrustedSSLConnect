package ru.khaksbyt.configuration.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "primaryDbEntityManagerFactory",
        transactionManagerRef = "primaryDbTransactionManager",
        basePackages = {
                "ru.khaksbyt.repository.primary"
        }
)
@EnableTransactionManagement
public class PrimaryDatabaseConfiguration extends AbstractDatabaseConfiguration {
    @Primary
    @Bean(value = "primaryDataSourceProperties")
    @ConfigurationProperties("application.datasource.primary")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(value = "primaryDataSource")
    @ConfigurationProperties(prefix = "application.datasource.primary.configuration")
    public HikariDataSource dataSource() {
        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(value = "primaryDbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource)
    {
        return builder
                .dataSource(dataSource)
                .packages("ru.khaksbyt.model.primary")
                .persistenceUnit("primary-unit")
                .properties(properties().toMap())
                .build();
    }

    @Primary
    @Bean(value = "primaryDbTransactionManager")
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier("primaryDbEntityManagerFactory") EntityManagerFactory entityManagerFactory)
    {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean("primaryProperties")
    @ConfigurationProperties("application.datasource.primary.properties")
    public AdditionalJpaProperties properties() {
        return new AdditionalJpaProperties();
    }
}
