package com.sportygroup.jackpot.config;

import com.sportygroup.jackpot.dto.BetRequest;
import org.springframework.context.ApplicationEvent;

public class BetPublishedEvent extends ApplicationEvent {
    
    private final BetRequest betRequest;
    
    public BetPublishedEvent(Object source, BetRequest betRequest) {
        super(source);
        this.betRequest = betRequest;
    }
    
    public BetRequest getBetRequest() {
        return betRequest;
    }
}
