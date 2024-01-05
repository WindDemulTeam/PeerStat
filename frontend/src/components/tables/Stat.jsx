import React, { useEffect, useState } from "react";
import Api from "../../Api/Api";
import ReactTable from "./ReactTable";
import { DropdownFilter, TextSearchFilter } from "../../utils/filters";
import { useFetching } from "../hooks/useFetching";
import Loader from "../loader/Loader";

const Stat = () => {
  const [campusList, setcampusList] = useState({});
  const [userList, setUserList] = useState([]);
  const [campusId, setCampusId] = useState(0);

  const [fetchCampusList, isCampusListLoading] = useFetching(async () => {
    const response = await Api.getCampusList();
    setcampusList(response.data);
  });

  const [fetchUsertList, isUsertListLoading] = useFetching(
    async (campusId) => {
      const response = await Api.getStat(campusId);
      setUserList(response.data);
    }
  );

  useEffect(() => {
    fetchCampusList();
  }, []);

  useEffect(() => {
    fetchUsertList(campusId);
  }, [campusId]);

  const columns = [
    {
      Header: "Логин",
      accessor: "login",
      Filter: TextSearchFilter,
      style: {
        width: "10%",
      },
    },
    {
      Header: "Коалиция",
      accessor: "coalition",
      Filter: DropdownFilter,
      style: {
        width: "8%",
      },
    },
    {
      Header: "Волна",
      accessor: "wave",
      Filter: DropdownFilter,
      style: {
        width: "10%",
      },
    },
    {
      Header: "Класс",
      accessor: "platformClass",
      Filter: DropdownFilter,
      style: {
        width: "10%",
      },
    },
    {
      Header: "Бассейн",
      accessor: "bootcamp",
      Filter: DropdownFilter,
      style: {
        width: "10%",
      },
    },
    {
      Header: "Уровень",
      accessor: "level",
      disableFilters: true,
      style: {
        width: "10%",
      },
    },
    {
      Header: "XP",
      accessor: "xp",
      disableFilters: true,
      style: {
        width: "5%",
      },
    },
    {
      Header: "PRP",
      accessor: "peerPoints",
      disableFilters: true,
      style: {
        width: "7%",
      },
    },
    {
      Header: "CRP",
      accessor: "codeReviewPoints",
      disableFilters: true,
      style: {
        width: "7%",
      },
    },
    {
      Header: "Coins",
      accessor: "coins",
      disableFilters: true,
      style: {
        width: "5%",
      },
    },
    {
      Header: "XP за 3 мес",
      accessor: "diff3",
      disableFilters: true,
      style: {
        width: "5%",
      },
    },
    {
      Header: "Текущие проекты",
      accessor: "currentProject",
      Filter: TextSearchFilter,
      style: {
        width: "13%",
      },
    },
  ];

  return (
    <div>
      <Loader
        loading={isCampusListLoading === true || isUsertListLoading === true}
      />

      {Object.keys(campusList).length > 0 && (
        <select
          defaultValue={"default"}
          onChange={(val) => setCampusId(val.target.value)}
        >
          <option hidden disabled value="default">
            Выбери кампус
          </option>
          {Object.keys(campusList).map((key) => (
            <option key={key} value={key}>
              {campusList[key]}
            </option>
          ))}
        </select>
      )}

      {userList.length !== 0 && (
        <ReactTable columns={columns} data={userList} />
      )}
    </div>
  );
};

export default Stat;
