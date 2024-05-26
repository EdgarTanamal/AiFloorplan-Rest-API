package com.aifloorplan.aifloorplanrestapi.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aifloorplan.aifloorplanrestapi.dto.FloorplanResponse;
import com.aifloorplan.aifloorplanrestapi.dto.ChatRequest;
import com.aifloorplan.aifloorplanrestapi.dto.ChatResponse;
import com.aifloorplan.aifloorplanrestapi.dto.ChatgroupResponse;
import com.aifloorplan.aifloorplanrestapi.model.Floorplan;
import com.aifloorplan.aifloorplanrestapi.model.Chat;
import com.aifloorplan.aifloorplanrestapi.model.Chatgroup;
import com.aifloorplan.aifloorplanrestapi.model.User;
import com.aifloorplan.aifloorplanrestapi.other.LimeWireApiService;
import com.aifloorplan.aifloorplanrestapi.repository.FloorplanRepository;
import com.aifloorplan.aifloorplanrestapi.repository.ChatRepository;
import com.aifloorplan.aifloorplanrestapi.repository.ChatgroupRepository;
import com.aifloorplan.aifloorplanrestapi.repository.UserRepository;

@Service
public class ChatService {
  private final LimeWireApiService limeWireApiService = new LimeWireApiService();

  private FloorplanResponse floorplan1 = new FloorplanResponse(0, new byte[] { 0x48, 0x65, 0x6C, 0x6C, 0x6F }, "Hello",
      new Timestamp(System.currentTimeMillis()));
  private FloorplanResponse floorplan2 = new FloorplanResponse(0, new byte[] { 0x57, 0x6F, 0x72, 0x6C, 0x64 }, "World",
      new Timestamp(System.currentTimeMillis()));
  private FloorplanResponse floorplan3 = new FloorplanResponse(0, new byte[] { 0x41, 0x42, 0x43, 0x44, 0x45 }, "ABCDE",
      new Timestamp(System.currentTimeMillis()));

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChatRepository chatRepository;

  @Autowired
  private ChatgroupRepository chatgroupRepository;

  @Autowired
  private FloorplanRepository floorplanRepository;

  public ChatResponse createChat(ChatRequest request) throws Exception {
    // List<FloorplanResponse> floorplans =
    // limeWireApiService.generateFloorplans(request.getChat());

    List<FloorplanResponse> floorplans = getFloorplans();
    Chatgroup chatgroup = new Chatgroup();

    if (request.getUserId() != 0) {
      User user = userRepository.findByIdUserAndIsDeletedFalse(request.getUserId()).orElseThrow();
      if (request.getChatgroupId() != 0) {
        chatgroup = chatgroupRepository.findByIdChatgroupAndIsDeletedFalse(request.getChatgroupId()).orElseThrow();
      } else {
        chatgroup.setUser(user);
        chatgroup = chatgroupRepository.save(chatgroup);
      }

      Chat chat = new Chat();
      chat.setChat(request.getChat());
      chat.setChatgroup(chatgroup);
      chatRepository.save(chat);

      List<FloorplanResponse> savedFloorplans = new ArrayList<>();

      for (FloorplanResponse floorplanResponse : floorplans) {
        Floorplan floorplan = new Floorplan();
        floorplan.setPrompt(floorplanResponse.getPrompt());
        floorplan.setImageData(floorplanResponse.getImageData());
        floorplan.setChat(chat);
        floorplanRepository.save(floorplan);

        FloorplanResponse savedFloorplan = new FloorplanResponse();
        savedFloorplan.setFloorplanId(floorplan.getIdFloorplan());
        savedFloorplan.setPrompt(floorplan.getPrompt());
        savedFloorplan.setImageData(floorplan.getImageData());

        savedFloorplans.add(savedFloorplan);
      }

      floorplans.clear();
      floorplans.addAll(savedFloorplans);
    }

    ChatResponse response = new ChatResponse();
    response.setChat(request.getChat());
    response.setChatgroupId(chatgroup.getIdChatgroup());
    response.setFloorplans(floorplans);

    return response;
  }

  public List<ChatgroupResponse> getHistory(int id) {
    Iterable<Chatgroup> chatgroupList = chatgroupRepository
        .findAllByUserIdUserAndIsDeletedFalseOrderByCreateTimeDesc(id);

    List<ChatgroupResponse> responseList = new ArrayList<>();

    for (Chatgroup chatgroup : chatgroupList) {
      Chat chat = chatRepository.findFirstByChatgroupIdChatgroup(chatgroup.getIdChatgroup())
          .orElseThrow();

      ChatgroupResponse response = new ChatgroupResponse();
      response.setChatgroupId(chatgroup.getIdChatgroup());
      response.setChat(chat.getChat());
      response.setCreateTime(chat.getCreateTime());

      responseList.add(response);
    }

    return responseList;
  }

  public List<ChatResponse> getChatList(int id) {
    Iterable<Chat> chatList = chatRepository.findAllByChatgroupIdChatgroup(id);

    List<ChatResponse> responseList = new ArrayList<>();

    for (Chat chat : chatList) {
      ChatResponse response = new ChatResponse();
      response.setChat(chat.getChat());
      response.setCreateTime(chat.getCreateTime());

      Iterable<Floorplan> floorplans = floorplanRepository.findAllByChatIdChat(chat.getIdChat());
      for (Floorplan floorplan : floorplans) {
        FloorplanResponse floorplanResponse = new FloorplanResponse();
        floorplanResponse.setFloorplanId(floorplan.getIdFloorplan());
        floorplanResponse.setPrompt(floorplan.getPrompt());
        floorplanResponse.setImageData(floorplan.getImageData());
        floorplanResponse.setCreateTime(floorplan.getCreateTime());
        response.getFloorplans().add(floorplanResponse);
      }

      responseList.add(response);
    }

    return responseList;
  }

  public void deleteChatgroupById(int id) {
    Chatgroup chatgroup = chatgroupRepository.findByIdChatgroupAndIsDeletedFalse(id).orElseThrow();
    chatgroup.setDeleted(true);
    chatgroupRepository.save(chatgroup);
  }

  public Optional<Chatgroup> getChatgroupById(int id) {
    return chatgroupRepository.findByIdChatgroupAndIsDeletedFalse(id);
  }

  public boolean chatgroupExistsById(int id) {
    return chatgroupRepository.existsByIdChatgroupAndIsDeletedFalse(id);
  }

  private List<FloorplanResponse> getFloorplans() {
    List<FloorplanResponse> floorplans = new ArrayList<>();
    floorplans.add(floorplan1);
    floorplans.add(floorplan2);
    floorplans.add(floorplan3);
    return floorplans;
  }
}
