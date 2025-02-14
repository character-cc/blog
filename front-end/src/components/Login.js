import { useEffect, useState } from "react";
import { fetchWrap } from "../fetchWrap";
import { useNavigate } from "react-router-dom";

export default function LoginForm() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [remember, setRemember] = useState(false);
    const [isRegister, setIsRegister] = useState(false);
    const [email, setEmail] = useState("");
    const [name, setName] = useState("");
    const [error, setError] = useState(""); // Thêm state để lưu lỗi

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(""); // Reset lỗi trước khi gửi request

        const url = isRegister ? "http://localhost/api/signup" : "http://localhost/api/signin";
        const body = isRegister
            ? { username, password, email, name }
            : { username, password };

        const options = {
            method: "POST",
            body: JSON.stringify(body),
            headers: {
                "Content-Type": "application/json",
            },
        };

        try {
            const response = await fetchWrap(url, options);
            const result = await response.json();

            if (response.ok) {
                var redirect = localStorage.getItem("prevUrl") || "http://localhost";
                localStorage.removeItem("prevUrl");
                    navigate(redirect);
            } else {
                if (typeof result.message === "object") {
                    const errorMessages = Object.values(result.message).join(" | "); // Ghép tất cả lỗi lại
                    setError(errorMessages);
                } else {
                    setError(result.message || "Có lỗi xảy ra, vui lòng thử lại.");
                }
            }
        } catch (error) {
            console.error("Request failed:", error);
            setError("Lỗi kết nối đến server, vui lòng thử lại!");
        }
    };

    useEffect(() => {
        const checkLogin = async () => {
            try {
                const response = await fetchWrap("http://localhost/api/users/me");
                if (response.ok) {
                    navigate("/");
                }
            } catch (error) {
                console.error("Login check failed:", error);
            }
        };
        checkLogin();
    }, []);

    return (
        <div className="container d-flex justify-content-center align-items-center vh-100">
            <div className="card p-4 shadow-lg" style={{ width: "450px" }}>
                <h3 className="text-center mb-3">{isRegister ? "Register" : "Login"}</h3>

                {error && (
                    <div className="alert alert-danger text-center">{error}</div>
                )}

                <form onSubmit={handleSubmit}>
                    {isRegister && (
                        <>
                            <div className="mb-3">
                                <label htmlFor="name" className="form-label">Full Name</label>
                                <input
                                    type="text"
                                    id="name"
                                    className="form-control"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    required={isRegister}
                                />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="email" className="form-label">Email</label>
                                <input
                                    type="email"
                                    id="email"
                                    className="form-control"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required={isRegister}
                                />
                            </div>
                        </>
                    )}

                    <div className="mb-3">
                        <label htmlFor="username" className="form-label">User Name</label>
                        <input
                            type="text"
                            id="username"
                            className="form-control"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input
                            type="password"
                            id="password"
                            className="form-control"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className="btn btn-primary w-100 mb-2">
                        {isRegister ? "Register" : "Login"}
                    </button>

                    {!isRegister && (
                        <button type="button" className="btn btn-danger w-100 mb-2">
                            <a href="http://localhost/api/oauth2/authorization/keycloak" style={{ color: "white", textDecoration: "none" }}>
                                <i className="fab fa-google me-2"></i> Sign in with Keycloak
                            </a>
                        </button>
                    )}

                    <div className="text-center mt-2">
                        <p>
                            {isRegister ? "Already have an account?" : "Don't have an account?"}{" "}
                            <a href="#" onClick={() => setIsRegister(!isRegister)}>
                                {isRegister ? "Login" : "Register"}
                            </a>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
}
