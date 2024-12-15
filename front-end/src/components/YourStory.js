
import {React, useEffect, useState} from "react";
import Post from "./Post";
import {Link} from "react-router-dom";
import {fetchWrap} from "../fetchWrap";
import parse from "html-react-parser";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";


const YourStory = () => {
    const [posts, setPosts] = useState([
        {id : 9 , author : "jdkjf" , likes : 5 , comments : 6 , content: "nskdn" , title: "Nskdn" ,imageUrl : ""}
    ]);

    useEffect(() => {
        const getData = async () => {
            try {
                const url = "http://localhost/api/me/story";
                const response = await fetchWrap(url);
                const result = await response.json();
                console.log("API Response:", result);
                setPosts(result);
            } catch (error) {
                console.log("Error fetching data:", error);
            }
        };
        getData();
    }, []);

    const handleMoreClick = () => {
        const getMore = async () => {
            try {
                const url = "";
                const response = await fetchWrap(url);
                if (response.ok){
                    const data = await response.json();
                    console.log(data);
                    setPosts((prevPosts) => [...prevPosts, ...data]);
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

    const handleDeleteClick = (postId) => {
        const deletePost = async () => {
            try {
                const isConfirmed  = window.confirm("Are you sure you want to delete this post?");
                if (isConfirmed) {
                    const url = "http://localhost/api/post/delete/" + postId;
                    const response = await fetchWrap(url);
                    if (response.ok){
                     setPosts((prevPosts) => {
                         return prevPosts.filter(post => post.id !== postId);
                     })
                    }
                    console.log("delte" + posts);
                }
            }
            catch (error) {
                console.log(error);
            }

        }
        deletePost();

    }
    return (<>
        <Navbar />
        <div className="container d-flex mt-5">

            <div className="w-75">
                <div className="container">
                    <div className="ms-5 mt-5">
                        <div className="mx-5">
                            <h3>Những bài viết của bạn</h3>
                            <div className="row p-1" style={{borderBottom: "solid 1px #5c5a5a"}}>
                            </div>
                            {posts.map((post) => (
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
                                                <Link to={`/post/${post.id}`}
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
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                         height="16"
                                                                         fill="currentColor" className="bi bi-star"
                                                                         viewBox="0 0 16 16">
                                                                        <path
                                                                            d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.56.56 0 0 0-.163-.505L1.71 6.745l4.052-.576a.53.53 0 0 0 .393-.288L8 2.223l1.847 3.658a.53.53 0 0 0 .393.288l4.052.575-2.906 2.77a.56.56 0 0 0-.163.506l.694 3.957-3.686-1.894a.5.5 0 0 0-.461 0z"/>
                                                                    </svg>
                                                                </i>
                                                            </div>
                                                            <div className="col-7">
                                                                <i>{post.comments}
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                         height="16"
                                                                         fill="currentColor" className="bi bi-chat"
                                                                         viewBox="0 0 16 16">
                                                                        <path
                                                                            d="M2.678 11.894a1 1 0 0 1 .287.801 11 11 0 0 1-.398 2c1.395-.323 2.247-.697 2.634-.893a1 1 0 0 1 .71-.074A8 8 0 0 0 8 14c3.996 0 7-2.807 7-6s-3.004-6-7-6-7 2.808-7 6c0 1.468.617 2.83 1.678 3.894m-.493 3.905a22 22 0 0 1-.713.129c-.2.032-.352-.176-.273-.362a10 10 0 0 0 .244-.637l.003-.01c.248-.72.45-1.548.524-2.319C.743 11.37 0 9.76 0 8c0-3.866 3.582-7 8-7s8 3.134 8 7-3.582 7-8 7a9 9 0 0 1-2.347-.306c-.52.263-1.639.742-3.468 1.105"/>
                                                                    </svg>
                                                                </i>
                                                            </div>
                                                            <div className="col-1">
                                                                <Link to={`/posts/edit/${post.id}`} style={{textDecoration: "none", color: "black"}}>
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16" fill="currentColor"
                                                                     className="bi bi-pen" viewBox="0 0 16 16">
                                                                    <path
                                                                        d="m13.498.795.149-.149a1.207 1.207 0 1 1 1.707 1.708l-.149.148a1.5 1.5 0 0 1-.059 2.059L4.854 14.854a.5.5 0 0 1-.233.131l-4 1a.5.5 0 0 1-.606-.606l1-4a.5.5 0 0 1 .131-.232l9.642-9.642a.5.5 0 0 0-.642.056L6.854 4.854a.5.5 0 1 1-.708-.708L9.44.854A1.5 1.5 0 0 1 11.5.796a1.5 1.5 0 0 1 1.998-.001m-.644.766a.5.5 0 0 0-.707 0L1.95 11.756l-.764 3.057 3.057-.764L14.44 3.854a.5.5 0 0 0 0-.708z"/>
                                                                </svg>
                                                                </Link>
                                                            </div>
                                                            <div className="col-2" style={{cursor: 'pointer'}}
                                                                 onClick={() => handleDeleteClick(post.id)}>
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16" fill="currentColor"
                                                                     className="bi bi-trash" viewBox="0 0 16 16">
                                                                <path
                                                                        d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z"/>
                                                                    <path
                                                                        d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z"/>
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
                            }
                            <div className="d-flex justify-content-center mt-5">
                                <div style={{cursor: "pointer", color: "green"}} onClick={handleMoreClick}><h5>Xem
                                    thêm</h5></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <Sidebar />
        </div>

</>);
            };

            export default YourStory;
