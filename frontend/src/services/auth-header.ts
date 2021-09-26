export default function authHeader() {
    const raw_user: string | null = localStorage.getItem('user');
    let user;
    if (raw_user){
        user = JSON.parse(raw_user);
    }else {
        user = null;
    }

    if (user && user.accessToken) {
        return { Authorization: 'Bearer ' + user.accessToken }; // for Spring Boot back-end
        // return { 'x-access-token': user.accessToken };       // for Node.js Express back-end
    } else {
        return {};
    }
}
