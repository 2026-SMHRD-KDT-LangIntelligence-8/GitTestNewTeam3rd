function signup() {

    const data = {
        userId: document.getElementById("userId").value,
        password: document.getElementById("password").value,
        birthDate: document.getElementById("birthDate").value,
        nickname: document.getElementById("nickname").value,
        phoneNumber: document.getElementById("phoneNumber").value,
        email: document.getElementById("email").value
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
            window.location.href = "/signup";
        })
        .catch(error => {
            console.error(error);
            alert("회원가입 중 오류 발생");
        });
}