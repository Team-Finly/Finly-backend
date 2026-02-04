package com.umc.finly.domain.auth.dto.req;

import com.umc.finly.domain.member.dto.request.PersonaAnswerReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignUpReqDTO {

    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "email 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "password는 필수입니다.")
    private String password;

    @NotBlank(message = "nickname은 필수입니다.")
    private String nickname;

    @NotEmpty(message = "termAgreements는 필수입니다.")
    @Valid
    private List<TermAgreementReq> termAgreements;

    @NotEmpty(message = "personaAnswers는 필수입니다.")
    @Valid
    private List<PersonaAnswerReq> personaAnswers;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TermAgreementReq {
        private Long termId;
        private Boolean agreed;
    }

}
