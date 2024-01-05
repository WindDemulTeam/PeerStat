import "./styles/App.css";
import { BrowserRouter } from "react-router-dom";
import React from "react";
import Header from "./components/header/Header";
import Rotuter from "./components/router/Rotuter";
import Footer from "./components/footer";

function App() {
  return (
    <BrowserRouter>
      <div className="wrapper">
        <Header />
        <Rotuter />
        <Footer />
      </div>
    </BrowserRouter>
  );
}

export default App;
