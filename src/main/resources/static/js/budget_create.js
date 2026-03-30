// 챌린지 설정 JS
document.addEventListener('DOMContentLoaded', function() {
    const today = new Date().toISOString().split('T')[0];

    // 1. 초기 날짜 세팅 (기본 1개월)
    initDates(today);

    // 2. 이벤트 리스너 등록
    document.getElementById('totalLimit').addEventListener('input', validateAndCalculate);
    document.getElementById('fixedCostSum').addEventListener('input', validateAndCalculate);
    document.getElementById('startDate').addEventListener('change', calculateDailyBudget);
    document.getElementById('endDate').addEventListener('change', calculateDailyBudget);

    // 토글 아이템 클릭 이벤트 (위임 방식)
    document.querySelectorAll('.toggle-item').forEach(item => {
        item.addEventListener('click', function() {
            const days = this.getAttribute('data-days');
            selectPeriod(days, this, today);
        });
    });

    // 시작하기 버튼
    document.querySelector('.btn-save').addEventListener('click', handleSave);

    // 뒤로가기 버튼 (메인 페이지 연동)
    document.getElementById('backBtn').addEventListener('click', function() {
        window.location.href = '/';
    });
});

// 초기 날짜 설정 함수
function initDates(today) {
    document.getElementById('startDate').value = today;
    let end = new Date();
    end.setDate(end.getDate() + 30);
    document.getElementById('endDate').value = end.toISOString().split('T')[0];
    calculateDailyBudget();
}

// 입력값이 0보다 작으면 0으로 고정
function validateAndCalculate(e) {
    if (e.target.value < 0) {
        e.target.value = 0;
    }
    calculateDailyBudget();
}

// 기간 선택 토글 함수
function selectPeriod(days, element, today) {
    document.querySelectorAll('.toggle-item').forEach(item => item.classList.remove('active'));
    element.classList.add('active');

    const customDiv = document.getElementById('customDateRange');
    const toggleArea = document.getElementById('toggleArea');

    if (days === 'custom') {
        customDiv.style.display = 'block';
        toggleArea.style.marginBottom = '45px';
    } else {
        customDiv.style.display = 'none';
        toggleArea.style.marginBottom = '65px';

        const dayNum = parseInt(days);
        document.getElementById('startDate').value = today;
        let end = new Date();
        end.setDate(end.getDate() + dayNum);
        document.getElementById('endDate').value = end.toISOString().split('T')[0];
    }
    calculateDailyBudget();
}

// 하루 권장 예산 계산
function calculateDailyBudget() {
    const total = parseInt(document.getElementById('totalLimit').value) || 0;
    const fixed = parseInt(document.getElementById('fixedCostSum').value) || 0;
    const start = new Date(document.getElementById('startDate').value);
    const end = new Date(document.getElementById('endDate').value);

    const diffTime = end - start;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

    if (diffDays > 0) {
        const daily = Math.max(0, Math.floor((total - fixed) / diffDays));
        document.getElementById('dailyAmount').innerText = daily.toLocaleString();
    } else {
        document.getElementById('dailyAmount').innerText = '0';
    }
}

// 저장 로직
async function handleSave() {
    const total = parseInt(document.getElementById('totalLimit').value) || 0;
    const fixed = parseInt(document.getElementById('fixedCostSum').value) || 0;

    if (total <= 0) {
        alert("총 예산을 입력해주세요.");
        return;
    }
    if (total < fixed) {
        alert("총 예산이 고정 지출보다 적습니다.");
        return;
    }

    alert("챌린지가 설정되었습니다! 🔥");
}