# Test-Faker

A Java application that generates synthetic person records with AI-generated biographies and saves them to a JSONL file.

## Features

- Creates random person data using JavaFaker
- Generates realistic biographies using a local AI model (Phi-4)
- Stores records in JSONL format

## Requirements

- Java 21
- Maven
- Local AI API server (LM Studio) running on http://localhost:1234

## Setup

1. Clone the repository
2. Build with Maven:
   ```
   mvn clean package
   ```
3. Make sure your local AI API server is running (compatible with OpenAI API format)

## Usage

Run the application to generate 1 million random person records:

```
java -cp target/test-faker-1.0-SNAPSHOT.jar com.mycodefu.Main
```

To test the biography generation separately:

```
java -cp target/test-faker-1.0-SNAPSHOT.jar com.mycodefu.FetchBio
```

## Technical Details

- Uses JavaFaker to generate random names, ages, and professions
- Connects to a local AI model via HTTP requests
- Formats AI prompts to generate personalized biographies
- Outputs data in JSONL format for easy processing

## Dependencies

- JavaFaker 1.0.2
- Jackson Databind 2.17.2