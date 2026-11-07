package com.jobready.service;

import com.jobready.pojo.dto.CoffeeChatDTO;
import com.jobready.pojo.dto.CoffeeChatUpdateDTO;
import com.jobready.pojo.entity.CoffeeChat;
import java.util.List;

public interface CoffeeChatService {

    String icebreaker(String mentorId);

    CoffeeChat request(CoffeeChatDTO dto);

    List<CoffeeChat> listMine();

    CoffeeChat update(String id, CoffeeChatUpdateDTO dto);
}
