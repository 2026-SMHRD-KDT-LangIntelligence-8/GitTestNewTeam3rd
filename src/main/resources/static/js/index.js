document.addEventListener('DOMContentLoaded', async function () {
    await checkLoginStatus();
    await fetchMainData();
});

// 로그인 상태 확인 후 버튼 변경
async function checkLoginStatus() {
    const loginBtn = document.getElementById('loginMoveBtn');

    if (!loginBtn) {
        console.error('loginMoveBtn 버튼을 찾을 수 없습니다.');
        return;
    }

    try {
        const response = await fetch('/user/session-check', {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            const data = await response.json();
            console.log('로그인 상태 확인 성공:', data);

            loginBtn.innerText = '로그아웃';
            loginBtn.onclick = logout;
        } else {
            console.log('비로그인 상태');

            loginBtn.innerText = '로그인';
            loginBtn.onclick = goToLogin;
        }
    } catch (error) {
        console.error('세션 확인 오류:', error);

        loginBtn.innerText = '로그인';
        loginBtn.onclick = goToLogin;
    }
}

// 로그인 페이지 이동
function goToLogin() {
    window.location.href = '/login';
}

// 로그아웃
async function logout() {
    try {
        const response = await fetch('/user/logout', {
            method: 'POST',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('로그아웃 실패');
        }

        alert('로그아웃 되었습니다.');
        window.location.href = '/';
    } catch (error) {
        console.error(error);
        alert('로그아웃 중 오류가 발생했습니다.');
    }
}

// 메인 데이터 조회
async function fetchMainData() {
    try {
        const response = await fetch('/api/main/dashboard', {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) throw new Error('데이터 로드 실패');

        const data = await response.json();

        let challengeName = data.challengeName;
        if (challengeName === "진행 중인 챌린지가 없습니다" || !challengeName) {
            challengeName = "진행 중인 챌린지가 없어요!";
        }

        document.getElementById('challengeNameDisplay').innerText = challengeName;

        let message = "";
        const formattedToday = data.todayUsage.toLocaleString();

        if (data.challengeName === "진행 중인 챌린지가 없습니다" || !data.challengeName) {
            message = "오늘은 " + formattedToday + "원을 사용했어요!<br>새로운 목표를 설정해볼까요?";
        } else {
            message = "오늘은 " + formattedToday + "원을 사용했어요!";
            if (data.overBudget) {
                message += "<br><span style='color: #FF5A5A;'>내일은 지출을 줄여야 해요!</span>";
            }
        }

        document.getElementById('bubbleMessage').innerHTML = message;
        document.getElementById('accumulatedUsage').innerText = data.accumulatedUsage.toLocaleString();

        const progress = data.progressPercent;
        document.getElementById('progressPercent').innerText = progress.toFixed(1) + '%';
        document.getElementById('progressBarFill').style.width = Math.min(100, progress) + '%';

        if (data.overBudget) {
            document.getElementById('mainDashboard').classList.add('is-over');
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('bubbleMessage').innerText = "데이터를 가져오지 못했어요. 🍙";
    }
}