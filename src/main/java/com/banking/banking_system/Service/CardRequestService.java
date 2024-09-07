package com.banking.banking_system.Service;

import com.banking.banking_system.Entity.CardRequest;
import com.banking.banking_system.Repository.CardRequestRepository;
import com.banking.banking_system.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class CardRequestService {
    private final CardRequestRepository cardRequestRepository;

    @Autowired
    public CardRequestService(CardRequestRepository cardRequestRepository) {
        this.cardRequestRepository = cardRequestRepository;
    }
    public boolean requestCard(CardRequest request) {
        try {
            CardRequest cardRequest = new CardRequest();
            cardRequest.setCardType(request.getCardType());
            cardRequest.setUserTC(request.getUserTC());

            cardRequestRepository.save(cardRequest);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean cancelCardRequest(String userTC) {
        try {
            Optional<CardRequest> optionalCardRequest = cardRequestRepository.findByUserTC(userTC);
            if (optionalCardRequest.isEmpty()) {
                return false;
            }
            CardRequest cardRequest = optionalCardRequest.get();
            cardRequestRepository.delete(cardRequest);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
