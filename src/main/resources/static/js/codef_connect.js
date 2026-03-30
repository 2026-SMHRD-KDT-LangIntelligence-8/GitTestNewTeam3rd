// ===== 카드사 목록 =====
const cardOrganizations = [
    { code: "0301", name: "KB카드" },
    { code: "0302", name: "현대카드" },
    { code: "0303", name: "삼성카드" },
    { code: "0304", name: "NH카드" },
    { code: "0305", name: "BC카드" },
    { code: "0306", name: "신한카드" },
    { code: "0307", name: "씨티카드" },
    { code: "0309", name: "우리카드" },
    { code: "0311", name: "롯데카드" },
    { code: "0313", name: "하나카드" },
    { code: "0315", name: "전북카드" },
    { code: "0316", name: "광주카드" },
    { code: "0320", name: "수협카드" },
    { code: "0321", name: "제주카드" }
];

// ===== 코드 → 이름 매핑 =====
const organizationMap = {};
cardOrganizations.forEach(org => {
    organizationMap[org.code] = org.name;
});

// ===== 초기 실행 =====
document.addEventListener("DOMContentLoaded", () => {
    renderOrganizationOptions();
});

// ===== 카드사 목록 렌더링 =====
function renderOrganizationOptions() {
    const organizationSelect = document.getElementById("organization");

    if (!organizationSelect) {
        console.error("organization select 요소를 찾지 못했습니다.");
        return;
    }

    organizationSelect.innerHTML = `
        <option value="">카드사를 선택하세요</option>
        ${cardOrganizations.map(org => `
            <option value="${org.code}">${org.name}</option>
        `).join("")}
    `;
}

// ===== 카드 연결 =====
async function connectAccount() {
    const data = {
        organization: document.getElementById("organization").value,
        loginId: document.getElementById("loginId").value.trim(),
        password: document.getElementById("password").value.trim(),
        accountAlias: document.getElementById("accountAlias").value.trim()
    };

    if (!data.organization) {
        alert("카드사를 선택해주세요.");
        return;
    }

    if (!data.loginId) {
        alert("카드사 로그인 아이디를 입력해주세요.");
        return;
    }

    if (!data.password) {
        alert("카드사 로그인 비밀번호를 입력해주세요.");
        return;
    }

    try {
        console.log("카드 연결 요청 시작", data);

        const response = await fetch("/codef/connect", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(data)
        });

        const result = await response.json().catch(() => null);

        console.log("카드 연결 응답 상태:", response.status);
        console.log("카드 연결 응답 바디:", result);

        if (!response.ok) {
            const errorMessage =
                result?.message ||
                result?.error ||
                "connectedId 발급 실패";
            throw new Error(errorMessage);
        }

        const organizationName = organizationMap[data.organization] || data.organization;

        alert(
            (result.message || "connectedId 발급 성공") +
            "\n카드사: " + organizationName +
            "\nconnectedId: " + (result.connectedId || "-")
        );

        resetForm();

    } catch (error) {
        console.error("connectAccount 에러:", error);
        alert("연결 중 오류 발생: " + error.message);
    }
}

// ===== 승인내역 불러오기 =====
async function loadApproval() {
    const organization = document.getElementById("organization").value;

    if (!organization) {
        alert("카드사를 먼저 선택해주세요.");
        return;
    }

    try {
        console.log("승인내역 요청 시작", organization);

        const response = await fetch("/codef/approval-list", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({
                organization: organization,
                startDate: "20260301",
                endDate: "20260330"
            })
        });

        const result = await response.json().catch(() => null);

        console.log("승인내역 응답 상태:", response.status);
        console.log("승인내역 응답 바디:", result);

        if (!response.ok) {
            const errorMessage =
                result?.message ||
                result?.error ||
                "승인내역 불러오기 실패";
            throw new Error(errorMessage);
        }

        alert("승인내역 가져오기 완료!");
        console.log("승인내역 결과:", result);

    } catch (error) {
        console.error("loadApproval 에러:", error);
        alert("승인내역 불러오기 중 오류 발생: " + error.message);
    }
}

// ===== 입력 초기화 =====
function resetForm() {
    document.getElementById("organization").value = "";
    document.getElementById("loginId").value = "";
    document.getElementById("password").value = "";
    document.getElementById("accountAlias").value = "";
}