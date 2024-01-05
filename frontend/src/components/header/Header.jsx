import React from "react";
import logo from "../../img/logo.svg";
import { NavLink } from "react-router-dom";
import classes from "./Header.module.css";

const Header = () => {
  return (
    <div className={classes.header}>
      <div className={classes.logo}>
        <NavLink to="/map">
          {" "}
          <img src={logo} alt="21 school" />
        </NavLink>
      </div>
      <nav className={classes.menu}>
        <ul>
          <li>
            <NavLink
              to="/map"
              className={({ isActive }) => (isActive ? classes.active : null)}
            >
              Карта пиров
            </NavLink>
          </li>
          <li>
            <NavLink
              to="/stat"
              className={({ isActive }) => (isActive ? classes.active : null)}
            >
              Статистика
            </NavLink>
          </li>
          <li>
            <NavLink
              to="/project"
              className={({ isActive }) => (isActive ? classes.active : null)}
            >
              Проекты
            </NavLink>
          </li>
          <li>
            <NavLink
              to="/availability"
              className={({ isActive }) => (isActive ? classes.active : null)}
            >
              Доступность проектов
            </NavLink>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default Header;
