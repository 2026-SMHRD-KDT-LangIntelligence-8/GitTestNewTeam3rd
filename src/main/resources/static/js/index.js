document.addEventListener('DOMContentLoaded', async function () {
    // 로그인 상태 확인 후, 메인 페이지로 이동
    const userId = await checkLoginStatus();
    await fetchMainData();

    // 2. 햄버거 메뉴 초기화
    initWaraakMenu(userId);
});

// 햄버거 메뉴 제어 로직
function initWaraakMenu(userId) {
    const menuTrigger = document.getElementById('menuTrigger');
    const menuWrapper = document.getElementById('menuWrapper');

    if (menuTrigger && menuWrapper) {
        // 버튼 클릭 시 메뉴 열기/닫기 토글
        menuTrigger.addEventListener('click', function(e) {
            e.stopPropagation(); // 부모로 클릭 이벤트 전파 방지
            menuWrapper.classList.toggle('active');
        });

        // 메뉴 바깥 영역 클릭 시 자동으로 닫기
        document.addEventListener('click', function(e) {
            if (!menuWrapper.contains(e.target)) {
                menuWrapper.classList.remove('active');
            }
        });

        // --- 🍙 메뉴 버튼 경로 연동 ---
        const subButtons = document.querySelectorAll('.sub-btn');

        if (subButtons.length >= 4) {
            // 1. 오늘의 소비 (💰)
            subButtons[0].onclick = function() {
                if (userId) {
                    window.location.href = `/api/recordMain/${userId}`;
                } else {
                    alert("로그인이 필요한 서비스입니다! 🍙");
                    goToLogin();
                }
            };

            // 2. 소비 캘린더 (📅)
            subButtons[1].onclick = function() {
                // [수정] CalendarController 경로(/api/dailyCalendar/{userId}) 연동
                if (userId) {
                    window.location.href = `/api/dailyCalendar/${userId}`;
                } else {
                    alert("로그인이 필요한 서비스입니다! 🍙");
                    goToLogin();
                }
            };

            // 3. 챌린지 설정 (🏆) - BudgetController 주소 연동
            subButtons[2].onclick = function() {
                window.location.href = '/api/budget/create-page';
            };

            // 4. 마이페이지 (⚙️)
            subButtons[3].onclick = function() {
                window.location.href = '/mypage';
            };
        }
    }
}

// 로그인 상태 확인 후 버튼/닉네임 표시 변경
async function checkLoginStatus() {
    const loginBtn = document.getElementById('loginMoveBtn');
    const loginUserText = document.getElementById('loginUserText');

    if (!loginBtn || !loginUserText) {
        console.error('헤더 요소를 찾을 수 없습니다.');
        return null;
    }

    try {
        const response = await fetch('/user/session-check', {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            const data = await response.json();
            console.log('로그인 상태 확인 성공:', data);

            loginUserText.innerText = (data.nickname || '') + '님';
            loginBtn.innerText = '로그아웃';
            loginBtn.onclick = logout;

            return data.userId; // Response 탭에서 확인한 userId 반환
        } else {
            loginUserText.innerText = '';
            loginBtn.innerText = '로그인';
            loginBtn.onclick = goToLogin;
            return null;
        }
    } catch (error) {
        console.error('세션 확인 오류:', error);
        loginUserText.innerText = '';
        loginBtn.innerText = '로그인';
        loginBtn.onclick = goToLogin;
        return null;
    }
}

// 로그인 페이지 이동
function goToLogin() {
    window.location.href = '/login';
}

// 로그아웃
async function logout() {
    if(!confirm('로그아웃 하시겠습니까?')) return; // 깨알 같은 확인창 추가 ㅋ

    try {
        const response = await fetch('/user/logout', {
            method: 'POST',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('로그아웃 실패');
        }

        alert('로그아웃 되었습니다.');
        // [수정] 에러 방지를 위해 유저님이 확인해주신 기본창인 /login으로 확실하게 리다이렉트 합니다.
        window.location.href = '/login';
    } catch (error) {
        console.error(error);
        alert('로그아웃 중 오류가 발생했습니다.');
    }
}

// 메인 데이터 조회 (기존 코드 유지)
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