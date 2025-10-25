package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardBlockingRequest;
import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardForBlocking;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-25T16:32:58+0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class CardMapperImpl implements CardMapper {

    @Override
    public CardResponseDto toDto(Card card) {
        if ( card == null ) {
            return null;
        }

        CardResponseDto cardResponseDto = new CardResponseDto();

        cardResponseDto.setUser( map( card.getUser() ) );
        cardResponseDto.setId( card.getId() );
        cardResponseDto.setCardNumber( card.getCardNumber() );
        cardResponseDto.setExpirationDate( card.getExpirationDate() );
        cardResponseDto.setStatus( card.getStatus() );
        cardResponseDto.setBalance( card.getBalance() );

        return cardResponseDto;
    }

    @Override
    public Card toEntity(CardRequestDto cardRequestDto) {
        if ( cardRequestDto == null ) {
            return null;
        }

        Card card = new Card();

        card.setUser( map( cardRequestDto.getUser() ) );
        card.setCardNumber( cardRequestDto.getCardNumber() );
        card.setExpirationDate( cardRequestDto.getExpirationDate() );
        card.setStatus( cardRequestDto.getStatus() );
        card.setBalance( cardRequestDto.getBalance() );

        return card;
    }

    @Override
    public CardForBlocking toBlockingEntity(CardBlockingRequest cardBlockingRequest) {
        if ( cardBlockingRequest == null ) {
            return null;
        }

        CardForBlocking cardForBlocking = new CardForBlocking();

        cardForBlocking.setCardNumber( cardBlockingRequest.getCardNumber() );
        cardForBlocking.setComment( cardBlockingRequest.getComment() );

        return cardForBlocking;
    }
}
