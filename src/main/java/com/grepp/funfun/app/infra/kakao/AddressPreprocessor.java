package com.grepp.funfun.app.infra.kakao;

import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddressPreprocessor {

    public boolean isTargetCategory(ContentCategory category) {
        if (category == null || category.getCategory() == null) return false;

        return switch (category.getCategory()) {
            case THEATER, DANCE, POP_DANCE, CLASSIC, GUKAK,
                 POP_MUSIC, MIX, MAGIC, MUSICAL -> true;
            default -> false;
        };
    }

    public String preprocessAddress(String rawAddress) {
        String processed = rawAddress
                .replaceAll("\\([^)]*\\)", "")
                .replaceAll("\\[[^]]*\\]", "")
                .replaceAll("[()\\[\\]]", "")
                .trim();

        return processed.replaceAll("\\s+", " ");
    }

    public String extractGunameFromAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return null;
        }
        String[] parts = fullAddress.split(" ");
        for (String part : parts) {
            if (part.endsWith("구")) {
                return part;
            }
        }
        return null;
    }

    public String[] optimizeKeywordForSearch(String keyword) {
        List<String> variants = new ArrayList<>();

        variants.add(keyword);

        String withoutSeoul = keyword.replaceAll("서울특별시\\s*", "")
                .replaceAll("서울시\\s*", "")
                .replaceAll("서울\\s*", "").trim();
        if (!withoutSeoul.isEmpty()) {
            variants.add(withoutSeoul);
        }

        String cleaned = preprocessAddress(keyword);
        if (!cleaned.isEmpty()) {
            variants.add(cleaned);
        }

        String core = extractCoreKeyword(keyword);
        if (!core.isEmpty() && core.length() >= 2) {
            variants.add(core);
        }

        String[] words = withoutSeoul.split("\\s+");
        if (words.length > 0 && words[0].length() >= 2) {
            variants.add(words[0]);
        }

        return variants.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .distinct()
                .toArray(String[]::new);
    }

    public String extractCoreKeyword(String keyword) {
        String cleaned = keyword.replaceAll("서울특별시\\s*", "")
                .replaceAll("서울시\\s*", "")
                .replaceAll("서울\\s*", "")
                .replaceAll("\\([^)]*\\)", "")
                .trim();

        String[] words = cleaned.split("\\s+");
        if (words.length == 0) return "";

        String core = words[words.length - 1];

        if (core.matches(".*[가-힣].*") && core.length() >= 2) {
            return core;
        } else if (core.matches(".*[a-zA-Z].*") && core.length() >= 3) {
            return core;
        }
        return "";
    }
}
