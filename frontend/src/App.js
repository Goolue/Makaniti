import React from 'react';
import { Route, BrowserRouter as Router, Switch } from 'react-router-dom';
import Amplify from 'aws-amplify';
import Authenticator from './components/auth/cognito/Authenticator'
import { AuthState, onAuthUIStateChange } from '@aws-amplify/ui-components';
import awsconfig from './aws-exports';
import UserItemsGrid from './components/userItems/UserItemsGrid'
import Navigation from './components/navigation/Navigation'
import 'bootstrap/dist/css/bootstrap.min.css';

Amplify.configure(awsconfig);

const AuthStateApp = () => {
    const [authState, setAuthState] = React.useState();

    React.useEffect(() => {
        return onAuthUIStateChange((nextAuthState, authData) => {
            setAuthState(nextAuthState);
        });
    }, []);

    const isSignedIn = () => {
        const signedIn = authState === AuthState.SignedIn
        console.log(`is user signed in? ${signedIn}`)
        return signedIn
    }

    const loggedIn = () => {
        console.log("logged in")
        return (
            <div className="App" >
                <Router>
                    <Navigation />
                    <Switch>
                        <Route exact path='/' component={UserItemsGrid} />
                    </Switch>
                </Router>
            </div >
        )
    }

    const notLoggedIn = () => {
        console.log("not logged in")
        return (
            <Authenticator />
        )
    }

    return isSignedIn() ? loggedIn() : notLoggedIn();
}

export default AuthStateApp;