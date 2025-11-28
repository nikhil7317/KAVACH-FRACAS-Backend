package com.railbit.tcasanalysis.config;

import javax.sql.DataSource;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.railbit.tcasanalysis.cactiRepo",
        entityManagerFactoryRef = "cactiEntityManager",
        transactionManagerRef = "cactiTransactionManager"
)
public class CactiDBConfig {

    @Bean(name = "cactiDataSource")
    public DataSource cactiDataSource(
            @Value("${cacti.datasource.url}") String url,
            @Value("${cacti.datasource.username}") String username,
            @Value("${cacti.datasource.password}") String password,
            @Value("${cacti.datasource.driver-class-name}") String driver) {

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driver)
                .build();
    }

    @Bean(name = "cactiEntityManager")
    public LocalContainerEntityManagerFactoryBean cactiEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier("cactiDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.railbit.tcasanalysis.entity.cactiEntity")
                .persistenceUnit("cacti")
                .build();
    }

    @Bean(name = "cactiTransactionManager")
    public PlatformTransactionManager cactiTransactionManager(
            @Qualifier("cactiEntityManager") EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}
