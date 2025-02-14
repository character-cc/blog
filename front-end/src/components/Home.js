import React, {useEffect , useState} from "react";
import Navbar from "./Navbar";
import MainContent from "./MainContent";
import Sidebar from "./Sidebar";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import CategoryModal from "./CategoryModal";
import useCategoryModal from "./useCategoryModal";
import {useNavigate} from "react-router-dom";


const Home = () => {
    const { showModal, closeModal } = useCategoryModal();
    const navigate = useNavigate();
    useEffect(() => {
        var url = localStorage.getItem("prevUrl");
        localStorage.removeItem("prevUrl");
        if(url){
            navigate(url);
        }
        }
    )
    return (
        <div>
                <>
                    <Navbar />
                    <div className="container d-flex mt-5">
                        <MainContent />
                        <Sidebar />
                    </div>
                </>
        </div>
    );
};

export default Home;