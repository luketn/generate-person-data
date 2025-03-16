package com.mycodefu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.mycodefu.FetchBio.bioFor;

public class Main {
    public record Person(
            String id,
            String name,
            int age,
            String job,
            String bio
    ) {
    }

    public static void main(String[] args) throws IOException {
        OutputStream outputStream =
                Files.newOutputStream(Paths.get("records.jsonl"),
                        StandardOpenOption.APPEND
                );

        //write the records to the file
        Faker faker = Faker.instance();

        for (int i = 0; i < 1_000_000; i++) {
            String name = faker.name().fullName();
            int age = faker.number().numberBetween(18, 65);
            String profession = faker.company().profession();
            System.out.println("Requesting bio for: " + name + ", " + age + ", " + profession);
            String bio = bioFor(name, age, profession);
            System.out.println("Bio result: " + bio);

            Person person = new Person(
                    null,
                    name,
                    age,
                    profession,
                    bio
            );
            System.out.println("Inserting person: " + person);
            String result;
            try {
                result = new ObjectMapper().writeValueAsString(person) + "\n";
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            //write the record to the file
            outputStream.write(result.getBytes());
        }

        //close the file
        outputStream.close();
    }
}