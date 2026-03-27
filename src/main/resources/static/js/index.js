/**
 * 주먹밥 메인 대시보드 (index.html) 전용 스크립트
 */
document.addEventListener('DOMContentLoaded', function() {
    fetchMainData();
});

async function fetchMainData() {
    try {
        const response = await fetch('/api/main/dashboard');
        if (!response.ok) throw new Error('데이터 로드 실패');

        const data = await response.json();

        // 1. 챌린지 이름 설정 (그래프 위쪽 왼쪽에 대입)
        var challengeName = data.challengeName;
        if (challengeName === "진행 중인 챌린지가 없습니다" || !challengeName) {
            challengeName = "진행 중인 챌린지가 없어요!";
        }
        document.getElementById('challengeNameDisplay').innerText = challengeName;

        // 2. 말풍선 메시지 조합
        var message = "";
        var formattedToday = data.todayUsage.toLocaleString();

        if (data.challengeName === "진행 중인 챌린지가 없습니다" || !data.challengeName) {
            message = "오늘은 " + formattedToday + "원을 사용했어요!<br>새로운 목표를 설정해볼까요?";
        } else {
            message = "오늘은 " + formattedToday + "원을 사용했어요!";
            if (data.overBudget) {
                message += "<br><span style='color: #FF5A5A;'>내일은 지출을 줄여야 해요!</span>";
            }
        }
        document.getElementById('bubbleMessage').innerHTML = message;

        // 3. 기타 데이터 세팅 (그래프 업데이트)
        document.getElementById('accumulatedUsage').innerText = data.accumulatedUsage.toLocaleString();

        var progress = data.progressPercent;
        document.getElementById('progressPercent').innerText = progress.toFixed(1) + '%';
        document.getElementById('progressBarFill').style.width = Math.min(100, progress) + '%';

        // 4. 예산 초과 시 스타일 변경
        if (data.overBudget) {
            document.getElementById('mainDashboard').classList.add('is-over');
        }

    } catch (error) {
        console.error('Error:', error);
        document.getElementById('bubbleMessage').innerText = "데이터를 가져오지 못했어요. 🍙";
    }
}