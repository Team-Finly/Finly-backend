# Finly-backend
Finly의 SpringBoot 서버 레포지토리입니다.

---
## 🧰 기술 스택
- Language: Java 21
- Framework: SpringBoot 4.0.1
- Build Tool: Gradle
- Database: MySQL, JPA, Redis
- Infra: AWS, Github Actions, Docker
- Auth: JWT, Spring Security
- 외부 API: OpenAI API, 
- 기타:

## 🗂️ 프로젝트 구조
```
Finly/
└── src/main/
    ├── java/
    │   └── com/umc/finly
    │       ├── FinlyApplication.java 
    │       ├── domain/
    │       │   ├── auth/
    │       │   │   ├── controller/
    │       │   │   ├── converter/
    │       │   │   ├── dto/
    │       │   │   ├── entity/
    │       │   │   ├── enums/
    │       │   │   ├── repository/
    │       │   │   └── service/
    │       │   ├── member/
    │       │   ├── record/
    │       │   ├── market/
    │       │   └── analysis/
    │       └── global/
    │           ├── config/             
    │           ├── entity/             
    │           ├── infra/
    │           ├── apiPayload/
    │           │   ├── code/
    │           │   ├── exception/
    │           │   └── validation/
    │           └── util/    
    └── resources/
        ├── application.yml       
        ├── application-secret.yml
        ├── ...             

```

## 📖 Git convention

### 작업 규칙
- 직접 main 브랜치에서 작업 금지  
  - 모든 개발은 develop 브랜치를 기준으로 진행
- 기능 단위로 브랜치를 생성하여 작업 후 PR을 통해 병합
  - 브랜치 이름은 기능/작업 내용이 명확하게 드러나도록 작성
- 모든 작업 시작 전, 작업(로컬) 브랜치에서 최신 develop 브랜치 pull하여 최신 상태 반영 후 작업 시작
- PR 생성 전 원격 develop 브랜치에 변경 사항이 있는 경우, 작업(로컬) 브랜치에 develop 브랜치 merge 후 PR 생성
- PR은 코드 리뷰 및 승인(2인 이상) 후 병합

### 작업 흐름
```
1. 작업 단위로 Issue 생성
2. develop 브랜치에서 작업용 브랜치 생성
3. 생성한 브랜치에서 작업 진행
4. 작업 완료 후 develop 브랜치로 Pull Request(PR) 생성
5. 코드 리뷰 및 승인 후 PR merge
```

### 브랜치 전략
#### Github Flow
- main : 프로젝트가 최종적으로 배포되는 브랜치
    - Production 환경에 언제 배포해도 문제없는 안정(stable) 브랜치입니다.
    - 장애나 긴급 버그 발생 시 main 브랜치에서 핫픽스를 진행합니다.
    - Initial commit을 제외하고 main 브랜치에 직접 커밋하지 않으며, 반드시 Pull Request로만 병합합니다.
- develop : 통합 개발 브랜치
    - 새로운 기능 개발 시 main을 기준으로 develop 브랜치를 생성합니다.
    - feature 브랜치들을 병합하는 곳입니다.
    - 모든 기능이 통합되고 버그가 수정된 후 main 브랜치로 PR을 생성합니다.
    - main 브랜치는 항상 안정적이어야 하며, 불완전한 작업은 develop에서 처리합니다.
- feat/* : 기능 개발 브랜치
    - 이슈 기반으로 브랜치를 생성하며, 브랜치명은 반드시 `feat/{도메인}-{이슈번호}-{기능명}` 형식을 따릅니다.
  예: `feat/auth-1-login`
    - develop 브랜치를 기준으로 분기하여 새로운 기능을 개발합니다.
    - 기능에 대한 버그 수정은 해당 feature 브랜치 내에서 완료 후 develop으로 PR을 생성합니다.
 - 기타: fix/* 등

#### 브랜치 네이밍
```
feat/{도메인-이슈번호-기능명}
```
예: `feat/auth-3-login`

### Issue
```
[Tag] Title
```
- 이슈 내용에는 다음을 포함:
  - 이슈 요약
  - 상세 내용
  - 체크리스트
  - 참고 사항
- Labels, Assignees 지정
  
### Pull Request (PR)
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
| Feat     | 새로운 기능 추가             |
| Fix      | 버그 수정                    |
| Docs     | 문서 수정                    |
| Style    | 코드 포맷/스타일 변경        |
| Refactor | 코드 리팩토링                |
| Test     | 테스트 코드 추가/수정        |
| Chore    | 빌드/설정/패키지 관련 작업   |
| Merge    | 브랜치 병합                  |


### Commit message
```
# 제목
Tag: Title

# 본문 (Body)
- 72자 내로 줄바꿈하여 작성
- 무엇을, 왜 변경했는지 상세히 설명

# 꼬리말 (Footer)
- (#이슈번호) 형식으로 작성하여 커밋과 Issue 연결
```

예:
```
Feat: 감정 기록 상세 조회 API 추가 (#12)

- 사용자가 일별/월별 감정 기록을 조회할 수 있는 API 구현
- 프론트에서 그래프, 통계 정보 바로 활용 가능하도록 설계
```

#### Commit message Tag 종류
| Type | 의미 | 사용 예시 |
|------|------|-----------|
| Feat | 새로운 기능 추가 | `Feat: 감정 기록 상세 조회 API 추가` |
| Fix | 버그 수정 | `Fix: 금융마음지수 계산 오류 수정` |
| Docs | 문서 수정 | `Docs: API 명세서 및 사용 가이드 업데이트` |
| Style | 코드 스타일 변경 (로직 영향 없음) | `Style: import 정리 및 코드 포맷 수정` |
| Refactor | 코드 리팩토링 (기능 변경 없음) | `Refactor: 감정 기록 로직 분리` |
| Test | 테스트 코드 추가/수정 | `Test: 감정 기록 저장 테스트 추가` |
| Chore | 빌드/설정/기타 작업 | `Chore: Gradle 의존성 업데이트` |
| Comment | 주석 추가/수정 | `Comment: 금융마음지수 계산 로직 설명 추가` |
| Rename | 파일/폴더명 변경 | `Rename: EmotionRecordDto → EmotionRecordResponse` |
| Remove | 파일/코드 삭제 | `Remove: 사용하지 않는 테스트 코드 삭제` |
| Init | 프로젝트 초기 세팅 | `Init: Spring Boot 프로젝트 초기화` |
| Merge | 브랜치 병합 | `Merge: feature/emotion-record` |
| !BREAKING CHANGE | 하위 호환 불가 변경 | `!BREAKING CHANGE: 감정 기록 수정 API 응답 구조 변경` |
| !HOTFIX | 운영 긴급 수정 | `!HOTFIX: 감정 기록 저장 실패 긴급 수정` |



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
    <td align="center">
      감정 통계<br>
      금융마음지수
    </td>
    <td align="center">
      KOSPI/KOSDAQ/FGI<br>
      실시간 인사이트
    </td>
    <td align="center">
      인증/회원<br>
      페르소나
    </td>
    <td align="center">
      기록 및 리포트<br>
      검색
    </td>
    <td align="center">
      주식 분석
    </td>
    <td align="center">
      연관 분석
    </td>
  </tr>
</table>
