import React from "react";
import FollowedPost from "./FollowedPost";

const Sidebar = () => {
    return (
        <div className="w-25" style={{ borderLeft: "solid 1px #5c5a5a" }}>
            <div className="row p-3">
                <div className="col">
                    <i>Bài viết của những người bạn đã theo dõi</i>
                </div>
            </div>
            <FollowedPost />
        </div>
    );
};

export default Sidebar;
