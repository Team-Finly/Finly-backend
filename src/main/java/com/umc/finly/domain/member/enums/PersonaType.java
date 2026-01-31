package com.umc.finly.domain.member.enums;

public enum PersonaType {

    WORRIED_DEER("걱정 많은 사슴"),
    CAUTIOUS_TURTLE("신중한 거북이"),
    SHARP_EAGLE("날카로운 독수리"),
    FIERY_LION("불타는 사자");

    private final String displayName;

    PersonaType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
