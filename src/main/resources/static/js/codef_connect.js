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
    loadAccounts();
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
                "카드 연결 실패. 다시 시도해주세요.";
            throw new Error(errorMessage);
        }

        alert("카드 연결 성공!");

        resetForm();
        loadAccounts();

    } catch (error) {
        console.error("connectAccount 에러:", error);
        alert(error.message || "연결 중 오류 발생. 다시 시도해주세요.");
    }
}

// ===== 연결된 계정 목록 조회 =====
async function loadAccounts() {
    try {
        const response = await fetch("/codef/accounts", {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("계정 목록 조회 실패");
        }

        const list = await response.json();
        const accountList = document.getElementById("accountList");

        if (!accountList) {
            console.error("accountList 요소를 찾지 못했습니다.");
            return;
        }

        accountList.innerHTML = "";

        if (!list || list.length === 0) {
            accountList.innerHTML = `<div class="account-item">연결된 계정이 없습니다.</div>`;
            return;
        }

        list.forEach(account => {
            const item = document.createElement("div");
            item.className = "account-item";

            item.innerHTML = `
                <span>${account.loginId || "-"}</span>
                <span 
                    style="float: right; cursor: pointer; color: #e74c3c; font-weight: bold;"
                    onclick="deleteAccount(${account.id})"
                >✕</span>
            `;

            accountList.appendChild(item);
        });

    } catch (error) {
        console.error("loadAccounts 에러:", error);
    }
}

// ===== 연결된 계정 삭제 =====
async function deleteAccount(id) {
    if (!confirm("이 계정을 삭제하시겠습니까?")) {
        return;
    }

    try {
        const response = await fetch(`/codef/account/${id}`, {
            method: "DELETE",
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("계정 삭제 실패");
        }

        alert("계정이 삭제되었습니다.");
        loadAccounts();

    } catch (error) {
        console.error("deleteAccount 에러:", error);
        alert("계정 삭제 중 오류가 발생했습니다.");
    }
}

// ===== 입력 초기화 =====
function resetForm() {
    document.getElementById("organization").value = "";
    document.getElementById("loginId").value = "";
    document.getElementById("password").value = "";
    document.getElementById("accountAlias").value = "";
}