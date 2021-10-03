import React from "react";
import { render, screen } from "@testing-library/react";
import App from "./App";

test("renders learn react link", () => {
    render(<App />);
    const linkElement = screen.getByText(/欢迎来到付费问答系统/i);
    expect(linkElement).toBeInTheDocument();
});
