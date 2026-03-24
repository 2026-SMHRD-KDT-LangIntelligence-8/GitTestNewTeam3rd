function signup() {

    const data = {
        userId: document.getElementById("userId").value.trim(),
        password: document.getElementById("password").value.trim(),
        birthDate: document.getElementById("birthDate").value.trim(),
        nickname: document.getElementById("nickname").value.trim(),
        phoneNumber: document.getElementById("phoneNumber").value.trim(),
        email: document.getElementById("email").value.trim()
    };

    // 간단한 입력값 체크
    if (!data.userId || !data.password) {
        alert("아이디와 비밀번호는 필수입니다.");
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

            // 👉 회원가입 성공 후 페이지 이동
            window.location.href = "/login";
        })
        .catch(error => {
            console.error(error);
            alert("회원가입 중 오류 발생");
        });
}