package com.jobready.controller;

import com.jobready.common.result.ListResult;
import com.jobready.pojo.dto.CoffeeChatDTO;
import com.jobready.pojo.dto.CoffeeChatUpdateDTO;
import com.jobready.pojo.dto.IcebreakerDTO;
import com.jobready.pojo.entity.CoffeeChat;
import com.jobready.pojo.vo.IcebreakerVO;
import com.jobready.service.CoffeeChatService;
import jakarta.validation.Valid;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coffee-chats")
@Slf4j
@RequiredArgsConstructor
public class CoffeeChatController {

    private final CoffeeChatService coffeeChatService;

    /** AI drafts a first message to a mentor. */
    @PostMapping("/icebreaker")
    public Result<IcebreakerVO> icebreaker(@Valid @RequestBody IcebreakerDTO dto) {
        return Result.success(new IcebreakerVO(coffeeChatService.icebreaker(dto.getMentorId())));
    }

    @PostMapping
    public Result<CoffeeChat> request(@Valid @RequestBody CoffeeChatDTO dto) {
        log.info("Request coffee chat with mentor {}", dto.getMentorId());
        return Result.success(coffeeChatService.request(dto));
    }

    @GetMapping
    public Result<ListResult<CoffeeChat>> listMine() {
        return Result.success(ListResult.of(coffeeChatService.listMine()));
    }

    /** Mentor accepts or declines; either side marks it completed. */
    @PatchMapping("/{id}")
    public Result<CoffeeChat> update(@PathVariable String id, @Valid @RequestBody CoffeeChatUpdateDTO dto) {
        log.info("Update coffee chat {}: {}", id, dto);
        return Result.success(coffeeChatService.update(id, dto));
    }
}
