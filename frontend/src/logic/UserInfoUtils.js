import { Auth } from 'aws-amplify';

export const getUserInfo = async () => {
    const userInfo = await Auth.currentAuthenticatedUser();
    return userInfo.attributes
}

export const convertCognitoUserToUiUser = (cognitoUser) => {
    return Object.assign({}, cognitoUser,
        {
            first_name: cognitoUser['custom:first_name'],
            last_name: cognitoUser['custom:last_name']
        })
}

export const getUserItems = user => {
    return [...Array(15).keys()]
}