import { useEffect, useState } from "react";
import { fetchWrap } from "./fetchWrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

function LoginRedirect() {
    const [data, setData] = useState(null);

    useEffect(() => {
        const getData = async () => {
            try {
                const result = await fetchWrap("./api/home");
                console.log("API Response:", result);
                setData(result);
            } catch (error) {
                console.log("Error fetching data:", error);
            }
        };

        getData();
    }, []);
    return (
        <div>
            {data ? (
                <>
                    <h1></h1>
                    <p></p>
                </>
            ) : (
                <p>Loading...</p>
            )}
        </div>
    );
}

export default LoginRedirect;
