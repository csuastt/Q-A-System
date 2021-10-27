import React, { useEffect, useState } from "react";
import {OrderInfo} from "../services/definations";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import _ from "lodash";
import {Button, Divider, Grid, Typography} from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
//待审核列表
const ReviewList: React.FC<{ selectModel?: boolean }> = (props) => {
    const [reviewrList, setReviewList] = useState<Array<OrderInfo>>();
    useEffect(() => {
        //Todo
    }, []);

    const renderPlaceholder = () => (
        <ListItem alignItems="flex-start">
            <Grid container spacing={1} justifyContent={"flex-end"}>
                <Grid item xs={8}>
                    <ListItemText
                        primary={"2333"}
                        secondary={"软件工程怎么学？"}
                    />
                </Grid>
                <Grid item xs={3}>
                    <Button
                        size="small"
                        color="success"
                        variant="contained"
                        component={RouterLink}
                        to="/manager"
                    >
                        通过
                    </Button>

                    <Button
                        size="small"
                        color="error"
                        variant="contained"
                        component={RouterLink}
                        to="/manager"
                    >
                        驳回
                    </Button>

                    <Button
                        size="small"
                        color="info"
                        variant="contained"
                        component={RouterLink}
                        to="/manager"
                    >
                        详情
                    </Button>
                </Grid>
            </Grid>
        </ListItem>
    );

    if (reviewrList == null) {
        return renderPlaceholder();
    } else if (reviewrList.length === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有待审核的订单
            </Typography>
        );
    } else {
        const list = _.flatten(
            _.zip(
                reviewrList!,
                _.fill(Array(reviewrList!.length - 1), undefined)
            )
        );
        return (
            <>
                {list.map((order: OrderInfo | undefined, index: number) => {
                    return order === undefined ? (
                        <Divider variant="inset" component="li" key={index} />
                    ) : (
                        <ListItem alignItems="flex-start">
                            <Grid
                                container
                                spacing={1}
                                justifyContent={"flex-end"}
                            >
                                <Grid item xs={8}>
                                    <ListItemText
                                        primary={order.id.toString()}
                                        secondary={order.question}
                                    />
                                </Grid>
                                <Grid item xs={3}>
                                    <Button
                                        size="small"
                                        color="success"
                                        variant="contained"
                                        component={RouterLink}
                                        to="/manager"
                                    >
                                        通过
                                    </Button>

                                    <Button
                                        size="small"
                                        color="error"
                                        variant="contained"
                                        component={RouterLink}
                                        to="/manager"
                                    >
                                        驳回
                                    </Button>

                                    <Button
                                        size="small"
                                        color="info"
                                        variant="contained"
                                        component={RouterLink}
                                        to="/manager"
                                    >
                                        详情
                                    </Button>
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
