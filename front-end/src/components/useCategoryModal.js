import { useState, useEffect } from "react";
import { useLocation ,  useNavigate  } from "react-router-dom";

const useCategoryModal = () => {

    const [showModal, setShowModal] = useState(false);
    console.log("Show Modal:", showModal);
    const location = useLocation();

    const navigate = useNavigate();
    const getQueryParams = (search) => {
        return new URLSearchParams(search);
    };

    useEffect(() => {
        const queryParams = getQueryParams(location.search);
        if (queryParams.get("showCategoryModal") === "true") {
            setShowModal(true);
        }
    }, [location.search]);
    const closeModal = () => {
        setShowModal(false);
        navigate(location.pathname, { replace: true });
    };
    return { showModal, closeModal };
}
export default useCategoryModal;