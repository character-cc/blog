import React from "react";
import Home from "./components/Home";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import UploadPost from "./components/UploadPost";
import CategoryModal from "./components/CategoryModal";
import PostDetail from "./components/PostDetail";
import Test from "./components/Test";
import Search from "./components/Search";
import YourStory from "./components/YourStory";
const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="upload_post" element={<UploadPost />} />
                <Route path="test" element={<Test />} />
                <Route path="/upload/post" element={<UploadPost />} />
                <Route path="/post/:id" element={<PostDetail />} />
                <Route path="category_modal" element={<CategoryModal />} />
                <Route path="/search" element={<Search />} />
                <Route path="/your-story" element={<YourStory />}></Route>
                <Route path="/posts/edit/:postId" element={<UploadPost />} />
            </Routes>
        </Router>
    );
};

export default App;

