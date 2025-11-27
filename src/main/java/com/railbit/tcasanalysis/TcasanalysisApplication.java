package com.railbit.tcasanalysis;

import com.railbit.tcasanalysis.shedule.NotifyUsers;
import org.quartz.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.railbit.tcasanalysis.service.DataInitialization;
import jakarta.annotation.PostConstruct;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class TcasanalysisApplication {

	@Bean
	public JobDetailFactoryBean notifyUsersJob() {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(NotifyUsers.class);
		factoryBean.setDescription("Invoke My Job service...");
		factoryBean.setDurability(true);
		return factoryBean;
	}

	@Bean
	public CronTriggerFactoryBean notifyUsersJobTrigger(JobDetail notifyUsersJob) {
		CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
		factoryBean.setJobDetail(notifyUsersJob);
		// Set the Cron expression to run the job every hour
        factoryBean.setCronExpression("0 0 * * * ?"); // At the start of every hour
		factoryBean.setDescription("Cron Trigger for NotifyUsersJob");
		return factoryBean;
	}

	@Bean
	public ModelMapper getGet() {
		ModelMapper mapper=new ModelMapper();
		mapper.getConfiguration()
				.setMatchingStrategy(MatchingStrategies.STANDARD) // Set the matching strategy
				.setPropertyCondition(Conditions.isNotNull()); // Set the condition to skip null values

		return mapper;
	}

	@Bean
	public FirebaseApp initializeFirebase() {
		try {
			InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("tcas-analysis-firebase-account.json");

			if (serviceAccount == null) {
				throw new IOException("Service account file not found in resources.");
			}

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();

			return FirebaseApp.initializeApp(options);
		} catch (IOException e) {
			throw new RuntimeException("Failed to initialize Firebase", e);
		}
	}

	@PostConstruct
	public void init(){

		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
//		dataInitialization.createDefaultRoles();
//		dataInitialization.createDefaultDesignations();
//		dataInitialization.createDefaultProjectType();
//		dataInitialization.createDefaultRemarkType();
//		dataInitialization.createDefaultIssueCategories();
//		dataInitialization.createDefaultFirms();
//		dataInitialization.createDefaultDivisions();
//		dataInitialization.createDefaultPossibleIssues();
//		dataInitialization.createDefaultRootCauses();
//		dataInitialization.createDefaultRootCauseSubCategories();
//		dataInitialization.createDefaultUser();
//		dataInitialization.createDefaultSheds();
//		dataInitialization.createDefaultLocoTypes();
//		dataInitialization.createDefaultTcasOptions();

		try {
			// Specify the log directory path
			String logDirectoryPath = "./logs";

			// Create the directory if it doesn't exist
			Path logDirectory = Paths.get(logDirectoryPath)
					.toAbsolutePath().normalize();
			Files.createDirectories(logDirectory);

			// Log the successful creation
			System.out.println("Log directory created: " + logDirectory);
		} catch (Exception e) {
			// Handle exceptions, e.g., log an error
			e.printStackTrace();
		}

	}

	@Autowired
	private DataInitialization dataInitialization;

	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

	public static void main(String[] args) {
		SpringApplication.run(TcasanalysisApplication.class, args);
	}

}
