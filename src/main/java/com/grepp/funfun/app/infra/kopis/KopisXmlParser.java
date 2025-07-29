package com.grepp.funfun.app.infra.kopis;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.ContentImageDTO;
import com.grepp.funfun.app.domain.content.dto.ContentUrlDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class KopisXmlParser {

    private static final Map<String, String> CATEGORY_MAP = Map.ofEntries(
            Map.entry("연극", "THEATER"),
            Map.entry("무용(서양/한국무용)", "DANCE"),
            Map.entry("대중무용", "POP_DANCE"),
            Map.entry("서양음악(클래식)", "CLASSIC"),
            Map.entry("한국음악(국악)", "GUKAK"),
            Map.entry("대중음악", "POP_MUSIC"),
            Map.entry("복합", "MIX"),
            Map.entry("서커스/마술", "MAGIC"),
            Map.entry("뮤지컬", "MUSICAL")
    );

    public List<String> parseIdList(String xmlContent) {
        List<String> ids = new ArrayList<>();
        try {
            Document document = parseXml(xmlContent);
            NodeList items = document.getElementsByTagName("db");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                String mt20id = getTextContent(item, "mt20id");
                if (mt20id != null && !mt20id.trim().isEmpty()) {
                    ids.add(mt20id.trim());
                }
            }
        } catch (Exception e) {
            log.debug("ID 목록 파싱 실패: {}", e.getMessage());
        }
        return ids;
    }

    public ContentDTO parseDetail(String xmlContent) {
        try {
            Document document = parseXml(xmlContent);
            Element item = (Element) document.getElementsByTagName("db").item(0);
            if (item == null) return null;

            String[] timeInfo = parseTimeGuidance(getTextContent(item, "dtguidance"));

            List<ContentImageDTO> images = new ArrayList<>();
            NodeList imageNodes = item.getElementsByTagName("styurl");
            for (int i = 0; i < imageNodes.getLength(); i++) {
                String imageUrl = imageNodes.item(i).getTextContent();
                if (imageUrl != null && !imageUrl.isBlank()) {
                    images.add(ContentImageDTO.builder().imageUrl(imageUrl.trim()).build());
                }
            }

            List<ContentUrlDTO> urls = new ArrayList<>();
            NodeList relateNodes = item.getElementsByTagName("relate");
            for (int i = 0; i < relateNodes.getLength(); i++) {
                Element relate = (Element) relateNodes.item(i);
                String siteName = getTextContent(relate, "relatenm");
                String siteUrl = getTextContent(relate, "relateurl");
                if (siteUrl != null && !siteUrl.isBlank()) {
                    urls.add(ContentUrlDTO.builder()
                            .siteName(siteName != null ? siteName : "알 수 없음")
                            .url(siteUrl.trim())
                            .build());
                }
            }

            return ContentDTO.builder()
                    .externalId(getTextContent(item, "mt20id"))
                    .contentTitle(getTextContent(item, "prfnm"))
                    .startDate(parseDate(getTextContent(item, "prfpdfrom")))
                    .endDate(parseDate(getTextContent(item, "prfpdto")))
                    .address(getTextContent(item, "fcltynm"))
                    .area(getTextContent(item, "area"))
                    .guname(null)
                    .age(getTextContent(item, "prfage"))
                    .fee(getTextContent(item, "pcseguidance"))
                    .runTime(getTextContent(item, "prfruntime"))
                    .time(timeInfo[0])
                    .startTime(timeInfo[1])
                    .poster(getTextContent(item, "poster"))
                    .category(mapCategoryToEnglish(getTextContent(item, "genrenm")))
                    .description(null)
                    .eventType(null)
                    .bookmarkCount(0)
                    .latitude(null)
                    .longitude(null)
                    .images(images)
                    .urls(urls)
                    .build();

        } catch (Exception e) {
            log.debug("상세 응답 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    private Document parseXml(String xmlContent) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlContent));
        is.setEncoding("UTF-8");
        return builder.parse(is);
    }

    private String getTextContent(Element parent, String id) {
        NodeList nodeList = parent.getElementsByTagName(id);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    private String[] parseTimeGuidance(String dtguidance) {
        if (dtguidance == null || dtguidance.trim().isEmpty()) return new String[]{null, null};

        try {
            String originalTime = dtguidance.trim();
            Pattern pattern = Pattern.compile("(\\d{1,2}:\\d{1,2})");
            Matcher matcher = pattern.matcher(dtguidance);
            List<String> times = new ArrayList<>();
            while (matcher.find()) {
                String[] parts = matcher.group(1).split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                String normalizedTime = String.format("%02d:%02d", hour, minute);
                if (!times.contains(normalizedTime)) times.add(normalizedTime);
            }
            return new String[]{originalTime, times.isEmpty() ? null : String.join(", ", times)};
        } catch (Exception e) {
            log.warn("시간 파싱 실패: {}", dtguidance);
            return new String[]{dtguidance, null};
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            String cleanDate = dateStr.replace(".", "-");
            return LocalDate.parse(cleanDate);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }

    private String mapCategoryToEnglish(String category) {
        if (category == null || category.trim().isEmpty()) return null;
        String key = category.trim();
        String mapped = CATEGORY_MAP.get(key);
        if (mapped == null) log.debug("매핑되지 않은 카테고리: {}", key);
        return mapped;
    }
}
