import {React ,useState} from "react";
import Post from "./Post";


const MainContent = () => {
    const [selectedCategory, setSelectedCategory] = useState("Cho bạn");
    const categories = ["Cho bạn" , "Jpa" , "Java"];
    const handleClickCategory = (category) => {
        setSelectedCategory(category);
    }
    const renderCategories = () => {
        return categories.map((category) => (
            (
                <div className="col-2" key={category} onClick={() => handleClickCategory(category)} style={{
                    color: category === selectedCategory ? "green" : "black",
                    fontWeight: category === selectedCategory ? "bold" : "normal",
                    cursor: "pointer",
                }}>{category}</div>
            )
        )
        )
    }
    return (
        <div className="w-75">
            <div className="container">
                <div className="ms-5">
                    <div className="mx-5">
                    <div className="row p-3" style={{ borderBottom: "solid 1px #5c5a5a" }}>
                        {renderCategories()}
                        </div>
                        <Post />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MainContent;
