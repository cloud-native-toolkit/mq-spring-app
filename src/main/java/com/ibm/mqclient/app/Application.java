package com.ibm.mqclient.app;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan({ "com.ibm.mqclient", "com.ibm.cloud_garage.*", "com.ibm.hello", "com.ibm.health" })
public class Application extends SpringBootServletInitializer {
	@Autowired
	Environment environment;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${opentracing.jaeger.enabled:false}")
	private boolean tracingEnabled;

	public static void main(String[] args) {
		SpringApplication.run(com.ibm.mqclient.app.Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			String port = environment.getProperty("local.server.port");

			System.out.println();
			System.out.println("Server started - http://localhost:" + port + "/swagger-ui.html");
		};
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Bean
	public io.opentracing.Tracer initTracer() {
		if(tracingEnabled){
			Configuration.SamplerConfiguration samplerConfig = new Configuration.SamplerConfiguration().withType("const")
					.withParam(1);
			Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv()
					.withLogSpans(true);

			return Configuration.fromEnv(this.applicationName).withSampler(samplerConfig).withReporter(reporterConfig)
					.getTracer();
		} else {
			return io.opentracing.noop.NoopTracerFactory.create();
		}
	}
}
