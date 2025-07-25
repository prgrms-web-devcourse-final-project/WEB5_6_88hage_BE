package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.ContentImageDTO;
import com.grepp.funfun.app.domain.content.dto.ContentUrlDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.entity.ContentImage;
import com.grepp.funfun.app.domain.content.entity.ContentUrl;
import com.grepp.funfun.app.domain.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.vo.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
// 실제 데이터 수집 및 저장 로직 담당
public class DataPipeline {

    private final ContentRepository contentRepository;
    private final KakaoGeoService kakaoGeoService;
    private final ContentCategoryRepository contentCategoryRepository;

    @Value("${kopis.api.key}")
    private String kopisApiKey;

    private static final Map<String, String> KOPIS_URLS = Map.of(
            "performance", "http://www.kopis.or.kr/openApi/restful/pblprfr"
    );

    private static final String DETAIL_URL = "http://www.kopis.or.kr/openApi/restful/pblprfr";
    private static final int MAX_API_CALLS = 1500;

    // 전체 데이터 수집
    public void importFullData() {
        importFromOpenApi(false);
    }

    // 증분 데이터 수집
    public void importIncrementalData() {
        importFromOpenApi(true);
    }

    private void importFromOpenApi(boolean incremental) {
        log.info("API로 컨텐츠 수집 시작 ({})", incremental ? "증분" : "전체");

        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String startDate = today;
            String endDate = LocalDate.now().plusMonths(6).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String afterDate = incremental ? today : null;

            List<String> dataTypes = List.of("performance");
            int totalSaved = 0;

            List<Content> updatedOrInserted = new ArrayList<>();

            for (String dataType : dataTypes) {
                int savedCount = collectDataType(dataType, startDate, endDate, afterDate, updatedOrInserted);
                totalSaved += savedCount;

                if (getCurrentApiCallCount() >= MAX_API_CALLS) {
                    log.warn("API 호출 제한 도달. 수집 중단");
                    break;
                }
            }

            if (incremental) {
                kakaoGeoService.updateContentCoordinates(updatedOrInserted);
            } else {
                kakaoGeoService.updateAllContentCoordinates();
            }

            log.info("자동화 수집 완료: 총 {}개 저장", totalSaved);

        } catch (Exception e) {
            log.error("자동화 수집 중 오류 발생", e);
        }
    }

    private int collectDataType(String dataType, String startDate, String endDate, String afterDate, List<Content> updatedOrInserted) {
        int savedCount = 0;

        List<String> contentIds = getIdList(dataType, startDate, endDate, afterDate);

        // 테스트 50개만
//        int limit = Math.min(50, contentIds.size());
//        List<String> limitedIds = contentIds.subList(0, limit);

        for (String contentId : contentIds) {
            if (getCurrentApiCallCount() >= MAX_API_CALLS) {
                log.warn("API 호출 제한 도달. 수집 중단");
                break;
            }

            Optional<Content> result = processContent(contentId);
            result.ifPresent(updatedOrInserted::add);
            if (result.isPresent()) {
                savedCount++;
            }

            incrementApiCallCount();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("{} 컨텐츠 수집 완료: {}개 저장", dataType, savedCount);
        return savedCount;
    }

    private int apiCallCount = 0;

    private int getCurrentApiCallCount() {
        return apiCallCount;
    }

    private void incrementApiCallCount() {
        apiCallCount++;
    }

    protected Optional<Content> processContent(String contentId) {
        try {
            ContentDTO contentDTO = getDetailInfo(contentId);
            if (contentDTO == null) {
                return Optional.empty();
            }

            Content existingContent = contentRepository.findByExternalId(contentDTO.getExternalId()).orElse(null);

            if (existingContent != null) {
                // 업데이트
                updateContent(existingContent, contentDTO);
                contentRepository.save(existingContent);
                log.debug("기존 콘텐츠 업데이트 완료: {}", existingContent.getContentTitle());
                return Optional.of(existingContent);
            }

            Content content = toEntity(contentDTO);
            if (content == null) {
                log.debug("Content 변환 실패: {}", contentId);
                return Optional.empty();
            }

            addImagesAndUrls(content, contentDTO);
            contentRepository.save(content);

            log.debug("저장 완료: {}", content.getContentTitle());
            return Optional.of(content);

        } catch (Exception e) {
            log.debug("저장 실패: {} - {}", contentId, e.getMessage());
            return Optional.empty();
        }
    }

    private void addImagesAndUrls(Content content, ContentDTO contentDTO) {
        if (contentDTO.getImages() != null) {
            for (ContentImageDTO imageDTO : contentDTO.getImages()) {
                ContentImage image = ContentImage.builder()
                        .content(content)
                        .imageUrl(imageDTO.getImageUrl())
                        .build();
                content.getImages().add(image);
            }
        }

        if (contentDTO.getUrls() != null) {
            for (ContentUrlDTO urlDTO : contentDTO.getUrls()) {
                ContentUrl url = ContentUrl.builder()
                        .content(content)
                        .siteName(urlDTO.getSiteName())
                        .url(urlDTO.getUrl())
                        .build();
                content.getUrls().add(url);
            }
        }
    }

    private void updateContent(Content content, ContentDTO dto) {
        content.setContentTitle(dto.getContentTitle());
        content.setAge(dto.getAge());
        content.setStartDate(dto.getStartDate());
        content.setEndDate(dto.getEndDate());
        content.setFee(dto.getFee());
        content.setArea(dto.getArea());
        content.setTime(dto.getTime());
        content.setRunTime(dto.getRunTime());
        content.setStartTime(dto.getStartTime());
        content.setPoster(dto.getPoster());
        content.setAddress(dto.getAddress());
        content.setGuname(dto.getGuname());
        content.setLatitude(dto.getLatitude());
        content.setLongitude(dto.getLongitude());

        // 기존 이미지 및 URL은 삭제하고 새로 추가
        content.getImages().clear();
        content.getUrls().clear();
        addImagesAndUrls(content, dto);
    }

    protected List<String> getIdList(String dataType, String startDate, String endDate, String afterDate) {
        List<String> allIds = new ArrayList<>();
        String baseUrl = KOPIS_URLS.get(dataType);
        int page = 1;

        while (getCurrentApiCallCount() < MAX_API_CALLS) {
            try {
                String url = String.format(
                        "%s?service=%s&stdate=%s&eddate=%s%s&cpage=%d&rows=100&signgucode=11",
                        baseUrl, kopisApiKey, startDate, endDate,
                        (afterDate != null ? "&afterdate=" + afterDate : ""),
                        page
                );

                RestTemplate restTemplate = createRestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept", "application/xml; charset=UTF-8");
                HttpEntity<?> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, String.class);

                List<String> pageIds = parseIdList(response.getBody());
                if (pageIds.isEmpty()) {
                    break;
                }

                allIds.addAll(pageIds);
                page++;
                Thread.sleep(100);

            } catch (Exception e) {
                log.debug("{} ID 수집 실패 (페이지 {}): {}", dataType, page, e.getMessage());
                break;
            }
        }

        log.debug("{} ID 수집 완료: {}개", dataType, allIds.size());
        return allIds;
    }

    protected ContentDTO getDetailInfo(String contentId) {
        try {
            String url = String.format("%s/%s?service=%s", DETAIL_URL, contentId, kopisApiKey);

            RestTemplate restTemplate = createRestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/xml; charset=UTF-8");
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            return parseDetailResponse(response.getBody());

        } catch (Exception e) {
            log.debug("상세 정보 수집 실패: {} - {}", contentId, e.getMessage());
            return null;
        }
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0,
                new StringHttpMessageConverter(StandardCharsets.UTF_8)
        );

        return restTemplate;
    }


    private Content toEntity(ContentDTO dto) {
        if (dto == null) {
            log.warn("ContentDTO가 null입니다.");
            return null;
        }

        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            log.warn("카테고리가 없습니다: {}", dto.getContentTitle());
            return null;
        }

        ContentClassification classification;
        try {
            classification = ContentClassification.valueOf(dto.getCategory());
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 카테고리(enum 변환 실패): {} - 제목: {}", dto.getCategory(), dto.getContentTitle());
            return null;
        }

        ContentCategory category = contentCategoryRepository
                .findByCategory(classification)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + dto.getCategory()));

        return Content.builder()
                .externalId(dto.getExternalId())
                .contentTitle(dto.getContentTitle())
                .age(dto.getAge())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .fee(dto.getFee())
                .address(dto.getAddress())
                .area(dto.getArea())
                .guname(dto.getGuname())
                .time(dto.getTime())
                .runTime(dto.getRunTime())
                .startTime(dto.getStartTime())
                .poster(dto.getPoster())
                .description(dto.getDescription())
                .category(category)
                .bookmarkCount(dto.getBookmarkCount() != null ? dto.getBookmarkCount() : 0)
                .eventType(EventType.EVENT)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .images(new ArrayList<>())
                .urls(new ArrayList<>())
                .build();
    }

    private List<String> parseIdList(String xmlContent) {
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

    private String getTextContent(Element parent, String id) {
        NodeList nodeList = parent.getElementsByTagName(id);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    private ContentDTO parseDetailResponse(String xmlContent) {
        try {
            Document document = parseXml(xmlContent);

            Element item = (Element) document.getElementsByTagName("db").item(0);
            if (item == null) return null;

            String mt20id = getTextContent(item, "mt20id");
            String prfnm = getTextContent(item, "prfnm");
            String prfpdfrom = getTextContent(item, "prfpdfrom");
            String prfpdto = getTextContent(item, "prfpdto");
            String fcltynm = getTextContent(item, "fcltynm");
            String poster = getTextContent(item, "poster");
            String area = getTextContent(item, "area");
            String genrenm = getTextContent(item, "genrenm");
            String prfage = getTextContent(item, "prfage");
            String prfruntime = getTextContent(item, "prfruntime");
            String pcseguidance = getTextContent(item, "pcseguidance");
            String dtguidance = getTextContent(item, "dtguidance");

            String[] timeInfo = parseTimeGuidance(dtguidance);
            String timeOriginal = timeInfo[0];
            String startTimes = timeInfo[1];

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
                    .externalId(mt20id)
                    .contentTitle(prfnm)
                    .startDate(parseDate(prfpdfrom))
                    .endDate(parseDate(prfpdto))
                    .address(fcltynm)
                    .area(area)
                    .guname(null)
                    .age(prfage)
                    .fee(pcseguidance)
                    .runTime(prfruntime)
                    .time(timeOriginal)
                    .startTime(startTimes)
                    .poster(poster)
                    .category(mapCategoryToEnglish(genrenm))
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

    private String mapCategoryToEnglish(String category) {
        if (category == null || category.trim().isEmpty()) {
            return null;
        }

        String key = category.trim();
        String mapped = CATEGORY_MAP.get(key);
        if (mapped == null) {
            log.debug("매핑되지 않은 카테고리: {}", key);
        }

        return mapped;
    }

    private String[] parseTimeGuidance(String dtguidance) {
        if (dtguidance == null || dtguidance.trim().isEmpty()) {
            return new String[]{null, null};
        }

        try {
            String originalTime = dtguidance.trim();

            Pattern pattern = Pattern.compile("(\\d{1,2}:\\d{1,2})");
            Matcher matcher = pattern.matcher(dtguidance);

            List<String> times = new ArrayList<>();
            while (matcher.find()) {
                String timeStr = matcher.group(1);
                String[] parts = timeStr.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                String normalizedTime = String.format("%02d:%02d", hour, minute);

                if (!times.contains(normalizedTime)) {
                    times.add(normalizedTime);
                }
            }

            String startTimes = times.isEmpty() ? null : String.join(", ", times);
            return new String[]{originalTime, startTimes};

        } catch (Exception e) {
            log.warn("시간 파싱 실패: {}", dtguidance);
            return new String[]{dtguidance, null};
        }
    }


    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            String cleanDate = dateStr.replace(".", "-");
            return LocalDate.parse(cleanDate);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }

    private Document parseXml(String xmlContent) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlContent));
        is.setEncoding("UTF-8");
        return builder.parse(is);
    }
}