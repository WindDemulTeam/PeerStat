import React from "react";
import cl from "./Map.module.css";

const Map = () => {
  const handleSubmit = (e) => {
    console.log(e.target.defaultValue);
  };

  return (
    <div>
      <p>
        <iframe
          title="Map"
          src="https://www.google.com/maps/d/embed?mid=1N8kLjBsam6ILhmhvaT3tmKeYF8ArhO8&amp;ehbc=2E312F"
          width="95%"
          height="800"
        ></iframe>
      </p>
      <h2>Добавь себя на карту</h2>

      <div className={cl.form}>
        <form onSubmit={handleSubmit}>
          <div className={cl.form_container}>
            <div className={cl.form_block}>
              <p>
                <label>
                  {" "}
                  Имя и/или ник
                  <input type="text" name="name" defaultValue="" required />
                </label>
              </p>
              <p>Твой «родной» кампус</p>
              <p>
                <label>
                  <input
                    type="radio"
                    name="campus"
                    defaultValue="Москва"
                    defaultChecked={true}
                  />
                  Москва
                </label>
                <label>
                  <input type="radio" name="campus" defaultValue="Казань" />
                  Казань
                </label>
                <label>
                  <input
                    type="radio"
                    name="campus"
                    defaultValue="Новосибирск"
                  />
                  Новосибирск
                </label>
              </p>
              <br />
              <p>Платформа</p>
              <p>
                <label>
                  <input
                    type="radio"
                    name="platform"
                    defaultValue="Интра"
                    defaultChecked={true}
                  />
                  Интра
                </label>

                <label>
                  <input
                    type="radio"
                    name="platform"
                    defaultValue="Сберплатформа"
                  />
                  Сберплатформа
                </label>

                <label>
                  <input
                    type="radio"
                    name="platform"
                    defaultValue="Выпускник"
                  />
                  Выпускник
                </label>
                <label>
                  <input
                    type="radio"
                    name="platform"
                    defaultValue="deactivated"
                  />
                  deactivated
                </label>
              </p>
              <p>
                <label>
                  {" "}
                  Название города/страны или точные координаты
                  <input type="text" name="point" defaultValue="" required />
                </label>
              </p>
              <p>
                <label>
                  {" "}
                  Любые контакты, какие хочешь оставить: телега, слак, соцсети и
                  тд.
                  <input type="text" name="contacts" defaultValue="" required />
                </label>
              </p>
            </div>
            <div className={cl.form_block}>
              <p>
                <label>
                  {" "}
                  Комментарий или доп информация (необязательно)
                  <textarea name="comment" cols="40" rows="10"></textarea>
                </label>
              </p>
              <p>
                <button type="submit">Отправить</button>
              </p>
            </div>
          </div>
        </form>
      </div>

      <div className={cl.map_text}>
        <h2>Что это и зачем</h2>
        <p>
          Кампус школы всегда был и остается точкой притяжения, но сейчас многие
          пиры уезжают из России. С одной стороны здорово, что они теперь в
          безопасности, с другой - мы хотим не потеряться, сохранить наше
          комьюнити, даже если не можем больше видеться в кампусе.
        </p>
        <br />
        <p>
          Для этого мы запускаем проект «21 world» - на первом этапе это карта
          пиров.
        </p>
        <br />
        <p>
          Заполни форму - где ты и как можно связаться. Это поможет пирам в
          разных городах и странах найти друг друга, может чем-то помочь,
          скооперироваться или просто пообщаться со своими.
        </p>
        <br />
        <p>
          Отправь эту ссылку пиру, который уехал, по которому ты скучаешь, пусть
          заполнит и будет рядом.
        </p>
        <br />
        <p>
          Если надо что-то изменить в твоей точке, просто заполни форму заново.
        </p>
      </div>
    </div>
  );
};

export default Map;
