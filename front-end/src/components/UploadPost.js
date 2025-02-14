import React, {useEffect, useState} from "react";
import { Editor } from "@tinymce/tinymce-react";
import Select from "react-select";
import axios from "axios";
import Navbar from "./Navbar";
import "./uploadPost.css"
import {data, useNavigate, useParams} from "react-router-dom";
import {fetchWrap} from "../fetchWrap";

const UploadPost = () => {
    const { postId } = useParams();
    const [title, setTitle] = useState("");
    const [categories, setCategories] = useState([]);
    const [content, setContent] = useState("");
    const [categoryOptions, setCategoryOptions] = useState([]);

    const navigate = useNavigate();
    useEffect(() => {
        const getCategories = async () => {
            try {
                const response = await fetch("http://localhost/api/categories");
                if (response.ok) {
                    const data = await response.json();
                    const transformedOptions = data.map(category => ({
                        value: category.id,
                        label: category.name
                    }));
                    setCategoryOptions(transformedOptions);
                }
            } catch (error) {
                console.error( error);
            }
        };
        getCategories();
    }, []);
    const [editorContent, setEditorContent] = useState("");
    const convertRelativeToAbsolute = (htmlContent) => {
        return htmlContent.replace(/src="\.\.\/api\/images/g, 'src="http://localhost/api/images');
    };
    useEffect(() => {
        const fetchPostDetails = async () => {
            if (!postId) return;

            try {
                const response = await fetchWrap("http://localhost/api/posts/" + postId + "/detail");
                if (response.ok) {
                    const data = await response.json();
                    setTitle(data.title);

                    const fullContent = convertRelativeToAbsolute(data.content);
                    setContent(fullContent);

                    setCategories(data.categories.map(cat => ({
                        value: cat.id,
                        label: cat.name
                    })));
                    console.log(data);
                    console.log(fullContent);
                    console.log()
                    console.log(categories);
                }
            } catch (error) {
                console.log( error);
            }
        };
        fetchPostDetails();
    }, [postId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (categories.length === 0) {
            alert("Vui lòng chọn ít nhất một thể loại.");
            return;
        }
        const postData = {
            title,
            categories: categories.map(cat => cat.label),
            content,
        };
        try {
            let response;
            if (postId) {
                response = await axios.put(`http://localhost/api/posts/${postId}`, postData);
                alert(postId ? "Bài viết đã được cập nhật thành công!" : "Bài viết đã được tạo thành công!");
                navigate("/posts/" + postId);
            } else {
                response = await axios.post("http://localhost/api/posts", postData);
                const id = response.data.postId;
                alert(postId ? "Bài viết đã được cập nhật thành công!" : "Bài viết đã được tạo thành công!");
                navigate("/posts/" + id);
            }



        } catch (error) {
            console.log( error);
            alert("Có lỗi xảy ra.");
        }
    };


    return (
        <>
            <Navbar />
            <div className="container mt-4">
                <h1 className="mb-4">{postId ? "Sủa bài viết " : "Tạo bài viết"}</h1>
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label htmlFor="title" className="form-label">Tiêu đề</label>
                        <input
                            type="text"
                            className="form-control"
                            id="title"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="Nhập tiêu đề..."
                        />
                    </div>

                    <div className="mb-3">
                        <label htmlFor="categories" className="form-label">Thể loại</label>
                        <Select className = "react-select__control"
                            isMulti
                            options={categoryOptions}
                            value={categories}
                            onChange={setCategories}
                            placeholder="Chọn thể loại..."
                        />
                    </div>

                    <div className="mb-3">
                        <label htmlFor="content" className="form-label">Nội dung</label>
                        <Editor
                            apiKey="e165yfho0uvtfkbdv6swajcvqjdweaixeq9swobtwphycw1f"
                            init={{
                                plugins: [
                                    'anchor', 'autolink', 'emoticons',
                                    'image', 'link', 'lists', 'media',
                                    'table',
                                ],
                                toolbar: 'undo redo | blocks fontsize | ' +
                                    'bold italic underline strikethrough | ' +
                                    'link image media table | ' +
                                    'align  | indent outdent | ' +
                                    'emoticons  | ',
                                images_upload_url: "http://localhost/api/posts/images",
                                images_upload_handler: async (blobInfo, success, failure) => {
                                    const formData = new FormData();
                                    formData.append("file", blobInfo.blob(), blobInfo.filename());
                                    try {
                                        const response = await fetch("http://localhost/api/posts/images", {
                                            method: "POST",
                                            body: formData,
                                        });

                                        if (!response.ok) {
                                            throw new Error("Failed to upload image");
                                        }
                                        const result = await response.json();
                                        console.log(result);
                                        if (result && result.location) {
                                            success(result.location);
                                             return result.location
                                        } else {
                                            console.error("Missing location in response:", result);
                                            failure("Invalid response: location is missing");
                                        }
                                    } catch (error) {
                                        console.error("Upload failed:", error);
                                        failure("Error: " + error.message);
                                    }
                                }
                            }}
                            value={content}
                            onEditorChange={(newContent) => setContent(newContent)}
                            className="tinymce-container"
                        />
                    </div>

                    <div className="text-end">
                        <button type="submit" className="btn btn-success">{postId ? "Sửa" : "Xuất bản"}</button>
                    </div>
                </form>
            </div>
        </>
    );
};

export default UploadPost;
