package com.banking.banking_system.Controller;

import com.banking.banking_system.Entity.CardRequest;
import com.banking.banking_system.Service.CardRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cardRequest")
@PreAuthorize("hasRole('USER')")
public class CardRequestController {
    private final CardRequestService cardRequestService;

    @Autowired
    public CardRequestController(CardRequestService cardRequestService) {
        this.cardRequestService = cardRequestService;
    }

    @PostMapping("/requestCard")
    public ResponseEntity<String> requestCard(@RequestBody CardRequest request) {
        if (cardRequestService.requestCard(request)) {
            return ResponseEntity.ok("Card request is successful.");
        } else {
            return ResponseEntity.badRequest().body("Card request is unsuccessful.");
        }
    } // http://localhost:8080/api/cardRequest/requestCard

    @PostMapping("/cancelCardRequest")
    public ResponseEntity<String> cancelCardRequest(@RequestParam Long requestID) {
        if (cardRequestService.cancelCardRequest(requestID)) {
            return ResponseEntity.ok("Card request is cancelled.");
        } else {
            return ResponseEntity.badRequest().body("Card request is not cancelled.");
        }
    } // http://localhost:8080/api/cardRequest/cancelCardRequest?userTC=...
}
