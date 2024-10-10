# 🛒E-Commerce
## MileStone
### 1주차 : 시나리오 분석 및 프로젝트 준비 (10/5 ~ 10/11) 
- 개발 환경 준비 (2H)
- 마일스톤 작성 (2H)
- 시나리오 요구사항 분석 (2H)
  - 시퀀스 다이어그램 작성 (3H)
  - 플로우 차트 작성 (2H)
  - ERD 설계 (3H)
- 패키지 구조 작성 (3H)
- Mock API 작성 (8H)

### 2주차 : 비즈니스 로직 설계 (10/12 ~ 10/18)
- TDD 작성 (4H)
- 기본(상품 조회, 상위 상품 조회) 기능 구현 (5H)
- 주요(잔액 충전/조회, 잔액 충전/조회) 기능 구현 (7H)
- 예외사항 누락 확인 및 점검 (2H)
- 통합 테스트 (3H)

### 3주차 : 고도화 (10/19 ~ 10/24)
- 동시성 제어 케이스 확인 (2H)
- 동시성을 위한 통합 테스트 작성 (5H)
- 동시성 이슈 처리 (Redis 등 이용, 분산 락) (6H)

## 시퀀스 다이어그램
![상품 조회](/src/main/resources/images/Product_inquiry.png)
![잔액 조회](/src/main/resources/images/Balance_inquiry.png)
![잔액 충전](/src/main/resources/images/Balance_recharge.png)
![주문 및 결제](/src/main/resources/images/Order_payment.png)
![인기 상품 조회](/src/main/resources/images/Search_popular_products.png)

## 플로우 차트
![주문 및 결제](/src/main/resources/images/Flowchart_payment.png)
![장바구니](/src/main/resources/images/Flowchart_cart.png)
