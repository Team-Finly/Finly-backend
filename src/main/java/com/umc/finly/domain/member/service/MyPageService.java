package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.MyPagePersonaRes;

public interface MyPageService {
    MyPagePersonaRes getMyPersona(Long memberId);
}
