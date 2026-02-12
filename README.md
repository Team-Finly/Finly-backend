# Finly

> **감정과 투자 데이터를 연결해 사용자가 자신의 금융 성향을 이해하도록 돕는 서비스**

<img width="700" alt="00_표지" src="https://github.com/user-attachments/assets/4f3445ad-d7b4-4dfc-9ba9-8211f05d824e" />


## 🚀 기술 스택
- **Language**: Java 21
- **Framework**: SpringBoot 4.0.1
- **Build Tool**: Gradle
- **Database**: MySQL, JPA, Redis
- **Infra**: AWS, Github Actions, Docker
- **Auth**: JWT, Spring Security
- **외부 API**: OpenAI API, 한국투자증권 API

## 📂 프로젝트 구조  
```
Finly/
├── .gitignore                      # Git에 올리지 않을 파일/디렉토리 설정
├── .github/                        # GitHub 관련 설정 디렉토리
│   ├── ISSUE_TEMPLATE/             # 이슈 템플릿
│   ├── workflows/                  # GitHub Actions 워크플로우
│   └── pull_request_template.md    # PR 템플릿
├── Dockerfile                      # 애플리케이션 Docker 이미지 빌드 설정
├── docker-compose.yml              # Docker 컨테이너 구성
├── build.gradle                    # Gradle 빌드 스크립트
├── settings.gradle                 # Gradle 프로젝트 설정
└── src/
    └── main/
        ├── java/
        │   └── com/umc/finly
        │       ├── FinlyApplication.java      # Spring Boot 애플리케이션 진입점
        │       ├── domain/                    # 비즈니스 도메인 계층
        │       │   ├── auth/                  # 인증/인가, 회원가입/로그인, JWT 등
        │       │   │   ├── controller/        
        │       │   │   ├── converter/         
        │       │   │   ├── dto/               
        │       │   │   ├── entity/           
        │       │   │   ├── enums/
        │       │   │   ├── exception/                
        │       │   │   ├── repository/        
        │       │   │   └── service/           
        │       │   ├── member/                # 회원 도메인 
        │       │   ├── home/                  # 홈/대시보드 도메인
        │       │   ├── record/                # 기록 도메인
        │       │   ├── market/                # 주식 데이터(종목, 지수) 도메인
        │       │   └── analysis/              # 통계 도메인
        │       └── global/                    # 전역 공통 모듈
        │           ├── config/                # 설정 파일
        │           ├── entity/                # 공통 BaseEntity
        │           ├── infra/                 # 외부 인프라 연동
        │           ├── apiPayload/            # 공통 응답/예외 포맷
        │           │   ├── exception/         # 전역 예외 정의 및 핸들러
        │           │   └── response/          # 공통 API 응답 래퍼, 상태 코드
        │           └── util/                  # 공통 유틸리티 클래스
        └── resources/
            ├── application.yml                # Spring Boot 환경 설정
            ├── application-dev.yml            # 개발 환경 설정
            └── application-prod.yml           # 운영 환경 설정                          

```

## 📌 Git convention
<details>
<summary><b>작업 규칙</b></summary>
    
    - 직접 main 브랜치에서 작업 금지, 모든 개발은 develop 브랜치를 기준으로 진행
    - 기능 단위로 브랜치를 생성하여 작업 후 PR을 통해 병합, 브랜치 이름은 기능/작업 내용이 명확하게 드러나도록 작성
    - 모든 작업 시작 전 작업 브랜치에서 최신 develop 브랜치 pull하여 최신 상태 반영 후 작업 시작
    - PR 생성 전 원격 develop 브랜치에 변경 사항이 있는 경우, 작업 브랜치에 develop 브랜치 merge 후 PR 생성
    - PR은 2인 이상 코드 리뷰 및 승인 후 병합
    
</details>


<details>
<summary><b>브랜치 전략</b></summary>
    
**Github Flow**

```
1. 작업 단위로 Issue 생성
2. develop 브랜치에서 분기한 작업용 브랜치 생성
3. 생성한 브랜치에서 작업 진행
4. 작업 완료 후 develop 브랜치로 Pull Request(PR) 생성
5. 코드 리뷰 및 승인 후 PR merge
6. 배포 가능한 상태 확인 후, develop 브랜치에서 main 브랜치로 merge
7. main 브랜치에 merge 시 CI/CD 파이프라인을 통해 자동 빌드, 테스트, 배포 진행
```

- **main** : 프로젝트가 최종적으로 배포되는 브랜치
    - Production 환경에 언제 배포해도 문제없는 안정(stable) 브랜치입니다.
    - 장애나 긴급 버그 발생 시 main 브랜치에서 핫픽스를 진행합니다.
    - Initial commit을 제외하고 main 브랜치에 직접 커밋하지 않으며, 반드시 Pull Request로만 병합합니다.
- **develop** : 통합 개발 브랜치
    - 새로운 기능 개발 시 main을 기준으로 develop 브랜치를 생성합니다.
    - feature 브랜치들을 병합하는 곳입니다.
    - 모든 기능이 통합되고 버그가 수정된 후 main 브랜치로 PR을 생성합니다.
    - main 브랜치는 항상 안정적이어야 하며, 불완전한 작업은 develop에서 처리합니다.
- **feat/*** : 기능 개발 브랜치
    - 이슈 기반으로 브랜치를 생성하며, 브랜치명은 반드시 `feat/{도메인}-{이슈번호}-{기능명}` 형식을 따릅니다. 예: `feat/auth-3-login`
    - develop 브랜치를 기준으로 분기하여 새로운 기능을 개발합니다.
    - 기능에 대한 버그 수정은 해당 feature 브랜치 내에서 완료 후 develop으로 PR을 생성합니다.

</details>


<details>
<summary><b>Issue/PR</b></summary>
    
**Issue**
```
[Tag] Title
```
- 이슈 내용에는 다음을 포함:
  - 이슈 요약
  - 상세 내용
  - 체크리스트
  - 참고 사항
- Labels, Assignees 지정
  
**Pull Request (PR)**
```
[Tag] Title
```
- PR 내용에는 다음을 포함:
  - 관련 이슈
  - 작업 내용
  - 테스트 결과
  - 스크린샷
  - 참고 사항
- Labels, Assignees, Reviewers 지정


#### Issue/PR Tag 종류
| Tag      | 설명                         |
|----------|-----------------------------|
| **Feat**     | 새로운 기능 추가             |
| **Fix**      | 버그 수정                    |
| **Docs**     | 문서 수정                    |
| **Style**    | 코드 포맷/스타일 변경        |
| **Refactor** | 코드 리팩토링                |
| **Test**     | 테스트 코드 추가/수정        |
| **Chore**    | 빌드/설정/패키지 관련 작업   |
| **Merge**    | 브랜치 병합                  |

  
</details>


<details>
<summary><b>Commit message</b></summary>

 ```
Type: Title

<body>               

<footer>            
 ```

- 제목 (Subject)
    - 커밋 유형과 간단한 제목 작성
    - 끝부분에 이슈 번호 포함
- 본문 (Body) (선택)
    - 72자 내로 줄바꿈하여 작성
    - 무엇을, 왜 변경했는지 상세히 설명
- 꼬리말 (Footer) (선택)
    - 이슈 트래커와 함께 사용할 때 해결한 이슈나 참고할 부분을 명시
        - ```Resolves: #이슈번호``` → 커밋이 해당 이슈를 해결할 때
        - ```Refs: #이슈번호``` → 커밋이 관련된 참고 이슈일 때
        - ```See also: #이슈번호``` → 관련 이슈를 참조할 때

예:
```
Feat: 감정 기록 상세 조회 API 추가 (#12)

사용자가 일별 및 월별 감정 기록을 조회할 수 있는 API를
구현했습니다. 프론트에서 그래프와 통계 정보를
바로 활용할 수 있도록 설계했습니다.

Resolves: #12
```

**Commit message Type 종류**

| Type | 의미 | 사용 예시 |
|------|------|-----------|
| **Feat** | 새로운 기능 추가 | `Feat: 감정 기록 상세 조회 API 추가` |
| **Fix** | 버그 수정 | `Fix: 금융마음지수 계산 오류 수정` |
| **Docs** | 문서 수정 | `Docs: API 명세서 및 사용 가이드 업데이트` |
| **Style** | 코드 스타일 변경 (로직 영향 없음) | `Style: import 정리 및 코드 포맷 수정` |
| **Refactor** | 코드 리팩토링 (기능 변경 없음) | `Refactor: 감정 기록 로직 분리` |
| **Test** | 테스트 코드 추가/수정 | `Test: 감정 기록 저장 테스트 추가` |
| **Chore** | 빌드/설정/기타 작업 | `Chore: Gradle 의존성 업데이트` |
| **Comment** | 주석 추가/수정 | `Comment: 금융마음지수 계산 로직 설명 추가` |
| **Rename** | 파일/폴더명 변경 | `Rename: EmotionRecordDto → EmotionRecordResponse` |
| **Remove** | 파일/코드 삭제 | `Remove: 사용하지 않는 테스트 코드 삭제` |
| **Init** | 프로젝트 초기 세팅 | `Init: Spring Boot 프로젝트 초기화` |
| **Merge** | 브랜치 병합 | `Merge: feature/emotion-record` |
| **!BREAKING CHANGE** | 하위 호환 불가 변경 | `!BREAKING CHANGE: 감정 기록 수정 API 응답 구조 변경` |
| **!HOTFIX** | 운영 긴급 수정 | `!HOTFIX: 감정 기록 저장 실패 긴급 수정` |
    
</details>


## 🫂 BE Team
<table>
  <!-- BE 역할 행 -->
  <tr>
    <td align="center">Backend(Lead)</td>
    <td align="center">Backend</td>
    <td align="center">Backend</td>
    <td align="center">Backend</td>
    <td align="center">Backend</td>
    <td align="center">Backend</td>
  </tr>

  <!-- 사진 행 -->
  <tr>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/ye-zin" width="100px" alt="박예진" />
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/wonee1" width="100px" alt="김채원" />
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/na311ng" width="100px" alt="이나영" />
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/jangsh7" width="100px" alt="장서현" />
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/dosp74" width="100px" alt="한종서" />
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/yjhss" width="100px" alt="홍유진" />
    </td>
  </tr>

  <!-- 이름 행 (링크 포함) -->
  <tr>
    <td align="center"><a href="https://github.com/ye-zin"><b>토리/박예진</b></a></td>
    <td align="center"><a href="https://github.com/wonee1"><b>위니/김채원</b></a></td>
    <td align="center"><a href="https://github.com/na311ng"><b>나미/이나영</b></a></td>
    <td align="center"><a href="https://github.com/jangsh7"><b>헤카/장서현</b></a></td>
    <td align="center"><a href="https://github.com/dosp74"><b>제이/한종서</b></a></td>
    <td align="center"><a href="https://github.com/yjhss"><b>하루/홍유진</b></a></td>

  </tr>

  <!-- 역할 상세 행 -->
 <tr>
  <td align="center" valign="top">
    <b>감정 통계</b><br/>
    <img width="200" alt="감정 통계" src="https://github.com/user-attachments/assets/90b68c24-619d-4e0f-9075-6e41fdc20235" />
  </td>
  <td align="center" valign="top">
    <b>홈</b><br/>
    <img width="200" alt="홈" src="https://github.com/user-attachments/assets/3ba73c26-ce3c-4520-8459-822c0c32737e" />
  </td>
  <td align="center" valign="top">
    <b>인증/회원/페르소나</b><br/>
    <img width="200" alt="인증/회원" src="https://github.com/user-attachments/assets/b45948c7-ee4a-47b4-b049-b4d96adbb79b" />
  </td>
  <td align="center" valign="top">
    <b>기록/리포트/AI피드백</b><br/>
    <img width="200" alt="기록/리포트" src="https://github.com/user-attachments/assets/884a130d-54f6-4818-93b5-0973c10f6525" />
  </td>
  <td align="center" valign="top">
    <b>주식 데이터</b><br/>
    <img width="200" alt="주식 데이터" src="https://github.com/user-attachments/assets/184beaf7-020b-49fb-b21a-5f707120523b" />
  </td>
  <td align="center" valign="top">
    <b>연관 분석</b><br/>
    <img width="200" alt="연관 분석" src="https://github.com/user-attachments/assets/537f314e-ce72-4f75-b6a5-ef5a7dc924ae" />
  </td>
</tr>
</table>
