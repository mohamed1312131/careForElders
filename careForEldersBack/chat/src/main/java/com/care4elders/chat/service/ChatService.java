package com.care4elders.chat.service;


import com.care4elders.chat.DTO.ChatDTO;

import java.util.List;

public interface ChatService {

    ChatDTO createNewChat(String patientId);

    ChatDTO addPatientPrompt(String chatId, String prompt);

    List<ChatDTO> getChatsForPatient(String patientId);

    ChatDTO getChatById(String chatId);
}
