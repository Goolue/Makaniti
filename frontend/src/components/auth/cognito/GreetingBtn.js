import React from "react"
import { Auth } from 'aws-amplify';
import { Button } from 'react-bootstrap';

const GreetingBtn = () => {
    const [user, setUser] = React.useState();

    React.useEffect(() => {
        Auth.currentAuthenticatedUser()
            .then(user => {
                console.log("logged in user:")
                console.log(user)
                setUser(user)
            })
    }, []);

    function getToken(userInfo) {
        return userInfo.signInUserSession.idToken.jwtToken
    }

    const callApi = async () => {
        try {
            const token = getToken(user)

            fetch(
                `/api/v1/users/`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            ).then(response => {
                response.json().then(json => {
                    console.log(json)
                });
            });
        }
        catch (err) {
            console.log(`err:`)
            console.log(err)
        }
    };

    return (
        <Button onClick={() => callApi()} >greet</Button>
    )
}

export default GreetingBtn

