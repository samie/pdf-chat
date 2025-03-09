# AI PDF Chat - Example Application for London Java User Group
<img width="759" alt="app-screenshot" src="https://github.com/user-attachments/assets/0d3ead7e-8019-4c60-ba6f-9b750bc203bf" />

## Overview
This repository contains an example Java-only application that enables AI-powered chat interactions with PDF documents.
Upload and process PDF documents. Ask questions about the document. 

## Tech Stack
- [Apache Tika](https://tika.apache.org/) to parse the PDF
- [Spring AI](https://spring.io/projects/spring-ai) for call Local LLM or Open AI API
- [Vaadin](https://vaadin.com/flow) 24.6 Web UI in Java

Project was created from [start.spring.io](https://start.spring.io/)

##  Also starring
- **Java 21**
- **Maven** 

### Build and Run
Clone the repository:
```sh
git clone https://github.com/samie/pdf-chat.git
cd pdf-chat
```
Build the project:
```sh
mvn clean package
```
Run the application:
```sh
java -jar target/ai-pdf-chat.jar
```

### API Usage
Once running, access the application at:
```
http://localhost:8080
```
## License
This project is licensed under the Apache 2.0 License.

## Acknowledgments
Special thanks to [London Java User Group](https://www.londonjavacommunity.co.uk/) for the inspiration behind this project.

