# 🎉 FUNFUN

> **팀명**: 88하게  
> **서비스명**: FUNFUN  
> **주제**: 여가시간 활동 추천 서비스

---

### 🎬 페이지
![funfun.gif](https://raw.githubusercontent.com/prgrms-web-devcourse-final-project/WEB5_6_88hage_BE/dev/docs/funfunGif/funfun.gif)
- 프론트 배포 주소 : funfunhage.vercel.app
- 백엔드 배포 주소 : funfun.cloud
---

### 👨‍🎓 팀원 소개
| 박병석 | 김윤서 | 손혜은 | 정기문 | 전정원 |
|:------:|:------:|:------:|:------:|:------:|
| <img height="150" style="width: auto;" alt="박병석" src="https://avatars.githubusercontent.com/u/147399765?v=4" /> | <img src="https://avatars.githubusercontent.com/u/145417394?v=4" height="150" style="width: auto;"> | <img src="https://avatars.githubusercontent.com/u/97518677?v=4" height="150" style="width: auto;"> | <img alt="정기문" src="https://avatars.githubusercontent.com/u/131163024?v=4" height="150" style="width: auto;" /> | <img height="150" style="width: auto;" alt="전정원" src="https://avatars.githubusercontent.com/u/120391720?v=4" /> |
| **팀장** | **팀원** | **팀원** | **팀원** | **팀원** |
| [@Parkbyungseok](https://github.com/Parkbyungseok) | [@yunseoy](https://github.com/yunseoy) | [@hesador12](https://github.com/hesador12) | [@Irreplaceable-j](https://github.com/Irreplaceable-j) | [@JeonJW24](https://github.com/JeonJW24) |
---

### 📝 주제 선정 이유
- 여가 시간이 생겼을 때, 의미 있게 활용할 수 있도록 도와주는 AI 추천 서비스의 필요성을 느꼈습니다.
- 단순히 정보를 나열하는 것이 아닌, 사용자의 상황과 취향에 맞춘 **개인화된 여가 활동 추천 서비스**를 제공하고자 합니다.

---

### 🔍 기존 서비스와의 차별점
- 기존 서비스들은 행사나 콘텐츠를 **카드 형식**으로 단순히 사용자에게 나열해 보여줍니다.
- **FUNFUN**은 사용자의 **여가 시간, 위치(거리), 취향**을 기반으로 **맞춤형 여가 활동**을 추천하는 방식입니다.

---

### 🚀 핵심 기능

1.  **콘텐츠 추천**
    - 사용자의 취향, 위치, 여가 시간에 맞춰 영화, 전시, 체험 등 다양한 활동 콘텐츠 추천

2.  **모임 추천**
    - 비슷한 관심사를 가진 사용자들과 연결되는 오프라인/온라인 모임 추천

3.  **모임 기능**
    - 추천받은 모임에 참여하거나 직접 모임을 생성하고 구성원 관리 가능

4.  **채팅 기능**
    - 모임 참여자 간 실시간 소통이 가능한 채팅 기능 제공

---

### 🛠 기술 스택

1. **Redis**
    - 콘텐츠 조회수 저장
    - JWT 토큰 저장 및 캐싱

2. **Python**
    - 데이터 구축 및 가공
    - 추천 로직 및 데이터 전처리

3. **Supabase**
    - PostgreSQL 기반 클라우드 DB
    - 사용자 및 콘텐츠 데이터 저장

4. **Langchain4j**
    - RAG(Retrieval-Augmented Generation) 기반 Retriever 구현
    - 문맥에 맞는 정보 검색 및 연결

5. **LLM (Gemini)**
    - 사용자에게 맞춤형 콘텐츠 및 활동 추천

6. **Spring Boot**
    - 백엔드 서버 개발 환경 구성

7. **JPA**
    - 데이터베이스 연동 및 ORM 처리

8. **Docker**
    - 애플리케이션 컨테이너화
    - 환경 일관성을 위한 이미지 빌드 및 배포

9. **배포**
    - **GCP**, **AWS EC2**를 활용한 서버 배포 및 인프라 구성

---

### 🌐 외부 API 및 연동 서비스

- **LLM (Gemini)**
   - 사용자 맞춤형 콘텐츠 및 여가 활동 추천에 활용되는 대규모 언어 모델

- **카카오맵 API**
   - 사용자의 위치 및 거리 기반 맞춤 추천을 위한 지도 및 위치 정보 제공

- **공공/Open API 데이터 활용**
   - 다양한 공공 데이터 및 오픈 API를 연동하여 풍부한 여가 활동 정보 제공

- **소셜 로그인 연동**
   - 네이버, 구글 소셜 로그인 지원으로 간편한 사용자 인증 및 가입 프로세스 제공

---

### 🧾 시퀀스 다이어그램

### 1. 로그인/정지
![login.png](/docs/sequence/login.png)
![stop.png](/docs/sequence/stop.png)
### 2. 조회수
![view.png.png](/docs/sequence/view.png)
### 3. 데이터 파이프 라인
![yun.png](/docs/sequence/yun.png)
![yuntwo.png](/docs/sequence/yuntwo.png)
### 4. 여가 활동 추천
![recommend.png](/docs/sequence/recommend.png)
### 5. 관리자
![hye.png](/docs/sequence/hye.png)
