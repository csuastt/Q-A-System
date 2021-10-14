export default function authToken() {
    const storedToken: string | null = localStorage.getItem("token");
    return storedToken ? { Authorization: `Bearer ${storedToken}` } : {};
}
