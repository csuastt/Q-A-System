import { useTheme } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import LoginIcon from "@mui/icons-material/Login";
import CardActionArea from "@mui/material/CardActionArea";
import { Link as RouterLink } from "react-router-dom";
import React, { useContext } from "react";
import SvgIcon from "@mui/material/SvgIcon/SvgIcon";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import AccountCircle from "@mui/icons-material/AccountCircle";
import AuthContext from "../AuthContext";
import QuestionAnswerIcon from "@mui/icons-material/QuestionAnswer";
import AddCommentIcon from "@mui/icons-material/AddComment";
import RateReviewIcon from "@mui/icons-material/RateReview";
import EqualizerIcon from "@mui/icons-material/Equalizer";
import SchoolIcon from "@mui/icons-material/School";
import { UserRole } from "../services/definations";
import HomeIcon from '@mui/icons-material/Home';
import {Grid} from "@mui/material";
import IncomeStatistics from "./IncomeStatistics";
import Avatar from "@mui/material/Avatar";
import Divider from "@mui/material/Divider";
import CardActions from "@mui/material/CardActions";
import Button from "@mui/material/Button";

export default function Welcome() {
    const theme = useTheme();
    const { user } = useContext(AuthContext);

    const ButtonCardWrapper: React.FC<{
        to: string;
        Icon: typeof SvgIcon;
        title1: string;
        title2: string;
    }> = (props) => (
        <Card
            sx={{
                display: "flex",
                margin: theme.spacing(2),
            }}
        >
            <CardActionArea component={RouterLink} to={props.to}>
                <CardContent>
                    <Box sx={{ display: "flex", flexDirection: "row" }}>
                        <props.Icon
                            sx={{
                                margin: theme.spacing(2, 2, 2, 0),
                            }}
                        />
                        <Box sx={{ flexDirection: "column" }}>
                            <Typography component="div" variant="h5">
                                {props.title1}
                            </Typography>
                            <Typography
                                variant="subtitle1"
                                color="text.secondary"
                                component="div"
                            >
                                {props.title2}
                            </Typography>
                        </Box>
                    </Box>
                </CardContent>
            </CardActionArea>
        </Card>
    );

    const drawAboutMe = () => {
      return (
          <Card>
              <CardContent>
                  <Box
                      sx={{
                          alignItems: "center",
                          display: "flex",
                          flexDirection: "column",
                      }}
                  >
                      <Avatar
                          src={user ? user.avatar : ""}
                          sx={{
                              height: 100,
                              width: 100,
                          }}
                      />
                      <Box mt={2}>
                          <Typography
                              color="textPrimary"
                              gutterBottom
                              variant="h4"
                          >
                              {user ? (user.nickname === ""
                                  ? "匿名用户"
                                  : user.nickname) :
                                  ("尚未登录")
                              }
                          </Typography>
                      </Box>
                      <Box mx={2}>
                          <Typography
                              color="textSecondary"
                              variant="body1"
                          >
                              {"你还不是回答者，快去申请吧~"}
                          </Typography>
                      </Box>
                  </Box>
              </CardContent>
          </Card>
      );
    }



    return (
        <>
            <Typography
                align="left"
                variant="h6"
                sx={{
                    margin: theme.spacing(1, 2, 1, 0),
                    fontWeight: 600
                }}
            >
                Hi，是求知让我们相聚于此
            </Typography>
            <Box>
                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    flexWrap: 'wrap',
                }}>
                    <HomeIcon color={"primary"}/>
                    <span style={{marginLeft: 5}}>/ 主页</span>
                </div>
            </Box>
            <Grid
                container
                spacing={4}

            >
                <Grid item md={8} xs={12} mt={2}>
                    <IncomeStatistics briefMsg={true} user={user}/>
                </Grid>
                <Grid item md={4} xs={12} mt={2}>
                    {drawAboutMe()}
                </Grid>
            </Grid>
            {/*<ButtonCardWrapper*/}
            {/*    to="/answerers"*/}
            {/*    Icon={SchoolIcon}*/}
            {/*    title1="回答者列表"*/}
            {/*    title2="寻找合适的回答者"*/}
            {/*/>*/}
            {/*{user ? (*/}
            {/*    <>*/}
            {/*        <ButtonCardWrapper*/}
            {/*            to="/order/create"*/}
            {/*            Icon={AddCommentIcon}*/}
            {/*            title1="提出问题"*/}
            {/*            title2="获取知识与答案"*/}
            {/*        />*/}
            {/*        <ButtonCardWrapper*/}
            {/*            to="/orders"*/}
            {/*            Icon={QuestionAnswerIcon}*/}
            {/*            title1="我的提问"*/}
            {/*            title2="查看历史提问与订单状态"*/}
            {/*        />*/}
            {/*        {user.role === UserRole.ANSWERER ? (*/}
            {/*            <>*/}
            {/*                <ButtonCardWrapper*/}
            {/*                    to="/orders?answerer=true"*/}
            {/*                    Icon={RateReviewIcon}*/}
            {/*                    title1="我的回答"*/}
            {/*                    title2="查看历史回答与订单状态"*/}
            {/*                />*/}
            {/*                <ButtonCardWrapper*/}
            {/*                    to="/income"*/}
            {/*                    Icon={EqualizerIcon}*/}
            {/*                    title1="收入统计"*/}
            {/*                    title2="查看个人月度收入情况"*/}
            {/*                />*/}
            {/*            </>*/}
            {/*        ) : (*/}
            {/*            <></>*/}
            {/*        )}*/}
            {/*        <ButtonCardWrapper*/}
            {/*            to="/profile"*/}
            {/*            Icon={AccountCircle}*/}
            {/*            title1="个人信息"*/}
            {/*            title2="查看、修改个人信息"*/}
            {/*        />*/}
            {/*    </>*/}
            {/*) : (*/}
            {/*    <>*/}
            {/*        <ButtonCardWrapper*/}
            {/*            to="/login"*/}
            {/*            Icon={LoginIcon}*/}
            {/*            title1="登录"*/}
            {/*            title2="提出问题、回答问题"*/}
            {/*        />*/}
            {/*        <ButtonCardWrapper*/}
            {/*            to="/register"*/}
            {/*            Icon={PersonAddIcon}*/}
            {/*            title1="注册"*/}
            {/*            title2="从现在开始解答您的疑惑"*/}
            {/*        />*/}
            {/*    </>*/}
            {/*)}*/}
        </>
    );
}
