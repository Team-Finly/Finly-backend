package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.MyPagePersonaRes;
import com.umc.finly.domain.member.exception.MemberErrorCode;
import com.umc.finly.domain.member.repository.MemberPersonaResultRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService{
    // MyPage 관련 서비스

    private final MemberPersonaResultRepository memberPersonaResultRepository;

    // 내 페르소나 조회
    @Override
    public MyPagePersonaRes getMyPersona(Long memberId){
        return memberPersonaResultRepository.findByMemberIdFetchPersona(memberId)
                .map(MyPagePersonaRes::from)
                .orElseThrow(()-> new CustomException(MemberErrorCode.PERSONA_RESULT_NOT_FOUND));
    }
}
