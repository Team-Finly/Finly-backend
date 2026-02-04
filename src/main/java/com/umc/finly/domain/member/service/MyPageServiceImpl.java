package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.MyPageMeResDTO;
import com.umc.finly.domain.member.dto.response.MyPagePersonaResDTO;
import com.umc.finly.domain.member.dto.response.UpdateNicknameResDTO;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.exception.MemberErrorCode;
import com.umc.finly.domain.member.repository.MemberPersonaResultRepository;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService{
    // MyPage 관련 서비스

    private final MemberPersonaResultRepository memberPersonaResultRepository;
    private final MemberRepository memberRepository;

    // 내 페르소나 조회
    @Override
    public MyPagePersonaResDTO getMyPersona(Long memberId){
        return memberPersonaResultRepository.findByMemberIdFetchPersona(memberId)
                .map(MyPagePersonaResDTO::from)
                .orElseThrow(()-> new CustomException(MemberErrorCode.PERSONA_RESULT_NOT_FOUND));
    }

    // 내 프로필 조회
    @Override
    public MyPageMeResDTO getMyInfo(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MyPageMeResDTO.from(member);
    }

    // 내 닉네임 변경
    @Override
    @Transactional
    public UpdateNicknameResDTO updateMyNickname(Long memberId, String nickname){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 닉네임 변경 없을 시
        if (member.getNickname().equals(nickname)){
            return UpdateNicknameResDTO.of(member.getNickname());
        }

        member.changeNickname(nickname);

        return UpdateNicknameResDTO.of(member.getNickname());
    }
}
