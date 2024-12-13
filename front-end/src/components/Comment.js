import moment from "moment/moment";
import {useEffect, useState} from "react";
import {fetchWrap} from "../fetchWrap";
import {useLocation} from "react-router-dom";

const Comment = ({cmts , postId}) => {
    const [comment, setComment] = useState("");
    const [comments, setComments] = useState(cmts);
    const location = useLocation();

    const handleInputChange = (e) => {
        setComment(e.target.value);
        console.log(comment);
    };

    useEffect(() => {
        console.log(postId);
        console.log(comments);
    },[]);

    const handleLikeClick = (id) => {
        const handleLike = async () => {
            const response = await fetchWrap("http://localhost/api/like_comment", "http://localhost" + location.pathname + location.search ,{
                method: "POST",
                body: JSON.stringify({
                    commentId: id,
                })
            })
            if (!response.ok) {
                console.log("Thành công like bài");
            }
        }
        handleLike();
        setComments((prevComments) =>
            prevComments.map((comment) =>
                comment.id === id
                    ? {
                        ...comment,
                        likedByCurrentUser: !comment.likedByCurrentUser,
                        totalLikes: comment.likedByCurrentUser
                            ? comment.totalLikes - 1
                            : comment.totalLikes + 1
                    }
                    : comment
            )
        );
    };

    const uploadComment = async () => {
        if (!comment.trim()) {
            alert("Bình luận không được để trống!");
            return;
        }
        try {
            const response = await fetch("http://localhost/api/comment/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    postId: postId,
                    content: comment,
                }),
            });
            if (response.ok) {
                const result = await response.json();
                console.log("Bình luận đã được tải lên:", result);
                setComment("");
                setComments((prevComment)=> [result,...prevComment]);

            }
        } catch (err) {
            console.error("Error:", err);
        }
    };

    return <>
        <div className="mt-5">
            <div className="row pt-2">
                <div className="col"><h3>Bình Luận</h3></div>
            </div>
            <div className="row mt-5">
                <div className="input-group input-group-lg">
                    <input type="text" className="form-control" aria-label="Sizing example input"
                           value={comment} onChange={handleInputChange} placeholder="Viết bình luận"
                           aria-describedby="inputGroup-sizing-lg"/>
                </div>
            </div>
            <div className="row mt-2 d-flex justify-content-end pe-4">
                <div className="col-1">
                    <button className="btn btn-primary" type="submit" onClick={uploadComment}>Gửi</button>
                </div>

            </div>
            <div className="row mt-2">
                {comments.map((comment) => {
                    return (<div className="row pt-5">
                        <div className="col-1">
                            <img src={comment.user.avatar} width="60" className="img-fluid rounded-circle"
                                 height="60"/>
                        </div>
                        <div className="col">
                            <div className="row">
                                <div className="col">
                                    {comment.user.username}
                                </div>
                            </div>
                            <div className="row mt-2">
                                <div className="col">
                                    {moment(comment.createdAt).fromNow()}
                                </div>
                            </div>
                        </div>
                        <div className="row mt-2">
                            <div className="col">
                                {comment.content}
                            </div>
                        </div>
                        <div className="row mt-2 p-3" style={{
                            borderBottom: "1px solid rgb(53 50 50)"
                        }}>
                            <div className="col-1">
                                <i key={comment.id} onClick={() => handleLikeClick(comment.id)}>{comment.totalLikes}
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                         fill={comment.likedByCurrentUser ? "green" : "currentColor"} className="bi bi-star" viewBox="0 0 16 16">
                                        <path
                                            d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.56.56 0 0 0-.163-.505L1.71 6.745l4.052-.576a.53.53 0 0 0 .393-.288L8 2.223l1.847 3.658a.53.53 0 0 0 .393.288l4.052.575-2.906 2.77a.56.56 0 0 0-.163.506l.694 3.957-3.686-1.894a.5.5 0 0 0-.461 0z"/>
                                    </svg>
                                </i>
                            </div>
                        </div>
                    </div>)
                })}

            </div>
        </div>
    </>
}

export default Comment;