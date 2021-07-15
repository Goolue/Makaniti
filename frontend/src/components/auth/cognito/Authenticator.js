import React from 'react';
import { AmplifyAuthenticator, AmplifySignIn, AmplifySignUp } from '@aws-amplify/ui-react';

const Authenticator = () => {
    // const [authState, setAuthState] = React.useState();

    // React.useEffect(() => {
    //     return onAuthUIStateChange((nextAuthState, authData) => {
    //         setAuthState(nextAuthState);
    //     });
    // }, []);

    // const isUserSignedIn = () => {
    //     const signedIn = authState === AuthState.SignedIn
    //     console.log(`is user signed in? ${signedIn}`)
    //     return signedIn
    // }


    return (
        <AmplifyAuthenticator >
            <AmplifySignUp
                slot="sign-up"
                formFields={[
                    {
                        type: "username",
                        label: "User Name",
                        placeholder: "Enter your desired user name please",
                        inputProps: { required: true },
                    },
                    {
                        type: "password",
                        label: "Password",
                        placeholder: "Enter your password please",
                        inputProps: { required: true },
                    },
                    {
                        type: "email",
                        label: "Email",
                        placeholder: "Enter your email please",
                        inputProps: { required: true },
                    },
                    {
                        type: "custom:first_name",
                        label: "First Name",
                        placeholder: "Enter your first name please",
                        inputProps: { required: true },
                    },
                    {
                        type: "custom:last_name",
                        label: "Last Name",
                        placeholder: "Enter your last name please",
                        inputProps: { required: true },
                    },
                    {
                        type: "phone_number",
                        label: "Phone Number",
                        placeholder: "Enter your phone number please",
                        inputProps: { required: false },
                    },
                ]}
            />
            <AmplifySignIn slot="sign-in" />
        </AmplifyAuthenticator>
    )
}

export default Authenticator