import React, {useEffect , useState} from "react";
import Navbar from "./Navbar";
import MainContent from "./MainContent";
import Sidebar from "./Sidebar";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import CategoryModal from "./CategoryModal";
import useCategoryModal from "./useCategoryModal";


const Home = () => {
    const { showModal, closeModal } = useCategoryModal();
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