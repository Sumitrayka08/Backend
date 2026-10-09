package com.agrinexus.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assistant_messages")
public class AssistantMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private AssistantConversation conversation;

    @Column(nullable = false)
    private String sender; // USER or ASSISTANT

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String messageText;

    @Column(length = 1000)
    private String sourcesUsed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public AssistantMessage() {
    }

    public AssistantMessage(AssistantConversation conversation, String sender, String messageText, String sourcesUsed) {
        this.conversation = conversation;
        this.sender = sender;
        this.messageText = messageText;
        this.sourcesUsed = sourcesUsed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssistantConversation getConversation() {
        return conversation;
    }

    public void setConversation(AssistantConversation conversation) {
        this.conversation = conversation;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSourcesUsed() {
        return sourcesUsed;
    }

    public void setSourcesUsed(String sourcesUsed) {
        this.sourcesUsed = sourcesUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

