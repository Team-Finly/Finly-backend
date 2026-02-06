package com.umc.finly.domain.auth.controller;

import com.umc.finly.domain.auth.dto.res.TermDetailResDTO;
import com.umc.finly.domain.auth.dto.res.TermResDTO;
import com.umc.finly.domain.auth.service.TermQueryService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
@Tag(name = "Terms", description = "약관 조회")
public class TermController {

    private final TermQueryService termQueryService;

    /**
     * 약관 목록 조회
     */
    @Operation(
            summary = "약관 목록 조회",
            description = """
                    서비스 이용에 필요한 약관 목록을 조회합니다.
                    
                    - 회원가입 화면에서 사용됩니다.
                    - 약관은 고정된 순서로 반환됩니다.
                    - 각 약관은 필수/선택 여부를 포함합니다.
                    """
    )
    @GetMapping
    public ApiResponse<TermResDTO.TermList> getTerms(){
        return ApiResponse.onSuccess(termQueryService.getTerms(), SuccessCode.OK);
    }

    @Operation(
            summary = "약관 상세 조회",
            description = """
                약관 ID로 약관 상세(제목/본문)를 조회합니다.
                
                - 로그인 없이 접근 가능하도록 permitAll 권장
                - 약관 종류(termType)도 함께 반환합니다.
                """
    )
    @GetMapping("/{termId}")
    public ApiResponse<TermDetailResDTO> getTermDetail(@PathVariable Long termId){
        return ApiResponse.onSuccess(termQueryService.getTermDetail(termId), SuccessCode.OK);
    }
}
