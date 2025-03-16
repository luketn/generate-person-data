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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public record Person(
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

        int total = 10;
        for (int i = 0; i < total; i++) {
            // Calculate and display progress percentage
            int percentage = (i * 100) / total;
            System.out.print("\rProgress: " + percentage + "% [" + "=".repeat(percentage / 2) + " ".repeat(50 - percentage / 2) + "] (" + i + "/" + total + ")");
            
            String name = faker.name().fullName();
            int age = faker.number().numberBetween(18, 65);
            String profession = faker.company().profession();
            String bio = bioFor(name, age, profession);

            Person person = new Person(
                    name,
                    age,
                    profession,
                    bio
            );
            String result;
            try {
                result = objectMapper.writeValueAsString(person) + "\n";
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            //write the record to the file
            outputStream.write(result.getBytes());
        }

        //close the file
        outputStream.close();
        // Print 100% progress when done
        System.out.print("\rProgress: 100% [" + "=".repeat(50) + "]");
        System.out.println("\nCompleted!");
    }
}