package com.jobready.coffeechats;

import com.jobready.coffeechats.CoffeeChatDtos.CoffeeChatDto;
import com.jobready.coffeechats.CoffeeChatDtos.CreateCoffeeChatRequest;
import com.jobready.coffeechats.CoffeeChatDtos.IcebreakerRequest;
import com.jobready.coffeechats.CoffeeChatDtos.IcebreakerResponse;
import com.jobready.coffeechats.CoffeeChatDtos.UpdateCoffeeChatRequest;
import com.jobready.common.ListResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coffee-chats")
public class CoffeeChatsController {

    private final CoffeeChatService coffeeChatService;

    public CoffeeChatsController(CoffeeChatService coffeeChatService) {
        this.coffeeChatService = coffeeChatService;
    }

    /** POST /coffee-chats/icebreaker -> { message } */
    @PostMapping("/icebreaker")
    public IcebreakerResponse icebreaker(@AuthenticationPrincipal String userId,
            @Valid @RequestBody IcebreakerRequest req) {
        return new IcebreakerResponse(coffeeChatService.icebreaker(userId, req.mentorId()));
    }

    /** POST /coffee-chats -> 201, status pending */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoffeeChatDto request(@AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateCoffeeChatRequest req) {
        return CoffeeChatDto.from(coffeeChatService.request(userId, req));
    }

    /** GET /coffee-chats */
    @GetMapping
    public ListResponse<CoffeeChatDto> mine(@AuthenticationPrincipal String userId) {
        return ListResponse.of(coffeeChatService.mine(userId).stream().map(CoffeeChatDto::from).toList());
    }

    /** PATCH /coffee-chats/{id} */
    @PatchMapping("/{id}")
    public CoffeeChatDto update(@AuthenticationPrincipal String userId, @PathVariable String id,
            @Valid @RequestBody UpdateCoffeeChatRequest req) {
        return CoffeeChatDto.from(coffeeChatService.update(userId, id, req));
    }
}
