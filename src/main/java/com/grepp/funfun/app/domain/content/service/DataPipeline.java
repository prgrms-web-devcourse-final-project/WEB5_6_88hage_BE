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
import com.grepp.funfun.app.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    private final NotificationService notificationService;

    @Value("${kopis.api.key}")
    private String kopisApiKey;

    private static final Map<String, String> KOPIS_URLS = Map.of(
            "performance", "http://www.kopis.or.kr/openApi/restful/pblprfr",
            "festival", "http://www.kopis.or.kr/openApi/restful/prffest",
            "writer", "http://www.kopis.or.kr/openApi/restful/prfper"
    );

    private static final String DETAIL_URL = "http://www.kopis.or.kr/openApi/restful/pblprfr";
    private static final int MAX_API_CALLS = 1500;

    public void importFromOpenApi() {

        log.info("API로 컨텐츠 수집 시작");
        try {
            // 동적 날짜 계산 (현재 ~ 6개월 후)
            String startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDate = LocalDate.now().plusMonths(6).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            List<String> dataTypes = Arrays.asList("performance", "festival", "writer");
            int totalSaved = 0;

            for (String dataType : dataTypes) {
                int savedCount = collectDataType(dataType, startDate, endDate); // 저장된 컨텐츠 수
                totalSaved += savedCount;

                // API 제한 확인
                if (getCurrentApiCallCount() >= MAX_API_CALLS) { // 현재 요청 수 >= 최대 요청 수
                    log.warn("API 호출 제한 도달. 수집 중단");
                    break;
                }
            }

            // 좌표 및 자치구 정보 자동 업데이트
            kakaoGeoService.updateAllContentCoordinates();

            log.info("자동화 수집 완료: 총 {}개 저장", totalSaved);

        } catch (Exception e) {
            log.error("자동화 수집 중 오류 발생", e);
            // 관리자한테 알림 발송 해도 좋을 것 같음
        }
    }

    private int collectDataType(String dataType, String startDate, String endDate) {
        int savedCount = 0;

        // 1. ID 목록 수집
        List<String> contentIds = getIdList(dataType, startDate, endDate);

        // 2. 각 ID에 대해 상세 조회 및 저장 시도
        for (String contentId : contentIds) {
            if (getCurrentApiCallCount() >= MAX_API_CALLS) {
                log.warn("API 호출 제한 도달. 수집 중단");
                break;
            }

            boolean saved = processContent(contentId);
            if (saved) {
                savedCount++;
            }

            // 호출 수 카운트 증가
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

    // 개별 컨텐츠 처리
    private boolean processContent(String contentId) {
        try {
            ContentDTO contentDTO = getDetailInfo(contentId);
            if (contentDTO == null) {
                return false;
            }

            // 이미 존재하는지 체크
            if (contentRepository.existsByContentTitle(contentDTO.getContentTitle())) {
                return false; // 중복 데이터
            }

            // 엔티티 변환 및 저장
            Content content = toEntity(contentDTO);
            addImagesAndUrls(content, contentDTO);
            contentRepository.save(content);

            log.debug("저장 완료: {}", content.getContentTitle());
            return true;

        } catch (Exception e) {
            log.debug("저장 실패: {} - {}", contentId, e.getMessage());
            return false;
        }
    }

    private void addImagesAndUrls(Content content, ContentDTO contentDTO) {
        // 이미지 추가
        if (contentDTO.getImages() != null) {
            for (ContentImageDTO imageDTO : contentDTO.getImages()) {
                ContentImage image = ContentImage.builder()
                        .content(content)
                        .imageUrl(imageDTO.getImageUrl())
                        .build();
                content.getImages().add(image);
            }
        }

        // URL 추가
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

    //목록에서 ID 수집
    private List<String> getIdList(String dataType, String startDate, String endDate) {
        List<String> allIds = new ArrayList<>();
        String baseUrl = KOPIS_URLS.get(dataType);
        int page = 1;

        while (getCurrentApiCallCount() < MAX_API_CALLS) {
            try {
                String url = String.format(
                        "%s?service=%s&stdate=%s&eddate=%s&cpage=%d&rows=100&signgucode=11",
                        baseUrl, kopisApiKey, startDate, endDate, page
                );

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                // 외부 api 응답에서 body 부분만 추출하여 id만 뽑아내서 pageIds 에 저장
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

    // 상세 정보
    private ContentDTO getDetailInfo(String contentId) {
        try {
            String url = String.format("%s/%s?service=%s", DETAIL_URL, contentId, kopisApiKey);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            return parseDetailResponse(response.getBody());

        } catch (Exception e) {
            log.debug("상세 정보 수집 실패: {} - {}", contentId, e.getMessage());
            return null;
        }
    }


    private Content toEntity(ContentDTO dto) {
        ContentClassification classification = ContentClassification.valueOf(dto.getCategory());

        ContentCategory category = contentCategoryRepository
                .findByCategory(classification)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + dto.getCategory()));

        return Content.builder()
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
                .eventType(dto.getEventType())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

    // Id만 꺼내옴
    private List<String> parseIdList(String xmlContent) {
        List<String> ids = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

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


    private List<ContentDTO> fetchAndParse() {
        List<ContentDTO> result = new ArrayList<>();

        for (int page = 1; page <= 10; page++) {
            String url = "http://www.kopis.or.kr/openApi/restful/pblprfr?serviceKey=" + kopisApiKey + "&page=" + page;

            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                String body = response.getBody();

                List<ContentDTO> parsed = parseOpenApiResponse(body);
                result.addAll(parsed);

            } catch (Exception e) {
                log.warn("페이지 {} 수집 실패", page, e);
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return result;
    }

    private ContentDTO parseDetailResponse(String xmlContent) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

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

            // 시간 정보 파싱
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

            // 사이트 링크 추출 (relates > relate)
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

    // 목록 전용
    private List<ContentDTO> parseOpenApiResponse(String raw) {
        List<ContentDTO> result = new ArrayList<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(raw)));

            NodeList items = document.getElementsByTagName("db");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String mt20id = getTextContent(item, "mt20id");
                String prfnm = getTextContent(item, "prfnm");
                String prfpdfrom = getTextContent(item, "prfpdfrom");
                String prfpdto = getTextContent(item, "prfpdto");
                String fcltynm = getTextContent(item, "fcltynm");
                String poster = getTextContent(item, "poster");
                String area = getTextContent(item, "area");
                String genrenm = getTextContent(item, "genrenm");

                ContentDTO dto = ContentDTO.builder()
                        .externalId(mt20id)
                        .contentTitle(prfnm)
                        .startDate(parseDate(prfpdfrom))
                        .endDate(parseDate(prfpdto))
                        .address(fcltynm)
                        .poster(poster)
                        .area(area)
                        .category(mapCategoryToEnglish(genrenm))
                        .eventType(null)
                        .bookmarkCount(0)
                        .images(new ArrayList<>())
                        .urls(new ArrayList<>())
                        .build();

                result.add(dto);
            }

        } catch (Exception e) {
            log.warn("OpenAPI XML 파싱 실패: {}", e.getMessage());
        }

        return result;
    }

    // 카테고리를 영어로 변환
    private String mapCategoryToEnglish(String category) {
        if (category == null || category.trim().isEmpty()) {
            return null;
        }

        // 괄호와 괄호 안의 내용 제거 후 매핑
        String cleanCategory = category.replaceAll("\\([^)]*\\)", "").trim();

        switch (cleanCategory) {
            case "연극":
                return "THEATER";
            case "무용":
                return "DANCE";
            case "대중무용":
                return "POP_DANCE";
            case "서양음악":
                return "CLASSIC";
            case "한국음악":
                return "GUKAK";
            case "대중음악":
                return "POP_MUSIC";
            case "복합":
                return "MIX";
            case "서커스/미술":
            case "서커스":
            case "미술":
                return "MAGIC";
            case "뮤지컬":
                return "MUSICAL";
            default:
                log.debug("매핑되지 않은 카테고리: {}", cleanCategory);
                return cleanCategory.toUpperCase().replace(" ", "_");
        }
    }

    private String[] parseTimeGuidance(String dtguidance) {
        if (dtguidance == null || dtguidance.trim().isEmpty()) {
            return new String[]{null, null};
        }

        try {
            String originalTime = dtguidance.trim();

            // 시간 패턴 찾기 (HH:M 또는 HH:MM 형식)
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

}