function signup() {
    const data = {
        userId: document.getElementById("userId").value.trim(),
        password: document.getElementById("password").value.trim(),
        birthDate: document.getElementById("birthDate").value.trim(),
        nickname: document.getElementById("nickname").value.trim(),
        phoneNumber: document.getElementById("phoneNumber").value.trim(),
        email: document.getElementById("email").value.trim()
    };

    // 전체 입력값 체크
    if (
        !data.userId ||
        !data.password ||
        !data.birthDate ||
        !data.nickname ||
        !data.phoneNumber ||
        !data.email
    ) {
        alert("모든 항목을 입력해주세요.");
        return;
    }

    // 아이디 형식 검사
    const idPattern = /^[a-zA-Z0-9]+$/;
    if (!idPattern.test(data.userId)) {
        alert("아이디는 영문과 숫자만 사용할 수 있습니다.");
        return;
    }

    // 비밀번호 형식 검사
    const pwPattern = /^[a-zA-Z0-9]+$/;
    if (!pwPattern.test(data.password)) {
        alert("비밀번호는 영문과 숫자만 사용할 수 있습니다.");
        return;
    }

    // 비밀번호 길이 검사
    if (data.password.length < 4 || data.password.length > 12) {
        alert("비밀번호는 4~12자 사이로 입력해주세요.");
        return;
    }

    // 비밀번호 확인 검사
    if (!isPasswordMatched) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    // 생년월일 형식 검사
    const birthPattern = /^\d{4}-\d{2}-\d{2}$/;
    if (!birthPattern.test(data.birthDate)) {
        alert("생년월일은 YYYY-MM-DD 형식으로 입력해주세요.");
        return;
    }

    // 전화번호 형식 검사
    const phonePattern = /^\d{3}-\d{4}-\d{4}$/;
    if (!phonePattern.test(data.phoneNumber)) {
        alert("전화번호는 010-1234-5678 형식으로 입력해주세요.");
        return;
    }

    // 이메일 형식 검사
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(data.email)) {
        alert("올바른 이메일 형식을 입력해주세요.");
        return;
    }

    // 아이디 중복 확인 검사
    if (!isIdChecked) {
        alert("아이디 중복 확인을 해주세요.");
        return;
    }

    if (!isIdAvailable) {
        alert("사용할 수 없는 아이디입니다.");
        return;
    }

    fetch("/user/signup", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("회원가입 실패");
            }
            return response.text();
        })
        .then(result => {
            alert(result);
            window.location.href = "/login";
        })
        .catch(error => {
            console.error(error);
            alert("회원가입 중 오류 발생");
        });
}

// 상태 변수
let isIdChecked = false;
let isIdAvailable = false;
let isPasswordMatched = false;

// 아이디 중복 확인 함수
function checkId() {
    const userId = document.getElementById("userId").value.trim();

    if (!userId) {
        alert("아이디를 입력하세요.");
        return;
    }

    fetch(`/user/check-id?userId=${userId}`)
        .then(response => response.json())
        .then(isDuplicate => {
            isIdChecked = true;
            isIdAvailable = !isDuplicate;

            const result = document.getElementById("idCheckResult");

            if (isDuplicate) {
                result.innerText = "이미 사용 중인 아이디입니다.";
                result.style.color = "red";
            } else {
                result.innerText = "사용 가능한 아이디입니다.";
                result.style.color = "green";
            }
        })
        .catch(error => {
            console.error(error);
            alert("중복 확인 실패");
        });
}

document.addEventListener("DOMContentLoaded", () => {
    const userIdInput = document.getElementById("userId");
    const passwordInput = document.getElementById("password");
    const confirmInput = document.getElementById("passwordConfirm");
    const birthInput = document.getElementById("birthDate");
    const phoneInput = document.getElementById("phoneNumber");

    // 아이디: 영문 + 숫자만 허용
    userIdInput.addEventListener("input", () => {
        userIdInput.value = userIdInput.value.replace(/[^a-zA-Z0-9]/g, "");

        // 아이디가 바뀌면 중복확인 다시 해야 함
        isIdChecked = false;
        isIdAvailable = false;
        document.getElementById("idCheckResult").innerText = "";
    });

    // 비밀번호: 영문 + 숫자만 허용
    passwordInput.addEventListener("input", () => {
        passwordInput.value = passwordInput.value.replace(/[^a-zA-Z0-9]/g, "");

        // 비밀번호 변경 시 확인값과 다시 비교
        validatePasswordMatch();
    });

    // 비밀번호 확인 입력 시 일치 검사
    confirmInput.addEventListener("input", () => {
        validatePasswordMatch();
    });

    // 생년월일: 숫자만 허용 + 8자리 제한 + 하이픈 자동 추가
    birthInput.addEventListener("input", () => {
        let value = birthInput.value.replace(/[^0-9]/g, "");

        if (value.length > 8) {
            value = value.slice(0, 8);
        }

        if (value.length >= 5 && value.length <= 6) {
            value = value.replace(/(\d{4})(\d+)/, "$1-$2");
        } else if (value.length >= 7) {
            value = value.replace(/(\d{4})(\d{2})(\d+)/, "$1-$2-$3");
        }

        birthInput.value = value;
    });

    // 전화번호: 숫자만 허용 + 11자리 제한 + 하이픈 자동 추가
    phoneInput.addEventListener("input", () => {
        let value = phoneInput.value.replace(/[^0-9]/g, "");

        if (value.length > 11) {
            value = value.slice(0, 11);
        }

        if (value.length >= 7) {
            value = value.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3");
        } else if (value.length >= 4) {
            value = value.replace(/(\d{3})(\d+)/, "$1-$2");
        }

        phoneInput.value = value;
    });
});

// 비밀번호 일치 검사 함수
function validatePasswordMatch() {
    const password = document.getElementById("password").value;
    const confirm = document.getElementById("passwordConfirm").value;
    const result = document.getElementById("passwordCheckResult");

    if (!confirm) {
        result.innerText = "";
        isPasswordMatched = false;
        return;
    }

    if (password === confirm) {
        result.innerText = "비밀번호가 일치합니다.";
        result.style.color = "green";
        isPasswordMatched = true;
    } else {
        result.innerText = "비밀번호가 일치하지 않습니다.";
        result.style.color = "red";
        isPasswordMatched = false;
    }
}