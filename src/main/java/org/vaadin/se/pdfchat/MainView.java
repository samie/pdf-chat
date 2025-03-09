package org.vaadin.se.pdfchat;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.vaadin.firitin.components.messagelist.MarkdownMessage;

@Route
public class MainView extends VerticalLayout {

    private String chatId;
    private String pdfText;
    private ChatMemory chatMemory = new InMemoryChatMemory();

    public MainView(ChatClient.Builder chatClientBuilder, PDFService service) {
        add(new H1("Ask me anything about the document"));

        ChatClient chatClient = chatClientBuilder.build();
        setSizeFull();

        var messageList = new VerticalLayout();
        var messageInput = new MessageInput();
        messageInput.setWidthFull();

        Upload upload = new Upload();
        upload.setAcceptedFileTypes(PDFService.CONTENT_TYPE);
        MemoryBuffer buffer = new MemoryBuffer();
        upload.setReceiver(buffer);

        // Parse document and start new chat when upload finished
        upload.addFinishedListener(e -> {
            this.pdfText = service.parseToText(buffer.getInputStream());
            chatId = e.getFileName().toLowerCase();
            messageList.removeAll();
            chatMemory = new InMemoryChatMemory();

            String documentSummary = chatClient.prompt("Summarize the following document in one sentence."+
                            "\n---- Start of the document ---- \n" +
                                    this.pdfText+
                            "\n---- End of the document ---- \n")
                    .call().content();

            SystemMessage systemMessage = new SystemMessage("Answer the user's question about the PDF document:" +
                    "\n---- Start of the document ---- \n" +
                    this.pdfText +
                    "\n---- End of the document ---- \n" +
                    "\nBe brief when answering and don't use any information outside the document itself.");
            chatMemory.add(chatId,systemMessage);

            messageList.add(new MarkdownMessage("You can now ask anything about '"+e.getFileName()+"'. "+documentSummary, "Assistant", MarkdownMessage.Color.AVATAR_PRESETS[1]));
            messageInput.focus();
        });
        add(upload);

        // Answer new chat questions
        messageInput.addSubmitListener(event -> {
            if (this.pdfText == null) {
                messageList.add(new MarkdownMessage("Please upload a valid document first.", "Assistant", MarkdownMessage.Color.AVATAR_PRESETS[1]));
                return;
            }

            var question = event.getValue();
            var userMessage = new MarkdownMessage(question, "You", MarkdownMessage.Color.AVATAR_PRESETS[0]);
            var assistantMessage = new MarkdownMessage("Assistant", MarkdownMessage.Color.AVATAR_PRESETS[1]);
            messageList.add(userMessage, assistantMessage);

            // Call LLM
            chatMemory.add(chatId,new UserMessage(question));
            chatClient.prompt()
                    .messages(chatMemory.get(chatId,10))
                    .stream().content()
                    .subscribe(assistantMessage::appendMarkdownAsync);
        });

        addAndExpand(new Scroller(messageList));
        add(messageInput);
    }
}
