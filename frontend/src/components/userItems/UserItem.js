import React from "react"
import GridListTile from '@material-ui/core/GridListTile';
import GridListTileBar from '@material-ui/core/GridListTileBar';

const UserItem = ({ itemInfo }) => {
    console.log(itemInfo)
    return (
        <GridListTile key={itemInfo.index} cols={itemInfo.cols}>
            <img src={itemInfo.img} alt="this is the alt" />
            <GridListTileBar
                title={`Hello ${itemInfo.user.first_name} ${itemInfo.user.last_name}`}
                subtitle={<span>by: subtitle</span>}
            />
        </GridListTile>
    )
}

export default UserItem