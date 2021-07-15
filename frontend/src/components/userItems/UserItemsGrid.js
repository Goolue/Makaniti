import React from "react"

import GridList from '@material-ui/core/GridList';
import GridListTile from '@material-ui/core/GridListTile';
import ListSubheader from '@material-ui/core/ListSubheader';
import withWidth, { isWidthUp } from '@material-ui/core/withWidth';
import CircularProgress from '@material-ui/core/CircularProgress';

import UserItem from './UserItem'

import { getUserInfo, convertCognitoUserToUiUser, getUserItems } from '../../logic/UserInfoUtils'

const UserItemsGrid = props => {
    const [user, setUser] = React.useState();

    React.useEffect(() => {
        getUserInfo().then(userInfo => { setUser(userInfo); })
    }, []);

    const getGridItems = (items) => {
        const createItem = i => {
            return (
                <UserItem itemInfo={{
                    index: i,
                    img: "https:thumbs.dreamstime.com/b/hipo-20043744.jpg",
                    user: convertCognitoUserToUiUser(user),
                    cols: 1
                }} />
            )
        }

        return items.map(i => createItem(i))
    }

    const getGridListCols = () => {
        let cols = 1;
        if (isWidthUp('xl', props.width)) {
            cols = 6
        }

        if (isWidthUp('lg', props.width)) {
            cols = 5
        }

        if (isWidthUp('md', props.width)) {
            cols = 4
        }

        console.log(`cols: ${cols}`)
        return cols;
    }

    return (
        user ?
            (
                <GridList cellHeight={180} cols={getGridListCols()} spacing={10}>
                    <GridListTile key="Subheader" cols={getGridListCols()} style={{ height: 'auto' }}>
                        <ListSubheader component="div">December</ListSubheader>
                    </GridListTile>
                    {getGridItems(getUserItems(user))}
                </GridList>
            )
            : (
                <CircularProgress />
            )
    )
}

export default withWidth()(UserItemsGrid)