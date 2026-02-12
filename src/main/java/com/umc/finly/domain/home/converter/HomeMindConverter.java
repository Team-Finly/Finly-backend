package com.umc.finly.domain.home.converter;

import com.umc.finly.domain.home.dto.response.HomeMindDetailResDTO;
import com.umc.finly.domain.home.dto.response.HomeMindResDTO;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;

public class HomeMindConverter {

    private HomeMindConverter() {
    }

    //Homemind
    public static HomeMindResDTO toHomeMindRes(
            Member member,
            Persona persona,
            int fmiScore,
            String fmiLevel,
            String fmiComment
    ) {
        return HomeMindResDTO.builder()
                .memberName(member.getNickname())
                .persona(
                        persona == null ? null :
                                HomeMindResDTO.Persona.builder()
                                        .personaType(persona.getPersonaType().name())
                                        .personaTitle(persona.getTitle())
                                        .build()
                )
                .fmiScore(fmiScore)
                .fmiLevel(fmiLevel)
                .fmiComment(fmiComment)
                .build();
    }

    // HomeMind Detail

    public static HomeMindDetailResDTO toHomeMindDetailRes(
            Member member,
            Persona persona,
            int fmiScore,
            String fmiLevel,
            String fmiComment,
            HomeMindDetailResDTO.Scores scores
    ) {
        return HomeMindDetailResDTO.builder()
                .memberName(member.getNickname())
                .persona(
                        persona == null ? null :
                                HomeMindDetailResDTO.Persona.builder()
                                        .personaTitle(persona.getTitle())
                                        .description(persona.getDescription())
                                        .build()
                )
                .fmiScore(fmiScore)
                .fmiLevel(fmiLevel)
                .fmiComment(fmiComment)
                .scores(scores)
                .build();
    }

    public static HomeMindDetailResDTO.ScoreDetail toScoreDetail(
            int score,
            String description
    ) {
        return HomeMindDetailResDTO.ScoreDetail.builder()
                .score(score)
                .description(description)
                .build();
    }
}
