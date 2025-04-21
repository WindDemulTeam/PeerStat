import React from "react";
import DgisMap from "./Dgismap";
import cl from "./Map.module.css";

const Map = () => {

  return (
    <div>
      <span className={cl.telegramIcon}>📢</span>
      <h1>Найди своих однокурсников в Telegram!</h1>
      <p><h2>Связь между школьным логином и Telegram-аккаунтом</h2></p>
      <a href="tg://resolve?domain=login_school21_bot" class="cta-button"><span className={cl.botName}>@login_school21_bot</span></a>
      <DgisMap />
    </div>
  );
};

export default Map;
