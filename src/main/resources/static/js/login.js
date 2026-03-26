function login() {
    const data = {
        userId: document.getElementById("userId").value.trim(),
        password: document.getElementById("password").value.trim()
    };

    if (!data.userId || !data.password) {
        alert("아이디와 비밀번호를 입력하세요.");
        return;
    }

    fetch("/user/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("로그인 실패");
            }
            return response.text();
        })
        .then(result => {
            alert(result);
            window.location.href = "/codef/connect-page";
        })
        .catch(error => {
            console.error(error);
            alert("아이디 또는 비밀번호를 확인하세요.");
        });
}