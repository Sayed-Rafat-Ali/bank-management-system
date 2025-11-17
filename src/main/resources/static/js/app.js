const BASE_URL = "http://localhost:8080/api/bank";

function get(value) {
    return document.getElementById(value).value;
}

// ========== CREATE ACCOUNT ===========
function createAccount() {
    let url = `${BASE_URL}/create?name=${get('name')}&phone=${get('phone')}&pin=${get('pin')}&balance=${get('balance')}&type=${get('type')}`;

    fetch(url, { method: "POST" })
        .then(r => r.text())
        .then(msg => alert(msg));
}

// ========== LOGIN ===========
function login() {
    fetch(`${BASE_URL}/login?phone=${get('phone')}&pin=${get('pin')}`, { method: "POST" })
        .then(r => r.text())
        .then(res => {
            if (res === "true") {
                localStorage.setItem("phone", get('phone'));
                window.location.href = "dashboard.html";
            } else {
                alert("Invalid Credentials!");
            }
        });
}

// ========== LOGOUT ===========
function logout() {
    localStorage.removeItem("phone");
    window.location.href = "index.html";
}

// ==========DEPOSIT=============
function depositMoney() {
    const phone = localStorage.getItem("phone");
    const amount = get("amount");

    fetch(`${BASE_URL}/deposit?phone=${phone}&amount=${amount}`, {
        method: "POST"
    })
    .then(r => r.text())
    .then(msg => alert(msg));
}

// =============Withdraw===========
function withdrawMoney() {
    const phone = localStorage.getItem("phone");
    const amount = get("amount");

    fetch(`${BASE_URL}/withdraw?phone=${phone}&amount=${amount}`, {
        method: "POST"
    })
    .then(r => r.text())
    .then(msg => alert(msg));
}

// ============Transfer===============
function transferMoney() {
    const sender = localStorage.getItem("phone");
    const receiver = get("receiver");
    const amount = get("amount");

    fetch(`${BASE_URL}/transfer?sender=${sender}&receiver=${receiver}&amount=${amount}`, {
        method: "POST"
    })
    .then(r => r.text())
    .then(msg => alert(msg));
}

// ============CHANGE-PIN================
function changePin() {
    const phone = localStorage.getItem("phone");
    const oldPin = get("oldPin");
    const newPin = get("newPin");

    fetch(`${BASE_URL}/changePin?phone=${phone}&oldPin=${oldPin}&newPin=${newPin}`, {
        method: "POST"
    })
    .then(r => r.text())
    .then(msg => alert(msg));
}

// =============TRANSACTIONS===============
function loadTransactions() {
    const phone = localStorage.getItem("phone");

    fetch(`${BASE_URL}/transactions?phone=${phone}`)
        .then(r => r.text())
        .then(data => {
            document.getElementById("history").innerHTML =
                "<pre>" + data + "</pre>";
        });
}

// ============CHECK BALANCE=================
function checkBalance() {
    let phone = document.getElementById("phone").value.trim();

    if (phone === "") {
        alert("Please enter phone number");
        return;
    }

    fetch(`${BASE_URL}/balance?phone=${phone}`)
        .then(res => res.text())
        .then(balance => {
            // Convert to 2 decimal places
            let amount = parseFloat(balance).toFixed(2);

            document.getElementById("result").innerHTML =
                `<span style="color: green; font-size: 22px;">
                    Balance: \u20B9 ${amount}
                 </span>`;
        })
        .catch(err => {
            console.error(err);
            alert("Error fetching balance!");
        });
}





