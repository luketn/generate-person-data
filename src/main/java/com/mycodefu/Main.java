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
    // ANSI escape codes for cursor and line control
    private static final String ANSI_CLEAR_LINE = "\033[2K";
    private static final String ANSI_CURSOR_UP = "\033[1A";
    private static final String ANSI_CURSOR_START = "\r";

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

        int total = 15_000;
        long startTime = System.currentTimeMillis();
        long lastIterationTime = startTime;
        double avgIterationTime = 0;
        
        for (int i = 0; i < total; i++) {
            // Calculate progress percentage
            int percentage = (i * 100) / total;
            
            // Calculate time estimates
            long currentTime = System.currentTimeMillis();
            String progress;
            if (i > 0) {
                long iterationTime = currentTime - lastIterationTime;
                // Exponential moving average with alpha=0.1
                avgIterationTime = (i == 1) ? iterationTime : (0.1 * iterationTime + 0.9 * avgIterationTime);
                
                // Estimate time remaining in seconds
                long remainingIterations = total - i;
                double estimatedSecondsLeft = (avgIterationTime * remainingIterations) / 1000.0;
                
                // Format the time remaining
                String timeLeft = String.format("%02d:%02d:%02d",
                        (int) (estimatedSecondsLeft / 3600),
                        (int) (estimatedSecondsLeft % 3600 / 60),
                        (int) (estimatedSecondsLeft % 60));

                // Display progress bar with time estimate
                progress = "Progress: " + percentage + "% [" + "=".repeat(percentage / 2) +
                           " ".repeat(50 - percentage / 2) + "] ETA: " + timeLeft + " | " + i + "/" + total +
                           " completed (" + (total - i) + " remaining)";
            } else {
                progress = "Progress: " + percentage + "% [" + " ".repeat(50) + "] ETA: calculating... | " + i + "/" + total +
                           " completed (" + (total - i) + " remaining)";
            }
            if (i > 0) {
                // Move cursor up to progress line
                System.out.print(ANSI_CURSOR_UP + ANSI_CLEAR_LINE + ANSI_CURSOR_START);
            }
            System.out.println(progress);

            lastIterationTime = currentTime;

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

        // Calculate total execution time
        long totalTimeMillis = System.currentTimeMillis() - startTime;
        String totalTime = String.format("%02d:%02d:%02d",
                (int) (totalTimeMillis / 3600000),
                (int) (totalTimeMillis % 3600000 / 60000),
                (int) (totalTimeMillis % 60000 / 1000));

        // Calculate average time per iteration
        double avgTimePerIteration = (double) totalTimeMillis / total;
        String avgTimeFormatted = String.format("%.2f", avgTimePerIteration);

        // Move cursor up to progress line
        System.out.print(ANSI_CURSOR_UP + ANSI_CLEAR_LINE + ANSI_CURSOR_START);

        // Print 100% progress when done with total execution time and average time per iteration
        System.out.print("Progress: 100% [" + "=".repeat(50) +
                         "] Completed in: " + totalTime +
                         " | Avg time per record: " + avgTimeFormatted + " ms");
        System.out.println();
    }
}