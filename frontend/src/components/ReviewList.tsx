import React, { useEffect, useState } from "react";
import {OrderInfo, OrderState, UserBasicInfo, UserInfo, UserType} from "../services/definations";
import userService from "../services/user.service";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import UserCard from "./UserCard";
import {Divider} from "@mui/material";
import ListItem from "@mui/material/ListItem";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import Avatar from "@mui/material/Avatar";
import ListItemText from "@mui/material/ListItemText";
import _ from "lodash";
import Skeleton from "@mui/material/Skeleton";

//待审核列表
const ReviewList: React.FC<{ selectModel?: boolean }> = (props) => {
    const [reviewrList, setReviewList] = useState<Array<OrderInfo>>();
    useEffect(() => {
        //Todo

    }, []);

    const renderPlaceholder = () => (
        <ListItem alignItems="flex-start">
            <ListItemText primary={<Skeleton />} secondary={<Skeleton />} />
        </ListItem>
    );

    if (reviewrList == null) {
        return (
            renderPlaceholder()
        );
    } else if (reviewrList.length === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有待审核的订单
            </Typography>
        );
    } else {
        const list = _.flatten(
            _.zip(reviewrList!, _.fill(Array(reviewrList!.length - 1), undefined))
        );
        return (
            <>
                {list.map((order: OrderInfo | undefined, index: number) => {
                    return order === undefined ? (
                        <Divider variant="inset" component="li" key={index} />
                    ) : (
                        <ListItem alignItems="flex-start" key={index}>
                            <ListItemText
                                primary={order.id.toString()}
                                secondary={order.question}
                            />
                        </ListItem>
                    );
                })}
            </>

        );
    }
};

export default ReviewList;
