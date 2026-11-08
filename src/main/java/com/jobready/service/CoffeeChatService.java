package com.jobready.service;

import com.jobready.pojo.dto.CoffeeChatDTO;
import com.jobready.pojo.dto.CoffeeChatUpdateDTO;
import com.jobready.pojo.vo.CoffeeChatVO;
import java.util.List;

public interface CoffeeChatService {

    String icebreaker(String mentorId);

    CoffeeChatVO request(CoffeeChatDTO dto);

    List<CoffeeChatVO> listMine();

    CoffeeChatVO update(String id, CoffeeChatUpdateDTO dto);
}
