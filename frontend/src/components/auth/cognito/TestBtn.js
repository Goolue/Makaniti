import React from "react"
import { Auth } from 'aws-amplify';



const TestBtn = () => {
    async function getUserInfo() {
        const user = await Auth.currentAuthenticatedUser();
        console.log('attributes:', user);
        return user
    }

    return (
        <button onClick={() => getUserInfo()} >test</button>
    )
}

export default TestBtn

