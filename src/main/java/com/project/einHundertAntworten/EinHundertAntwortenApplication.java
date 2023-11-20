package com.project.einHundertAntworten;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication (exclude = {SecurityAutoConfiguration.class })
public class EinHundertAntwortenApplication {


	public static void main(String[] args) {



		SpringApplication.run(EinHundertAntwortenApplication.class, args);
	}
}


