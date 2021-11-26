import { BrowserRouter, Route, Switch } from "react-router-dom";
import AuthContext from "./AuthContext";
import AppFrame from "./components/AppFrame";
import {
    Container,
    createTheme,
    PaletteMode,
    ThemeProvider,
} from "@mui/material";
import Welcome from "./components/Welcome";
import OrderCreationWizard from "./components/OrderCreationWizard";
import AccountProfile from "./components/AccountProfile";
import Login from "./components/Login";
import Register from "./components/Register";
import AnswererList from "./components/AnswererList";
import Logout from "./components/Logout";
import React, { useEffect, useState } from "react";
import ChangePassword from "./components/ChangePassword";
import { UserInfo, UserRole } from "./services/definations";
import authService from "./services/authService";
import OrderDetail from "./components/OrderDetail";
import PathParamParser from "./PathParamParser";
import UserOrderList from "./components/UserOrderList";
import IncomeStatistics from "./components/IncomeStatistics";
import Help from "./components/Help";
import { SnackbarProvider } from "notistack";
import NotificationController from "./components/NotificationController";
import websocketService from "./services/websocketService";
import NotificationList from "./components/NotificationList";
import { grey } from "@mui/material/colors";
import { SimplePaletteColorOptions } from "@mui/material/styles/createPalette";
import Library from "./components/Library";
import UserPurchasedList from "./components/UserPurchasedList";
import useMediaQuery from "@mui/material/useMediaQuery";
import { Helmet } from "react-helmet";

export const ColorModeContext = React.createContext({
    toggleColorMode: () => {},
});

const getDesignTokens = (mode: PaletteMode) => ({
    palette: {
        mode,
        ...(mode === "light"
            ? {
                  secondary: {
                      main: "#DC368D",
                      light: "#E666B2",
                      dark: "#BB2B69",
                  } as SimplePaletteColorOptions,
              }
            : {
                  // palette values for dark mode
                  primary: {
                      main: "#EA4F1C",
                      light: "#F29373",
                      dark: "#BA3C12",
                  } as SimplePaletteColorOptions,
                  secondary: {
                      main: "#03DAC6",
                      light: "#A6F2E7",
                      dark: "#02B19F",
                  } as SimplePaletteColorOptions,
                  background: {
                      default: "#2f3033",
                      paper: "#313336",
                  },
                  error: {
                      main: "#cf6679",
                      light: "#E19EAA",
                      dark: "#9C3043",
                  },
                  warning: {
                      main: "#d06b0b",
                      light: "#F6A456",
                      dark: "#C2640A",
                  },
                  text: {
                      primary: "#D9D9D9",
                      secondary: grey[500],
                  },
              }),
    },
});

export default function App() {
    const [user, setUser] = useState<UserInfo>();
    const [refreshing, setRefreshing] = useState(true);
    const [wsAvailable, setWsAvailable] = useState(false);

    const [mode, setMode] = React.useState<PaletteMode>("light");
    const colorMode = React.useMemo(
        () => ({
            // The dark mode switch would invoke this method
            toggleColorMode: () => {
                setMode((prevMode: PaletteMode) =>
                    prevMode === "light" ? "dark" : "light"
                );
            },
        }),
        []
    );

    // Update the theme only if the mode changes
    const theme = React.useMemo(
        () => createTheme(getDesignTokens(mode)),
        [mode]
    );
    const renderPWAMeta = () => (
        <Helmet>
            <meta name="theme-color" content={theme.palette.primary.main} />
            <meta
                name="background-color"
                content={theme.palette.primary.main}
            />
        </Helmet>
    );

    // if match the mobile size
    const matches = useMediaQuery(theme.breakpoints.up("md"));

    useEffect(() => {
        websocketService.onConnected = () => {
            console.log("WebSocket connection established");
            setWsAvailable(true);
        };
        websocketService.onDisconnected = () => {
            console.log("WebSocket is disconnected");
            setWsAvailable(false);
        };
        authService
            .refreshToken()
            .then(setUser)
            .then(() => websocketService.tryActivate())
            .catch(() => authService.clearToken())
            .finally(() => setRefreshing(false));
    }, []);

    const routes = [
        [
            "/answerers/select",
            <AnswererList selectModel userRole={UserRole.ANSWERER} />,
        ],
        [
            "/answerers",
            <AnswererList
                userRole={UserRole.ANSWERER}
                selectModel={typeof user !== "undefined"}
            />,
        ],
        [
            "/orders/:orderId",
            <PathParamParser
                params={[["orderId", "number"]]}
                C={OrderDetail}
            />,
        ],
        ["/orders", <UserOrderList />],
        ["/purchased", <UserPurchasedList />],
        ["/order/create/:answerer", <OrderCreationWizard />],
        ["/order/create", <OrderCreationWizard />],
        [
            "/profile",
            <AccountProfile isAdmin={false} matches={matches} theme={theme} />,
        ],
        ["/login", <Login redirect={"/"} isAdmin={false} matches={matches} />],
        ["/logout", <Logout redirect={"/"} isAdmin={false} />],
        ["/register", <Register matches={matches} />],
        [
            "/change_password",
            <ChangePassword
                redirectConfirm={"/logout"}
                redirectCancel={"/profile"}
                isAdmin={false}
                matches={matches}
            />,
        ],
        ["/notif", <NotificationList />],
        [
            "/income",
            <IncomeStatistics
                userId={user?.id}
                briefMsg={false}
                isAdmin={false}
            />,
        ],
        ["/help", <Help />],
        ["/lib", <Library />],
        ["/", <Welcome />],
    ];

    return refreshing ? (
        <></>
    ) : (
        <>
            {renderPWAMeta()}
            <AuthContext.Provider
                value={{
                    user: user,
                    setUser: setUser,
                    clearUser: () => setUser(undefined),
                    manager: undefined,
                    setManager: () => {},
                    clearManager: () => {},
                }}
            >
                <ColorModeContext.Provider value={colorMode}>
                    <ThemeProvider theme={theme}>
                        <BrowserRouter>
                            <SnackbarProvider maxSnack={4}>
                                <NotificationController
                                    wsAvailable={wsAvailable}
                                >
                                    <AppFrame isAdmin={false}>
                                        <Container
                                            maxWidth="md"
                                            sx={matches ? {} : { px: 0 }}
                                        >
                                            <Switch>
                                                {routes.map((routeItem) => {
                                                    return (
                                                        <Route
                                                            path={routeItem[0].toString()}
                                                            key={routeItem[0].toString()}
                                                        >
                                                            {routeItem[1]}
                                                        </Route>
                                                    );
                                                })}
                                            </Switch>
                                        </Container>
                                    </AppFrame>
                                </NotificationController>
                            </SnackbarProvider>
                        </BrowserRouter>
                    </ThemeProvider>
                </ColorModeContext.Provider>
            </AuthContext.Provider>
        </>
    );
}
