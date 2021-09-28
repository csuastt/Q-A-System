import React, {useEffect, useState} from "react";
import {UserListItem} from "../services/userDefination";
import userListService from "../services/userList.service"
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Avatar from '@mui/material/Avatar';
import Skeleton from "@mui/material/Skeleton";
import _ from "lodash";
import {Divider} from "@mui/material";

const UserList: React.FC<{ type: string }> = props => {
    const [userList, setUserList] = useState<Array<UserListItem>>();
    useEffect(() => {
        userListService.get(props.type).then(list => setUserList(list));
    }, [props.type]);

    const renderPlaceholder = () => (
        <ListItem alignItems="flex-start">
            <ListItemAvatar>
                <Skeleton variant="circular">
                    <Avatar/>
                </Skeleton>
            </ListItemAvatar>
            <ListItemText
                primary={<Skeleton/>}
                secondary={<Skeleton/>}
            />
        </ListItem>
    );

    const renderUserList = () => {
        const list = _.flatten(_.zip(userList!, _.fill(Array(userList!.length - 1), undefined)));
        return (<>
            {list.map((user: UserListItem | undefined, index: number) => {
                return user === undefined ? (<Divider variant="inset" component="li" key={index}/>) : (
                    <ListItem alignItems="flex-start" key={index}>
                        <ListItemAvatar>
                            <Avatar alt={user.name} src={user.avatarUrl}/>
                        </ListItemAvatar>
                        <ListItemText
                            primary={user.name}
                            secondary={user.introduction}
                        />
                    </ListItem>)
            })}
        </>)
    }

    return (
        <List>
            {userList == null ? renderPlaceholder() : renderUserList()}
        </List>
    );
}

export default UserList;