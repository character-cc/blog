import React , {useEffect, useState} from "react";
import Post from "./Post";
import {Link} from "react-router-dom";
import {fetchWrap} from "../fetchWrap";
import parse from "html-react-parser";
import Navbar from "./Navbar";
import { useLocation } from "react-router-dom";
import Sidebar from "./Sidebar";


const Search = () => {
    const [selectedCategory, setSelectedCategory] = useState("Post");
    const [categories, setCategories] = useState(["Post"]);
    const [users, setUsers] = useState([]);
    const [posts, setPosts] = useState([]);
    const handleClickCategory = (category) => {
        setSelectedCategory(category);
        setEnd(false);
    }
    const [end , setEnd] = useState(false);
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const search = searchParams.get("query");

    useEffect(() => {
        const getData = async () => {
            try {
                const url = "http://localhost/api/posts/search?query=" +search;
                const response = await fetchWrap(url);
                const result = await response.json();
                console.log("API Response:", result);
                setPosts(result);
            } catch (error) {
                console.log("Error fetching data:", error);
            }
        };
        getData();
    }, [search]);
    const renderCategories = () => {
        return categories.map((category) => (
                (
                    <div className="col-2" key={category} onClick={() => handleClickCategory(category)} style={{
                        color: category === selectedCategory ? "green" : "black",
                        fontWeight: category === selectedCategory ? "bold" : "normal",
                        cursor: "pointer",
                    }}>{category}</div>
                )
            )
        )
    }

    const [count, setCount] = useState(1);
    const handleMoreClick = () => {
        console.log(selectedCategory);
        const getMore = async () => {
            try {
                const url = "http://localhost/api/posts/search?query=" +search + "&page=" + count;
                setCount(prevCount => prevCount + 1);
                const response = await fetchWrap(url);
                if (response.ok){
                    if(selectedCategory === "Post"){
                        const data = await response.json();
                        console.log(data);
                        if(data.length === 0){ setEnd(true); }
                        setPosts((prevPosts) => [...prevPosts, ...(data)]);
                    }
                }
                else {
                    console.log("Error fetching data:");
                }
            }
            catch (error) {
                console.log(error);
            }

        }
        getMore();
    }

    const SideBar = () => {
        if (selectedCategory === "Post") {
            return (
                users.map((user) => (
                    <>
                    <div className="row mt-2 p-3">
                        <div className="col-2">
                            <img className="img-fluid rounded-circle" src={user.avatar} width="60" height="60"/>
                        </div>
                        <div className="col-8">{user.username}</div>
                        <div className="col-2">
                            <button className="rounded-2 btn btn-primary btn-sm btn-block" onClick={() => handleClickButton(user.id)}>
                                {user.follow ? "Unfollow" : "Follow"}
                            </button>
                        </div>
                    </div>
                </>
                ))
            );
        } else {
            return (
                posts.map((post) => (
                    <div className="mt-5 ps-3">
                        <div className="row">
                            <div className="col-1">
                                <img src={post.avatarUser} alt="" width={20} height={20} className="rounded-circle"/>
                            </div>
                            <div className="col-8">
                                <i>{post.author}</i>
                            </div>
                        </div>
                        <div className="row mt-2">
                            <Link
                                to={`/posts/${post.id}`}
                                style={{textDecoration: "none", color: "black"}}
                            >
                                <h5 style={{cursor: "pointer"}}>{post.title}</h5>
                            </Link>
                        </div>
                        <div className="row">
                            <i>
                                {post.likes}
                                <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="16"
                                    height="16"
                                    fill="currentColor"
                                    className="bi bi-star"
                                    viewBox="0 0 16 16"
                                >
                                    <path
                                        d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.56.56 0 0 0-.163-.505L1.71 6.745l4.052-.576a.53.53 0 0 0 .393-.288L8 2.223l1.847 3.658a.53.53 0 0 0 .393.288l4.052.575-2.906 2.77a.56.56 0 0 0-.163.506l.694 3.957-3.686-1.894a.5.5 0 0 0-.461 0z"/>
                                </svg>
                            </i>
                        </div>
                    </div>
                ))

            );
        }
    }
    const handleClickButton = (id) =>{
        const followingUser = async () => {
            const response = await fetchWrap("http://localhost/api/follow/"+id);
            if(response.ok){
                setUsers((prevUsers) => {
                    return prevUsers.map(user => {
                        if(user.id === id){
                            return {
                                ...user,
                                follow: !user.follow,
                            }
                        }
                        return user;
                    })
                })
            }
        }
        followingUser();
    }
    const mainContent = () => {
        if(selectedCategory === "Post"){
            return (
                posts.map((post) => (
                        <div key={post.id}>
                            <div className="mt-5 p-3">
                                <div className="row">
                                    <div className="col-1">
                                        <img className="img-fluid rounded-circle" src={post.avatarUser}
                                             width={30} height={30}/>
                                    </div>
                                    <div className="col">
                                        <h5>{post.author}</h5>
                                    </div>
                                </div>
                                <div className="row mt-1">
                                    <div className="col-8">
                                        <Link to={`/posts/${post.id}`}
                                              style={{textDecoration: "none", color: "black"}}>
                                            <div className="row">
                                                <h3>{post.title}</h3>
                                            </div>
                                        </Link>
                                        <div className="row">
                                            <p>{post.content}</p>
                                        </div>
                                        <div className="row">
                                            <div className="ms-2">
                                                <div className="row">
                                                    <div className="col-2">
                                                        <i>{post.likes}
                                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                                 width="16" height="16"
                                                                 fill="currentColor" className="bi bi-star"
                                                                 viewBox="0 0 16 16">
                                                                <path
                                                                    d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.56.56 0 0 0-.163-.505L1.71 6.745l4.052-.576a.53.53 0 0 0 .393-.288L8 2.223l1.847 3.658a.53.53 0 0 0 .393.288l4.052.575-2.906 2.77a.56.56 0 0 0-.163.506l.694 3.957-3.686-1.894a.5.5 0 0 0-.461 0z"/>
                                                            </svg>
                                                        </i>
                                                    </div>
                                                    <div className="col-8">
                                                        <i>{post.comments}
                                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                                 width="16" height="16"
                                                                 fill="currentColor" className="bi bi-chat"
                                                                 viewBox="0 0 16 16">
                                                                <path
                                                                    d="M2.678 11.894a1 1 0 0 1 .287.801 11 11 0 0 1-.398 2c1.395-.323 2.247-.697 2.634-.893a1 1 0 0 1 .71-.074A8 8 0 0 0 8 14c3.996 0 7-2.807 7-6s-3.004-6-7-6-7 2.808-7 6c0 1.468.617 2.83 1.678 3.894m-.493 3.905a22 22 0 0 1-.713.129c-.2.032-.352-.176-.273-.362a10 10 0 0 0 .244-.637l.003-.01c.248-.72.45-1.548.524-2.319C.743 11.37 0 9.76 0 8c0-3.866 3.582-7 8-7s8 3.134 8 7-3.582 7-8 7a9 9 0 0 1-2.347-.306c-.52.263-1.639.742-3.468 1.105"/>
                                                            </svg>
                                                        </i>
                                                    </div>
                                                    <div className="col-2">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                             height="16"
                                                             fill="currentColor" className="bi bi-bookmarks"
                                                             viewBox="0 0 16 16">
                                                            <path
                                                                d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5zm2-1a1 1 0 0 0-1 1v10.566l3.723-2.482a.5.5 0 0 1 .554 0L11 14.566V4a1 1 0 0 0-1-1z"/>
                                                            <path
                                                                d="M4.268 1H12a1 1 0 0 1 1 1v11.768l.223.148A.5.5 0 0 0 14 13.5V2a2 2 0 0 0-2-2H6a2 2 0 0 0-1.732 1"/>
                                                        </svg>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-4">
                                        <img src={post.imageUrl} alt="Hình ảnh" width={185} height={125}/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
            )
        }
        else {
            return (
                users.map((user) => (
                    <>
                        <div className="row mt-2 p-3">
                            <div className="col-1">
                                <img className="img-fluid rounded-circle" src={user.avatar} width="60" height="60"/>
                            </div>
                            <div className="col-2 d-flex  py-2">{user.username}</div>
                            <div className="col-8 d-flex justify-content-center py-3">
                                <button className="rounded-2 btn btn-primary btn-sm btn-block" onClick={() => handleClickButton(user.id)} >
                                    {user.follow ? "Unfollow" : "Follow"}
                                </button>
                            </div>
                        </div>
                    </>
                ))
            )
        }
    }
    return (
        <>
            <Navbar/>
            <div className="container d-flex mt-5">
                <div className="w-75">
                    <div className="container">
                        <div className="ms-5">
                            <div className="mx-5">
                                <h2 className="mb-4">Kết quả tìm kiếm cho {search}</h2>
                                <div className="row p-3" style={{borderBottom: "solid 1px #5c5a5a"}}>
                                    {renderCategories()}
                                </div>
                                {mainContent()}
                                <div className="d-flex justify-content-center mt-5">
                                    {end ? (
                                        <div style={{ color: "green"}} ><h5>Bạn đã xem hết</h5></div>
                                    ) : (
                                        <div style={{cursor: "pointer", color: "green"}} onClick={handleMoreClick}><h5>Xem thêm</h5></div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Sidebar />
            </div>
        </>
    );
};

export default Search;
