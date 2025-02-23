import { useEffect, useState} from "react";
import {fetchWrap} from "../fetchWrap";
import {useLocation, useNavigate} from "react-router-dom";
import {Link} from "react-router-dom";

const Navbar = () => {

    const [isauthenticated, setIsauthenticated] = useState(false);

    const location = useLocation();

    const [search, setSearch] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        const getUser = async () => {
            try {
                const response = await fetchWrap("http://localhost/api/users/me");
                if (response.ok) {
                    setIsauthenticated(true);
                }
            }
            catch (error) {
                console.log(error);
            }
        }
        getUser();
    },[]);

    const handleSubmit = (evt) => {
        evt.preventDefault();
        navigate("/search?query=" + search);
        setSearch("");
    }

    const handleLoginClick = () => {
        localStorage.setItem("prevUrl", location.pathname + location.search);
        navigate("/login");
    };


    return (
        <nav className="navbar navbar-expand-lg" style={{ borderBottom: "solid 1px #5c5a5a" }}>
            <div className="container">
                <Link className="navbar-brand" to="/">Blog</Link>
                <div className="collapse navbar-collapse ms-2" id="navbarSupportedContent">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li>
                            <form className="d-flex" role="search" onSubmit={handleSubmit}>
                                <input className="form-control me-2 rounded-5" type="search" placeholder="Tìm kiếm..." aria-label="Search" onChange={(e) => setSearch(e.target.value)} />
                                <button className="btn btn-outline-success rounded-5 w-auto" type="submit">Search</button>
                            </form>
                        </li>
                    </ul>
                    <div className="d-flex align-items-center gap-2">
                        {isauthenticated ? (
                            <>
                                <div className="d-flex align-items-center">
                                    {/* Nút viết bài */}
                                    <Link to="/posts/upload" style={{ textDecoration: "none", color: "black" }}>
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            width="24"
                                            height="24"
                                            fill="currentColor"
                                            className="bi bi-pen"
                                            viewBox="0 0 16 16"
                                        >
                                            <path
                                                d="m13.498.795.149-.149a1.207 1.207 0 1 1 1.707 1.708l-.149.148a1.5 1.5 0 0 1-.059 2.059L4.854 14.854a.5.5 0 0 1-.233.131l-4 1a.5.5 0 0 1-.606-.606l1-4a.5.5 0 0 1 .131-.232l9.642-9.642a.5.5 0 0 0-.642.056L6.854 4.854a.5.5 0 1 1-.708-.708L9.44.854A1.5 1.5 0 0 1 11.5.796a1.5 1.5 0 0 1 1.998-.001m-.644.766a.5.5 0 0 0-.707 0L1.95 11.756l-.764 3.057 3.057-.764L14.44 3.854a.5.5 0 0 0 0-.708z"
                                            />
                                        </svg>
                                        Viết bài
                                    </Link>

                                    {/*/!* Thông báo *!/*/}
                                    {/*<div className="ms-3">*/}
                                    {/*    <a href="/" style={{ textDecoration: "none", color: "black" }}>*/}
                                    {/*        <svg*/}
                                    {/*            xmlns="http://www.w3.org/2000/svg"*/}
                                    {/*            width="24"*/}
                                    {/*            height="24"*/}
                                    {/*            fill="currentColor"*/}
                                    {/*            className="bi bi-bell"*/}
                                    {/*            viewBox="0 0 16 16"*/}
                                    {/*        >*/}
                                    {/*            <path*/}
                                    {/*                d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2M8 1.918l-.797.161A4 4 0 0 0 4 6c0 .628-.134 2.197-.459 3.742-.16.767-.376 1.566-.663 2.258h10.244c-.287-.692-.502-1.49-.663-2.258C12.134 8.197 12 6.628 12 6a4 4 0 0 0-3.203-3.92zM14.22 12c.223.447.481.801.78 1H1c.299-.199.557-.553.78-1C2.68 10.2 3 6.88 3 6c0-2.42 1.72-4.44 4.005-4.901a1 1 0 1 1 1.99 0A5 5 0 0 1 13 6c0 .88.32 4.2 1.22 6"*/}
                                    {/*            />*/}
                                    {/*        </svg>*/}
                                    {/*    </a>*/}
                                    {/*</div>*/}

                                    {/* Dropdown Avatar */}
                                    <div className="dropdown ms-3">
                                        <button
                                            className="btn dropdown-toggle d-flex align-items-center"
                                            type="button"
                                            id="dropdownMenuButton"
                                            data-bs-toggle="dropdown"
                                            aria-expanded="false"
                                            style={{ border: "none", backgroundColor: "transparent" }}
                                        >
                                            {/* Avatar bo tròn */}
                                            <img
                                                src="http://localhost/api/images/avatar.png" // Thay bằng link avatar thực tế
                                                alt="Avatar"
                                                className="rounded-circle"
                                                style={{ width: "40px", height: "40px", objectFit: "cover" }}
                                            />
                                        </button>
                                        <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownMenuButton">
                                            <li>
                                                <Link className="dropdown-item" to="/me/story">
                                                    Your Story
                                                </Link>
                                            </li>
                                            <li>
                                                <form method="POST" action="http://localhost/api/logout">
                                                    <button type="submit" className="dropdown-item">
                                                        Đăng xuất
                                                    </button>
                                                </form>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </>

                        ) : (
                            <form className="d-flex" onSubmit={handleLoginClick}>
                                <button className="btn btn-outline-success rounded-5" type="submit">Đăng Nhập</button>
                            </form>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
