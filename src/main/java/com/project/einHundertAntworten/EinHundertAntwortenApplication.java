package com.project.einHundertAntworten;
import com.project.einHundertAntworten.Misc.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication // (exclude = {SecurityAutoConfiguration.class })
public class EinHundertAntwortenApplication {


	public static void main(String[] args) {



		SpringApplication.run(EinHundertAntwortenApplication.class, args);
	}
}


