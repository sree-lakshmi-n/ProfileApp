const form = document.getElementById("loginForm");
const errorMsg = document.getElementById("errorMsg");
const successMsg = document.getElementById("successMsg");

if (window.location.search.includes("logout")) {
    successMsg.style.display = "block";
}

form.addEventListener("submit", function(event) {
    event.preventDefault(); 

    const email = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    if (email === "admin@example.com" && password === "123456") {
        alert("Login successful!");
        window.location.href = "home.html"; 
    } else {
        errorMsg.style.display = "block";
        successMsg.style.display = "none";
    }
});
