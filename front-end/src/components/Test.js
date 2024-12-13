import React, { useState, useEffect } from "react";

// Component chính
const CommentList = () => {

    useEffect(() => {
        const data = async () => {
            try{
                const response = await fetch("http://localhost/api/categories");
                if(response.ok){
                    console.log(response);
                }
            }
            catch(e){
                console.log(e);
            }
        }
        data();
    },[]);
    return (
        <>
            jkask
        </>
    );
};

export default CommentList;
