package com.banking.banking_system.ServiceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.banking.banking_system.Entity.CardRequest;
import com.banking.banking_system.Repository.CardRequestRepository;
import com.banking.banking_system.Service.CardRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CardRequestServiceTest {

    @Mock
    private CardRequestRepository cardRequestRepository;

    @InjectMocks
    private CardRequestService cardRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestCard_Success() {
        CardRequest request = new CardRequest();
        request.setCardType("CreditCard");
        request.setUserTc("12345678901");

        boolean result = cardRequestService.requestCard(request);

        assertTrue(result);
        verify(cardRequestRepository, times(1)).save(any(CardRequest.class));
    }

    @Test
    void testRequestCard_Failure() {
        doThrow(new RuntimeException()).when(cardRequestRepository).save(any(CardRequest.class));

        CardRequest request = new CardRequest();
        request.setCardType("CreditCard");
        request.setUserTc("12345678901");

        boolean result = cardRequestService.requestCard(request);

        assertFalse(result);
        verify(cardRequestRepository, times(1)).save(any(CardRequest.class));
    }

    @Test
    void testCancelCardRequest_Success() {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setId(1L);

        when(cardRequestRepository.findById(1L)).thenReturn(Optional.of(cardRequest));

        boolean result = cardRequestService.cancelCardRequest(1L);

        assertTrue(result);
        verify(cardRequestRepository, times(1)).findById(1L);
        verify(cardRequestRepository, times(1)).delete(cardRequest);
    }

    @Test
    void testCancelCardRequest_CardRequestNotFound() {
        when(cardRequestRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = cardRequestService.cancelCardRequest(1L);

        assertFalse(result);
        verify(cardRequestRepository, times(1)).findById(1L);
        verify(cardRequestRepository, never()).delete(any(CardRequest.class));
    }

    @Test
    void testCancelCardRequest_Exception() {
        when(cardRequestRepository.findById(1L)).thenThrow(new RuntimeException());

        boolean result = cardRequestService.cancelCardRequest(1L);

        assertFalse(result);
        verify(cardRequestRepository, times(1)).findById(1L);
    }
}

