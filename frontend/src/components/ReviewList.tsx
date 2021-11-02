import React, { useEffect, useState } from "react";
import { OrderInfo, OrderState, UserRole } from "../services/definations";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import _ from "lodash";
import { Button, Divider, Grid, Typography } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
import orderService from "../services/orderService";
//待审核列表
const ReviewList: React.FC<{ selectModel?: boolean }> = (props) => {
    const [reviewList, setReviewList] = useState<Array<OrderInfo>>();
    // useEffect(() => {
    //     orderService.getOrderListByAdmin(OrderState.PAYED).then((list) => {
    //         setReviewList(list);
    //     });
    // }, []);

    const renderPlaceholder = () => (
        <ListItem alignItems="flex-start">
            <Grid container justifyContent={"flex-end"}>
                <Grid item xs={8}>
                    <ListItemText
                        primary={"2333"}
                        secondary={"软件工程怎么学？根本学不会怎么办"}
                    />
                </Grid>
                <Grid item xs={4}>
                    <ListItem alignItems="center">
                        <Grid item xs={4}>
                            <Button
                                size="small"
                                color="success"
                                variant="outlined"
                                component={RouterLink}
                                to="/manager"
                            >
                                通过
                            </Button>
                        </Grid>

                        <Grid item xs={4}>
                            <Button
                                size="small"
                                color="error"
                                variant="outlined"
                                component={RouterLink}
                                to="/manager"
                            >
                                驳回
                            </Button>
                        </Grid>
                        <Grid item xs={4}>
                            <Button
                                size="small"
                                color="info"
                                variant="outlined"
                                component={RouterLink}
                                to="/manager"
                            >
                                详情
                            </Button>
                        </Grid>
                    </ListItem>
                </Grid>
            </Grid>
        </ListItem>
    );

    if (reviewList == null) {
        return renderPlaceholder();
    } else if (reviewList.length === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有待审核的订单
            </Typography>
        );
    } else {
        const list = _.flatten(
            _.zip(reviewList!, _.fill(Array(reviewList!.length - 1), undefined))
        );
        return (
            <>
                {list.map((order: OrderInfo | undefined, index: number) => {
                    return order === undefined ? (
                        <Divider variant="inset" component="li" key={index} />
                    ) : (
                        <ListItem alignItems="flex-start">
                            <Grid container justifyContent={"flex-end"}>
                                <Grid item xs={8}>
                                    <ListItemText
                                        primary={order.id.toString()}
                                        secondary={order.question.substr(0, 25)}
                                    />
                                </Grid>
                                <Grid item xs={4}>
                                    <ListItem alignItems="center">
                                        <Grid item xs={4}>
                                            <Button
                                                size="small"
                                                color="success"
                                                variant="outlined"
                                                component={RouterLink}
                                                to="/manager"
                                            >
                                                通过
                                            </Button>
                                        </Grid>

                                        <Grid item xs={4}>
                                            <Button
                                                size="small"
                                                color="error"
                                                variant="outlined"
                                                component={RouterLink}
                                                to="/manager"
                                            >
                                                驳回
                                            </Button>
                                        </Grid>
                                        <Grid item xs={4}>
                                            <Button
                                                size="small"
                                                color="info"
                                                variant="outlined"
                                                component={RouterLink}
                                                to="/manager"
                                            >
                                                详情
                                            </Button>
                                        </Grid>
                                    </ListItem>
                                </Grid>
                            </Grid>
                        </ListItem>
                    );
                })}
            </>
        );
    }
};

export default ReviewList;
