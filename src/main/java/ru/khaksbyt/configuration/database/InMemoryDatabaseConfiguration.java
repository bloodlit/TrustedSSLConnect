package ru.khaksbyt.configuration.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "inMemoryDbEntityManagerFactory",
        transactionManagerRef = "inMemoryDbTransactionManager",
        basePackages = {
                "ru.khaksbyt.repository.memory"
        }
)
@EnableTransactionManagement
public class InMemoryDatabaseConfiguration extends AbstractDatabaseConfiguration {

    @Bean(value = "inMemoryDataSourceProperties")
    @ConfigurationProperties("application.datasource.in-memory")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(value = "inMemoryDataSource")
    @ConfigurationProperties(prefix = "application.datasource.in-memory.configuration")
    public HikariDataSource dataSource() {
        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(value = "inMemoryDbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("inMemoryDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("ru.khaksbyt.model.memory")
                .persistenceUnit("inMemory-unit")
                .properties(properties().toMap())
                .build();
    }

    @Bean(value = "inMemoryDbTransactionManager")
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier("inMemoryDbEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean("inMemoryProperties")
    @ConfigurationProperties("application.datasource.in-memory.properties")
    public AdditionalJpaProperties properties() {
        return new AdditionalJpaProperties();
    }
}
