package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;

    // 이메일 중복 확인
    @Override
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }
}