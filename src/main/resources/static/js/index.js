document.addEventListener('DOMContentLoaded', async function () {
    // 1. 로그인 상태 확인
    const userId = await checkLoginStatus();

    // 2. 메인 데이터 로드
    await fetchMainData();

    // 3. 햄버거 메뉴 초기화
    initWaraakMenu(userId);
});

// 햄버거 메뉴 제어 로직
function initWaraakMenu(userId) {
    const menuTrigger = document.getElementById('menuTrigger');
    const menuWrapper = document.getElementById('menuWrapper');

    if (menuTrigger && menuWrapper) {
        menuTrigger.addEventListener('click', function(e) {
            e.stopPropagation();
            menuWrapper.classList.toggle('active');
        });

        document.addEventListener('click', function(e) {
            if (!menuWrapper.contains(e.target)) {
                menuWrapper.classList.remove('active');
            }
        });

        const subButtons = document.querySelectorAll('.sub-btn');

        if (subButtons.length >= 4) {
            subButtons[0].onclick = function() {
                if (userId) {
                    window.location.href = `/api/recordMain/${userId}`;
                } else {
                    alert("로그인이 필요한 서비스입니다! 🍙");
                    goToLogin();
                }
            };

            subButtons[1].onclick = function() {
                if (userId) {
                    window.location.href = `/api/dailyCalendar/${userId}`;
                } else {
                    alert("로그인이 필요한 서비스입니다! 🍙");
                    goToLogin();
                }
            };

            subButtons[2].onclick = function() {
                if (userId) {
                    window.location.href = '/api/budget/create-page';
                } else {
                    alert("로그인이 필요한 서비스입니다! 🍙");
                    goToLogin();
                }
            };

            subButtons[3].onclick = function() {
                window.location.href = '/account_settings';
            };
        }
    }
}

// 로그인 상태 확인 및 UI 업데이트
async function checkLoginStatus() {
    const loginBtn = document.getElementById('loginMoveBtn');
    const nicknameDisplay = document.getElementById('nicknameDisplay');

    try {
        const response = await fetch('/user/session-check', {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            const data = await response.json();

            if (data.loggedIn) {
                if (nicknameDisplay) nicknameDisplay.innerText = data.nickname;
                if (loginBtn) {
                    loginBtn.innerText = '로그아웃';
                    loginBtn.onclick = logout;
                }
                return data.userId;
            }
        }

        if (nicknameDisplay) nicknameDisplay.innerText = '손님';
        if (loginBtn) {
            loginBtn.innerText = '로그인';
            loginBtn.onclick = goToLogin;
        }
        return null;

    } catch (error) {
        console.error('세션 확인 오류:', error);
        return null;
    }
}

function goToLogin() { window.location.href = '/login'; }

async function logout() {
    if(!confirm('로그아웃 하시겠습니까? 🍙')) return;
    try {
        const response = await fetch('/user/logout', { method: 'POST', credentials: 'include' });
        if (response.ok) {
            alert('로그아웃 되었습니다.');
            window.location.href = '/login';
        }
    } catch (error) { console.error(error); }
}

async function fetchMainData() {
    try {
        const response = await fetch('/api/main/dashboard', { method: 'GET', credentials: 'include' });
        if (!response.ok) throw new Error('데이터 로드 실패');

        const data = await response.json();
        console.log("서버 데이터 확인:", data); // 서버에서 넘어오는 실제 값 확인용

        // 1. 챌린지 이름 표시
        const challengeNameDisplay = document.getElementById('challengeNameDisplay');
        if (challengeNameDisplay) {
            challengeNameDisplay.innerText = data.challengeName || "진행 중인 챌린지가 없어요!";
        }

        // 2. 말풍선 메시지 및 캐릭터 이미지 변경
        const charImg = document.getElementById('characterImg');
        const bubbleMsg = document.getElementById('bubbleMessage');

        // 데이터가 없을 경우를 대비한 기본값 처리 (toLocaleString은 숫자에만 작동함)
        const todayUsage = data.todayUsage || 0;
        let message = `오늘은 ${todayUsage.toLocaleString()}원을 사용했어요!`;

        if (data.overBudget) {
            message += "<br><span style='color: #FF5A5A;'>내일은 지출을 줄여야 해요!</span>";
            if (charImg) charImg.src = '/img/sad_meokbap.png';
        } else {
            if (charImg) charImg.src = '/img/normal_meokbap.png';
        }

        if (bubbleMsg) {
            bubbleMsg.innerHTML = message;
        }

        // 3. 누적 금액 및 프로그레스 바 (가장 중요한 부분!)
        const accUsage = document.getElementById('accumulatedUsage');
        const progPercent = document.getElementById('progressPercent');
        const barFill = document.getElementById('progressBarFill');

        if (accUsage) {
            accUsage.innerText = (data.accumulatedUsage || 0).toLocaleString();
        }

        // progressPercent가 서버에서 오지 않을 경우를 대비해 0으로 처리
        const progress = data.progressPercent || 0;

        if (progPercent) {
            progPercent.innerText = progress.toFixed(1) + '%';
        }

        if (barFill) {
            // style.width에 반드시 '%' 문자를 붙여야 CSS가 인식함!
            barFill.style.width = Math.min(100, progress) + '%';
        }

    } catch (error) {
        console.error('Error:', error);
        const bubbleMsg = document.getElementById('bubbleMessage');
        if (bubbleMsg) {
            bubbleMsg.innerText = "데이터를 가져오지 못했어요. 🍙";
        }
    }
}