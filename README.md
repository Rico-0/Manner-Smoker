
# Manner-Smoker

## 1. 사용한 기술
- Retrofit (Java Spring 언어로 구현된 백엔드와 통신)
- KakaoMap API
- DataBinding
- MaterialCalendarView (흡연량 데이터 달력으로 표시)
- MPAndroidChart (흡연량 데이터 그래프로 표시)
- Glide (이미지 로딩)

## 1.1 로그인
![1](https://user-images.githubusercontent.com/45986958/184149883-b04e1b22-bece-42ff-af03-3279705150a7.png)

- 소셜로그인(카카오 로그인)

## 1.2 앱 최초 실행 시
![2](https://user-images.githubusercontent.com/45986958/184150324-98b8276e-e380-4064-a68d-94728abd9780.png)

![3](https://user-images.githubusercontent.com/45986958/184150693-5ebc3c4c-f6f4-4729-873c-b59d5b90c884.png)

- 흡연을 시작한 날짜 설정 (DatePicker)

## 1.3 흡연 기록
![4 (1)](https://user-images.githubusercontent.com/45986958/184154396-dfec5e5c-10e5-45cf-854a-036c4b07b007.png)

- 빨간색 동그라미로 표시된 아이콘을 누를 경우 흡연량 기록 가능

## 1.4 Home

![28](https://user-images.githubusercontent.com/45986958/184157592-13d346d8-6d05-442e-9f59-2e172eaa227f.png)

![29](https://user-images.githubusercontent.com/45986958/184157623-440aaf48-ffdd-40ba-ad73-df9d1bbdf4c5.png)

![30](https://user-images.githubusercontent.com/45986958/184157643-64c96482-cb36-4df9-9ee3-ae16f7966674.png)

![31](https://user-images.githubusercontent.com/45986958/184157654-540bd607-b6c7-4b4f-81fa-ef2c13e404cf.png)

![32](https://user-images.githubusercontent.com/45986958/184157681-f6c7fe69-b493-44e7-9217-b96081c09477.png)

- 실시간으로 자신의 흡연량을 체크할 수 있는 화면
- 매일 사용자가 설정한 흡연량 (별도로 설정하지 않을 시 기본 10개비로 설정됨) 을 기준으로 하루 흡연량에 따라 상단의 얼굴 아이콘과 배경색이 변경됨
- 소비한 금액은 하루 단위가 아닌 앱을 설치한 시점 이후로 계속 누적됨 

## 1.5 Map
![5](https://user-images.githubusercontent.com/45986958/184153340-75f3e026-9cc3-4c9d-aaae-995b9652bdd0.png)

- 원래는 전국 단위의 흡연 부스 데이터를 제공하려고 했으나, 백엔드 쪽 사정으로 서울시의 9개 구 데이터로 한정되었음
- 구 이름 (예 : 중랑구) 를 입력하면 카카오맵에 마커로 흡연 부스의 위치가 표시됨

![6](https://user-images.githubusercontent.com/45986958/184153361-f81680b1-deab-439c-bdeb-3eedf8007b2e.png)

![7](https://user-images.githubusercontent.com/45986958/184153385-08f141e3-dd3f-4a53-ac94-4eead4f16242.png)

![8](https://user-images.githubusercontent.com/45986958/184153427-7d9eea30-a93f-4812-86d4-e0bdb3caf596.png)

- 마커 클릭 시 사용자의 현 위치로부터의 대략적인 거리가 나오며, 카카오맵이 설치되어 있는 경우 자동차/대중교통/도보 수단을 중 선택하여 원하는 흡연 부스로 이동할 수 있음

## 1.6 Community

![13](https://user-images.githubusercontent.com/45986958/184155569-6ffdf02d-526c-4790-b7f5-75f710e68023.png)

![14](https://user-images.githubusercontent.com/45986958/184155593-7ac591a3-5eb3-4c90-98e8-6243481462cc.png)

![15](https://user-images.githubusercontent.com/45986958/184155656-84b04a71-d972-41af-9e50-8626e9a8eb9a.png)

![16](https://user-images.githubusercontent.com/45986958/184155678-3873e92a-edf7-49c4-92e7-67fdbc0e5424.png)

![17](https://user-images.githubusercontent.com/45986958/184155697-e1a0d683-4e65-4f34-a9c3-b650bcd16487.png)

![18](https://user-images.githubusercontent.com/45986958/184155716-c619bb11-7ee2-4a1b-8a0f-1e01b30c6939.png)

![19](https://user-images.githubusercontent.com/45986958/184155746-7da8a8ed-fd27-4ac9-a9fd-fb0cf3c801bd.png)

![20](https://user-images.githubusercontent.com/45986958/184155763-eb772c5d-ad7c-4575-ac97-45a70eb1a5af.png)

![21](https://user-images.githubusercontent.com/45986958/184157518-891dac34-8f9f-457d-9c29-5ced3d37af6e.png)

![22](https://user-images.githubusercontent.com/45986958/184157529-53c0c42f-fdc1-4358-a7e2-67ba771389b6.png)

- 게시글 작성, 수정, 삭제 / 댓글 작성, 삭제 기능 지원
- 댓글 열람과 작성은 게시글을 클릭했을 시 가능

## 1.5 My
![9](https://user-images.githubusercontent.com/45986958/184153445-e581c7c5-38b3-4a90-ab26-9515a3ee18bf.png)

- 사용자가 카카오톡에서 등록한 프로필 사진과 닉네임을 띄워 줌

![10](https://user-images.githubusercontent.com/45986958/184153460-5b278be2-4d79-46c3-bdad-7b9a01a3e104.png)

![11](https://user-images.githubusercontent.com/45986958/184155541-795007e0-a559-4ee3-b16b-fc43c3b408d1.png)

- 설정 클릭 시 일일 흡연량을 설정할 수 있음 (최대 0개비 ~ 최대 20개비 (한갑) )

![12](https://user-images.githubusercontent.com/45986958/184155555-3695de81-471d-4f85-9e69-8eee654516c2.png)

- 흡연량의 지속적인 변경은 목표 달성에 좋지 않은 영향을 줄 수 있다고 판단하여 하루에 한 번만 설정하도록 제한

## 1.5.1 흡연 기록 보기 : 달력

![23](https://user-images.githubusercontent.com/45986958/184157536-31b57d1a-9a70-496d-bf5a-ec5a32612f3e.png)

- 오늘 날짜는 굵은 글씨로 표시됨

![24](https://user-images.githubusercontent.com/45986958/184157541-bf52780e-7b1e-4d9f-94e9-c612d8c362b5.png)

- 원하는 날짜를 클릭하면 해당 날짜의 흡연 기록(시간) 을 열람할 수 있음

![25](https://user-images.githubusercontent.com/45986958/184157552-0a302eff-3190-49c1-ad66-d2a7696b696a.png)

- 1 ~ 20 을 5구간씩 나눠 최상 ~ 최악까지의 흡연 정도를 결정하도록 했으며, 흡연을 많이 한 날일수록 좌측 상단의 담배개비 아이콘 색이 진해짐

## 1.5.1 흡연 기록 보기 : 그래프

![26](https://user-images.githubusercontent.com/45986958/184157568-f9393762-aee8-455d-896a-d45ae302cdc3.png)

![27](https://user-images.githubusercontent.com/45986958/184157579-a40aab8b-a5dc-450c-8cec-b781f81e7937.png)

- 한 달 기준으로 흡연량 현황을 제공하는 그래프
- 따라서 년도와 달을 선택하면 흡연량 데이터를 열람할 수 있음

## 1.6 News

![33](https://user-images.githubusercontent.com/45986958/184157706-f66f4520-361f-40cc-91db-cd6115d05e5a.png)

- 본래는 백엔드에서 데이터를 제공받기로 하였으나 다른 기능 구현으로 인해 시간이 촉박하여 프론트엔드 단에서 임의로 금연 관련 기사 데이터 삽입

## 아쉬웠던 점
- MVVM 패턴을 적용하여 데이터의 실시간 변화를 View에 보여주지 못한 점
  -> 따라서 코드 가독성이 전체적으로 떨어지는 문제가 생김
- UX/UI에 대한 이해도 부족으로 사용자 입장에서 매력적인 UI를 디자인하지 못한 점


