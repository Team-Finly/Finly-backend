package com.umc.finly.domain.auth.converter;

import com.umc.finly.domain.auth.dto.res.TermDetailResDTO;
import com.umc.finly.domain.auth.dto.res.TermResDTO;
import com.umc.finly.domain.auth.entity.Term;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TermConverter {

    public static TermResDTO.TermItem toTermItem(Term term) {
        return new TermResDTO.TermItem(
                term.getId(),
                term.getTitle(),
                term.getTermType().isRequired(),
                term.getTermType().name()
        );
    }

    public static TermResDTO.TermList toTermList(List<Term> terms) {
        List<TermResDTO.TermItem> items = terms.stream()
                .map(TermConverter::toTermItem)
                .toList();

        return new TermResDTO.TermList(items);
    }

    public static TermDetailResDTO toTermDetailResDTO(Term term) {
        return new TermDetailResDTO(
                term.getId(),
                term.getTermType().name(),
                term.getTitle(),
                term.getContent()
        );
    }
}