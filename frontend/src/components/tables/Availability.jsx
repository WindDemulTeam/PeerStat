import React from "react";
import classes from './Availability.module.css';

const Availability = () => {
  return (
    <>
      <div className={classes.intro}>
        <p>
          На СберПлатформе как только ты подписываешься на проект, то уже никак
          нельзя узнать, до какого момента его можно пересдавать.
        </p>
        <p>
          Мы собрали все условия по выполнению проектов в одну таблицу, чтобы
          можно было уверенно делать то или иное задание, зная, что оно не
          заблокирует возможность приняться за другой сабджект.
        </p>
      </div>
      <table className={classes.table}>
        <thead>
          <tr>
            <th>
              <span>Ветка</span>
            </th>
            <th>
              <span>Номер</span>
            </th>
            <th>
              <span>Название</span>
            </th>
            <th>
              <span>Доступ после</span>
            </th>
            <th>
              <span>Пересдача до закрытия</span>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td rowSpan={2}>Work Experience</td>
            <td>Internship</td>
            <td>Internship</td>
            <td>APE, Career track</td>
            <td>-</td>
          </tr>
          <tr className={classes.underlined}>
            <td>Career track</td>
            <td>Career track</td>
            <td>AP_1</td>
            <td>-</td>
          </tr>
          <tr>
            <td rowSpan={3}>Exams</td>
            <td>APE</td>
            <td>Core final exam</td>
            <td>DOExam, CPPExam, SQL3, AP_6</td>
            <td>Internship</td>
          </tr>
          <tr>
            <td>CPPExam</td>
            <td>CPPE</td>
            <td>CPP7</td>
            <td>Internship</td>
          </tr>
          <tr className={classes.underlined}>
            <td>DOExam</td>
            <td>DevOpsExam</td>
            <td>DO6</td>
            <td>-</td>
          </tr>
          <tr>
            <td rowSpan={6}>Applied Language</td>
            <td>AP_6</td>
            <td>RetailAnalitycs_v2.0 Web</td>
            <td>AP_5</td>
            <td>Internship</td>
          </tr>
          <tr>
            <td>AP_5</td>
            <td>SmartCalc_v4.0 Web</td>
            <td>AP_4</td>
            <td>AP_6</td>
          </tr>
          <tr>
            <td>AP_4</td>
            <td>Info21_v2.0_Web</td>
            <td>AP_2</td>
            <td>AP_5</td>
          </tr>
          <tr>
            <td>AP_3 (extra)</td>
            <td>SimpleWebStudio_v1.0</td>
            <td>AP_2</td>
            <td>AP_5</td>
          </tr>
          <tr>
            <td>AP_2</td>
            <td>SmartCalc_v3.0 Desktop</td>
            <td>DO6, SQL3, CPP7, A3, A6, A8</td>
            <td>AP_4</td>
          </tr>
          <tr className={classes.underlined}>
            <td>AP_1</td>
            <td>Applied Software BootCamp</td>
            <td>CPP4</td>
            <td>AP_2</td>
          </tr>
          <tr>
            <td rowSpan={8}>Algorithms</td>
            <td>A8</td>
            <td>Algorithmic trading</td>
            <td>A2</td>
            <td>AP_2</td>
          </tr>
          <tr>
            <td>A7 (extra)</td>
            <td>DNA Analyzer</td>
            <td>A2</td>
            <td>A3 || A6 || A8</td>
          </tr>
          <tr>
            <td>A6</td>
            <td>Transactions</td>
            <td>A2</td>
            <td>AP_2</td>
          </tr>
          <tr>
            <td>A5 (extra)</td>
            <td>s21_memory</td>
            <td>A2</td>
            <td>A3 || A6 || A8</td>
          </tr>
          <tr>
            <td>A4 (extra)</td>
            <td>Crypto</td>
            <td>A2</td>
            <td>A3 || A6 || A8</td>
          </tr>
          <tr>
            <td>A3</td>
            <td>Parallels</td>
            <td>A2</td>
            <td>AP_2</td>
          </tr>
          <tr>
            <td>A2</td>
            <td>SimpleNavigator_v1.0</td>
            <td>A1</td>
            <td>A3 || A4 || A7</td>
          </tr>
          <tr className={classes.underlined}>
            <td>A1</td>
            <td>Maze</td>
            <td>CPP4</td>
            <td>A3-A8</td>
          </tr>
          <tr>
            <td rowSpan={9}>C++</td>
            <td>CPP9 (extra)</td>
            <td>MonitoringSystem</td>
            <td>CPP4, DO3</td>
            <td>A3</td>
          </tr>
          <tr>
            <td>CPP8 (extra)</td>
            <td>PhotoLab_v1.0</td>
            <td>CPP4</td>
            <td>A3</td>
          </tr>
          <tr>
            <td>CPP7</td>
            <td>MLP</td>
            <td>CPP4</td>
            <td>AP_2</td>
          </tr>
          <tr>
            <td>CPP6 (extra)</td>
            <td>3DViewer_v2.2</td>
            <td>CPP4</td>
            <td>CPP9</td>
          </tr>
          <tr>
            <td>CPP5 (extra)</td>
            <td>3DViewer_v2.1</td>
            <td>CPP4</td>
            <td>CPP9</td>
          </tr>
          <tr>
            <td>CPP4</td>
            <td>3DViewer_v2.0</td>
            <td>CPP3</td>
            <td>CPP7</td>
          </tr>
          <tr>
            <td>CPP3</td>
            <td>SmartCalc_v2.0</td>
            <td>CPP2</td>
            <td>CPP5-CPP8</td>
          </tr>
          <tr>
            <td>CPP2</td>
            <td>s21_containers</td>
            <td>CPP1</td>
            <td>CPP4</td>
          </tr>
          <tr className={classes.underlined}>
            <td>CPP1</td>
            <td>s21_matrix+</td>
            <td>С8</td>
            <td>CPP3</td>
          </tr>
          <tr>
            <td rowSpan={3}>SQL</td>
            <td>SQL3</td>
            <td>RetailAnalitycs_v1.0</td>
            <td>SQL2</td>
            <td>AP_2</td>
          </tr>
          <tr>
            <td>SQL2</td>
            <td>Info21_v1.0</td>
            <td>SQL1 BootCamp</td>
            <td>AP_2</td>
          </tr>
          <tr className={classes.underlined}>
            <td>SQL1</td>
            <td>SQL BootCamp</td>
            <td>С8</td>
            <td>SQL2</td>
          </tr>
          <tr>
            <td rowSpan={6}>DevOps</td>
            <td>DO6</td>
            <td>CICD</td>
            <td>DO5</td>
            <td>DOExam</td>
          </tr>
          <tr>
            <td>DO5</td>
            <td>SimpleDocker</td>
            <td>DO3</td>
            <td>DOExam</td>
          </tr>
          <tr>
            <td>DO4 (extra)</td>
            <td>LinuxMonitoring_v2.0</td>
            <td>DO3</td>
            <td>DO6</td>
          </tr>
          <tr>
            <td>DO3</td>
            <td>LinuxMonitoring_v1.0</td>
            <td>DO2</td>
            <td>DO6</td>
          </tr>
          <tr>
            <td>DO2</td>
            <td>Linux Network</td>
            <td>DO1</td>
            <td>DO4 || DO5</td>
          </tr>
          <tr className={classes.underlined}>
            <td>DO1</td>
            <td>Linux</td>
            <td>C3</td>
            <td>DO3</td>
          </tr>
          <tr>
            <td rowSpan={8}>C</td>
            <td>C8</td>
            <td>3DViewer_v1.0</td>
            <td>C7</td>
            <td>CPP2</td>
          </tr>
          <tr>
            <td>C7</td>
            <td>SmartCalc_v1.0</td>
            <td>C2, C6</td>
            <td>CPP2</td>
          </tr>
          <tr>
            <td>C6</td>
            <td>s21_matrix</td>
            <td>C5</td>
            <td>CPP2</td>
          </tr>
          <tr>
            <td>C5</td>
            <td>s21_decimal</td>
            <td>C2</td>
            <td>CPP1</td>
          </tr>
          <tr>
            <td>C4 (extra)</td>
            <td>s21_math</td>
            <td>C2</td>
            <td>CPP1</td>
          </tr>
          <tr>
            <td>C3</td>
            <td>s21_string</td>
            <td>C2</td>
            <td>C7</td>
          </tr>
          <tr>
            <td>C2</td>
            <td>SimpleBashUtils</td>
            <td>C1</td>
            <td>C6</td>
          </tr>
          <tr>
            <td>C1</td>
            <td>C BootCamp</td>
            <td>-</td>
            <td>-</td>
          </tr>
          <tr>
            <td className={classes.made} colSpan={5}>
              made by @valeryje
            </td>
          </tr>
        </tbody>
      </table>
    </>
  );
};

export default Availability;
