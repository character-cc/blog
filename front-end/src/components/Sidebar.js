import React, {useEffect} from "react";
import {useState} from "react";
import FollowedPost from "./FollowedPost";
import {useLocation} from "react-router-dom";
import {fetchWrap} from "../fetchWrap";
import {Link} from "react-router-dom";
const Sidebar = () => {

    const [unFollowUser , setUnFollowUser] = useState([
        {id : 1 , avatar: "" , username : "Test" , follow : false},
        {id : 2 , avatar: "" , username : "Test2" , follow : false},
        {id : 3 , avatar: "" , username : "Test3" , follow : false},
        ]);
    const [postsFollowingUser, setPostsFollowingUser] = useState([])
    const location = useLocation();
    const frontEndUrl = "http://localhost" + location.pathname + location.search;
    useEffect(() => {
        const getUser = async () => {
           const response = await fetchWrap("http://localhost/api/unfollow_user" , frontEndUrl);
            if(response.ok){
                const data = await response.json();
                setUnFollowUser(data);
            }
        }
        const getPosts = async () => {
            const response = await fetchWrap("http://localhost/api/posts/following_user" , frontEndUrl)
            if(response.ok){
                const data = await response.json();
                console.log(data);
                if(data){
                    setPostsFollowingUser(data);
                }

            }
        }
        getPosts();
        getUser();
    },[])

    const handleClickButton = (id) =>{
        const followingUser = async () => {
            const response = await fetchWrap("http://localhost/api/follow/"+id , frontEndUrl);
            if(response.ok){
                setUnFollowUser((prevUnFollowUser) => {
                    return prevUnFollowUser.map(user => {
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
    return (
        <div className="w-25" style={{borderLeft: "solid 1px #5c5a5a"}}>
            <div className="row p-3">
                <div className="col">
                    <i>Bài viết của những người bạn đã theo dõi</i>
                </div>
            </div>
            {postsFollowingUser.map(post => {
                return (<div className="mt-5 ps-3">
                    <div className="row">
                        <div className="col-1">
                            <img src={post.avatar} alt="" width={20} height={20} className="rounded-circle" />
                        </div>
                        <div className="col-8">
                            <i>{post.username}</i>
                        </div>
                    </div>
                    <div className="row mt-2">
                        <Link to={`/post/${post.id}`} style={{textDecoration : "none" , color: "black"}} ><h5 style={{cursor : "pointer"}}>{post.title}</h5></Link>
                    </div>
                    <div className="row">
                        <i>{post.totalLikes} <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                                   className="bi bi-star" viewBox="0 0 16 16">
                            <path
                                d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.56.56 0 0 0-.163-.505L1.71 6.745l4.052-.576a.53.53 0 0 0 .393-.288L8 2.223l1.847 3.658a.53.53 0 0 0 .393.288l4.052.575-2.906 2.77a.56.56 0 0 0-.163.506l.694 3.957-3.686-1.894a.5.5 0 0 0-.461 0z"/>
                        </svg>
                        </i>
                    </div>
                </div>)
            })}
            <div className="row mt-5 p-3">
                <div className="col"><h5>Có thể bạn biết</h5></div>
            </div>
            {unFollowUser.map(user => (
                <>
                    <div className="row mt-2 p-3">
                        <div className="col-2">
                            <img className="img-fluid rounded-circle" src={user.avatar} width="60" height="60"/>
                        </div>
                        <div className="col-8">
                            {user.username}
                        </div>
                        <div className="col-2">
                            <button className="rounded-2 btn btn-primary btn-sm btn-block"
                                    onClick={() => handleClickButton(user.id)}>
                                {user.follow ? "UnFollow" : "Follow"}
                            </button>
                            </div>
                        </div>
                    </>
                )
            )}
        </div>
    );
};

export default Sidebar;
