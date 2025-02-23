import {Link, useLocation, useParams} from "react-router-dom";
import {useEffect, useState , } from "react";
import  'moment/locale/vi';
import moment from 'moment';
import Navbar from "./Navbar";
import Comment from "./Comment";
import {fetchWrap} from "../fetchWrap";
import parse from "html-react-parser";

const PostDetail = () => {
    const [post, setPost] = useState({});
    const [loading, setLoading] = useState(true);
    const { id } = useParams();
    const [liked , setLiked] = useState(false);
    const [totalLikes, setTotalLikes] = useState(0);
    const location = useLocation();
    useEffect(() => {
        const getPost = async () => {
            try {
                const response = await fetchWrap("http://localhost/api/posts/" +id + "/detail" );
                if(response.ok){
                    const data = await response.json();
                    console.log(data);
                    setPost(data);
                    setTotalLikes(data.totalLikes);
                    setLiked(data.likedByCurrentUser);
                    setLoading(false)
                }
            }
            catch(err){
                console.log(err);
            }
        }
        getPost();
    }, []);

    const handleLikeClick = (e) => {
        const handleLike = async () => {
            const response = await fetchWrap("http://localhost/api/posts/" + post.id +"/likes" ,{
                method: "POST"
            })
            if (response.ok) {
                console.log("Thành công like bài");
                liked ? setTotalLikes(totalLikes - 1) : setTotalLikes(totalLikes + 1);
                setLiked(!liked);
            }
        }
        handleLike();

    }


        return (
        loading ? <> </> :
            <>
                <Navbar />
                <div className="mt-5">
                    <div style={{margin: "auto", width: 850}}>
                        <div className="row">
                            <div className="col-12">
                                <h1> {post.title} </h1>
                            </div>
                        </div>
                        <div className="row mt-5">
                            <div className="col-1">
                                <img src={post.author.avatar} width="60" className="img-fluid rounded-circle" height="60"/>
                            </div>
                            <div className="col">
                                <div className="row">
                                    <div className="col">
                                        {post.author.name}
                                        {/*<Link className="ms-2" style={{textDecoration: "none"}}>*/}
                                        {/*    Follow*/}
                                        {/*</Link>*/}
                                    </div>
                                </div>
                                <div className="row mt-2">
                                    <div className="col">
                                        {moment(post.createdAt).fromNow()}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="row mt-5 p-3" style={{
                            borderTop: "1px solid rgb(53 50 50)",
                            borderBottom: "1px solid rgb(53 50 50)"
                        }}>
                            <div className="col-1">
                                <i onClick={handleLikeClick} style={{cursor : "pointer"}}>{totalLikes}
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                         fill={liked ? "green" : "currentColor"} className="bi bi-star" viewBox="0 0 16 16">
                                        <path
                                            d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.56.56 0 0 0-.163-.505L1.71 6.745l4.052-.576a.53.53 0 0 0 .393-.288L8 2.223l1.847 3.658a.53.53 0 0 0 .393.288l4.052.575-2.906 2.77a.56.56 0 0 0-.163.506l.694 3.957-3.686-1.894a.5.5 0 0 0-.461 0z"/>
                                    </svg>
                                </i>
                            </div>
                            <div className="col-10">
                                {/*<i>{post.comments.length}*/}
                                {/*    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"*/}
                                {/*         fill="currentColor" className="bi bi-chat" viewBox="0 0 16 16">*/}
                                {/*        <path*/}
                                {/*            d="M2.678 11.894a1 1 0 0 1 .287.801 11 11 0 0 1-.398 2c1.395-.323 2.247-.697 2.634-.893a1 1 0 0 1 .71-.074A8 8 0 0 0 8 14c3.996 0 7-2.807 7-6s-3.004-6-7-6-7 2.808-7 6c0 1.468.617 2.83 1.678 3.894m-.493 3.905a22 22 0 0 1-.713.129c-.2.032-.352-.176-.273-.362a10 10 0 0 0 .244-.637l.003-.01c.248-.72.45-1.548.524-2.319C.743 11.37 0 9.76 0 8c0-3.866 3.582-7 8-7s8 3.134 8 7-3.582 7-8 7a9 9 0 0 1-2.347-.306c-.52.263-1.639.742-3.468 1.105"/>*/}
                                {/*    </svg>*/}
                                {/*</i>*/}
                            </div>
                            <div className="col-1">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                     fill="currentColor" className="bi bi-bookmarks" viewBox="0 0 16 16">
                                    <path
                                        d="M2 4a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v11.5a.5.5 0 0 1-.777.416L7 13.101l-4.223 2.815A.5.5 0 0 1 2 15.5zm2-1a1 1 0 0 0-1 1v10.566l3.723-2.482a.5.5 0 0 1 .554 0L11 14.566V4a1 1 0 0 0-1-1z"/>
                                    <path
                                        d="M4.268 1H12a1 1 0 0 1 1 1v11.768l.223.148A.5.5 0 0 0 14 13.5V2a2 2 0 0 0-2-2H6a2 2 0 0 0-1.732 1"/>
                                </svg>
                            </div>
                        </div>
                        <div className="mt-5">
                            <div className="row pt-2">
                                <div className="col">{parse(post.content)}</div>
                            </div>
                        </div>
                        <div className="mt-5">
                            <div className="mt-5">
                                <div className="row pt-2 d-flex justify-content-start">
                                    <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
                                        {post.categories.map((category) => (
                                            <div key={category.id} className="rounded-pill text-center bg-white p-2">
                                                {category.name}
                                            </div>
                                        ))}
                                    </div>
                                </div>

                            </div>
                        </div>

                        <Comment postId={post.id} />
                    </div>
                </div>
            </>
    )
};

export default PostDetail;