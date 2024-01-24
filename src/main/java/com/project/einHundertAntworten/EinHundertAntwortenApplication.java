package com.project.einHundertAntworten;
import com.project.einHundertAntworten.Misc.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication // (exclude = {SecurityAutoConfiguration.class })
public class EinHundertAntwortenApplication extends SpringBootServletInitializer {


	public static void main(String[] args) {



		SpringApplication.run(EinHundertAntwortenApplication.class, args);
	}
}


