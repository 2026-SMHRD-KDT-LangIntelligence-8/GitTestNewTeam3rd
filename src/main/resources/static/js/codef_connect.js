// ===== 은행 목록 =====
const bankOrganizations = [
    { code: "0002", name: "산업은행" },
    { code: "0003", name: "기업은행" },
    { code: "0004", name: "국민은행" },
    { code: "0007", name: "수협은행" },
    { code: "0011", name: "농협은행" },
    { code: "0020", name: "우리은행" },
    { code: "0023", name: "SC은행" },
    { code: "0027", name: "씨티은행" },
    { code: "0031", name: "대구은행" },
    { code: "0032", name: "부산은행" },
    { code: "0034", name: "광주은행" },
    { code: "0035", name: "제주은행" },
    { code: "0037", name: "전북은행" },
    { code: "0039", name: "경남은행" },
    { code: "0045", name: "새마을금고" },
    { code: "0048", name: "신협은행" },
    { code: "0071", name: "우체국" },
    { code: "0081", name: "KEB하나은행" },
    { code: "0088", name: "신한은행" },
    { code: "0089", name: "K뱅크" }
];

// ===== 카드사 목록 (이미지 기준 반영) =====
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

// 은행 등록
bankOrganizations.forEach(org => {
    organizationMap[org.code] = org.name;
});

// 카드 등록
cardOrganizations.forEach(org => {
    organizationMap[org.code] = org.name;
});

// ===== 초기 실행 =====
document.addEventListener("DOMContentLoaded", () => {
    const accountTypeSelect = document.getElementById("accountType");

    renderOrganizationOptions(accountTypeSelect.value);

    accountTypeSelect.addEventListener("change", () => {
        renderOrganizationOptions(accountTypeSelect.value);
    });

    loadMyAccounts();
});

// ===== 기관 목록 렌더링 =====
function renderOrganizationOptions(accountType) {
    const organizationSelect = document.getElementById("organization");

    const organizations = accountType === "bank"
        ? bankOrganizations
        : cardOrganizations;

    organizationSelect.innerHTML = `
        <option value="">선택하세요</option>
        ${organizations.map(org => `
            <option value="${org.code}">${org.name}</option>
        `).join("")}
    `;
}

// ===== 계정 연결 =====
function connectAccount() {

    const data = {
        accountType: document.getElementById("accountType").value,
        organization: document.getElementById("organization").value,
        loginId: document.getElementById("loginId").value.trim(),
        password: document.getElementById("password").value.trim(),
        accountAlias: document.getElementById("accountAlias").value.trim(),
        loginType: "1"
    };

    // ✅ 사용자 입력 검증
    if (!data.accountType) {
        alert("구분을 선택해주세요.");
        return;
    }

    if (!data.organization) {
        alert("은행 또는 카드사를 선택해주세요.");
        return;
    }

    if (!data.loginId) {
        alert("로그인 아이디를 입력해주세요.");
        return;
    }

    if (!data.password) {
        alert("비밀번호를 입력해주세요.");
        return;
    }

    fetch("/codef/connect", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(data)
    })
        .then(async response => {
            const result = await response.json().catch(() => null);

            if (!response.ok) {
                const errorMessage =
                    typeof result === "string"
                        ? result
                        : (result && result.message ? result.message : "연결 실패");

                throw new Error(errorMessage);
            }

            return result;
        })
        .then(result => {
            const organizationName = organizationMap[data.organization] || data.organization;

            alert(
                result.message +
                "\n기관명: " + organizationName +
                "\nconnectedId: " + result.connectedId
            );

            resetForm();
            loadMyAccounts();
        })
        .catch(error => {
            console.error(error);
            alert("연결 중 오류 발생: " + error.message);
        });
}

// ===== 입력 초기화 =====
function resetForm() {
    const accountType = document.getElementById("accountType").value;

    renderOrganizationOptions(accountType);
    document.getElementById("loginId").value = "";
    document.getElementById("password").value = "";
    document.getElementById("accountAlias").value = "";
}

// ===== 목록 조회 =====
function loadMyAccounts() {
    fetch("/codef/my-accounts", {
        method: "GET",
        credentials: "include"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("목록 조회 실패");
            }
            return response.json();
        })
        .then(accounts => {
            const accountList = document.getElementById("accountList");

            if (!accounts.length) {
                accountList.innerHTML = "<p>아직 연결된 계정이 없습니다.</p>";
                return;
            }

            accountList.innerHTML = accounts.map(account => {
                const organizationName = organizationMap[account.organization] || account.organization;

                const typeName = account.organization.startsWith("03")
                    ? "카드"
                    : "계좌";

                return `
                    <div class="account-item">
                        <p><strong>구분:</strong> ${typeName}</p>
                        <p><strong>별칭:</strong> ${account.accountAlias ?? "-"}</p>
                        <p><strong>은행/카드사:</strong> ${organizationName}</p>
                        <p><strong>connectedId:</strong> ${account.connectedId}</p>
                    </div>
                `;
            }).join("");
        })
        .catch(error => {
            console.error(error);
            document.getElementById("accountList").innerHTML =
                "<p>계정 목록을 불러오지 못했습니다.</p>";
        });
}