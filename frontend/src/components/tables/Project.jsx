import React, { useEffect, useState, useMemo } from "react";
import Api from "../../Api/Api";
import ReactTable from "./ReactTable";
import { DropdownFilter, TextSearchFilter } from "../../utils/filters";
import { useFetching } from "../hooks/useFetching";
import Loader from "../loader/Loader";

const Project = () => {
  const [projectsList, setProjectsList] = useState([]);
  const [userList, setUserList] = useState([]);
  const [projectId, setProjectId] = useState(0);

  const [fetchProjectList, isProjectListLoading] = useFetching(async () => {
    const response = await Api.getProjectList();
    setProjectsList(response.data);
  });

  const [fetchUsertList, isUsertListLoading] = useFetching(
    async (projectId) => {
      const response = await Api.getProjectUsers(projectId);
      setUserList(response.data);
    }
  );

  useEffect(() => {
    fetchProjectList();
  }, []);

  useEffect(() => {
    fetchUsertList(projectId);
  }, [projectId]);

  const currentProject = useMemo(() => {
    if (projectsList.length > 0 && projectId > 0) {
      return projectsList.find((item) => item.projectId === projectId);
    }
    return null;
  }, [projectId, projectsList]);

  const columns = [
    {
      Header: "Логин",
      accessor: "login",
      Filter: TextSearchFilter,
    },
    {
      Header: "Кампус",
      accessor: "campus",
      Filter: DropdownFilter,
    },
    {
      Header: "Коалиция",
      accessor: "coalition",
      Filter: DropdownFilter,
    },
    {
      Header: "Волна",
      accessor: "wave",
      Filter: DropdownFilter,
    },
    {
      Header: "Класс",
      accessor: "platformClass",
      Filter: DropdownFilter,
    },
    {
      Header: "Уровень",
      accessor: "level",
      Filter: DropdownFilter,
    },
    {
      Header: "XP пира",
      accessor: "xp",
      disableFilters: true,
    },
    {
      Header: "Результат проекта",
      accessor: "score",
      disableFilters: true,
    },
    {
      Header: "Статус",
      accessor: "state",
      Filter: DropdownFilter,
    },
    {
      Header: "Место",
      accessor: "location",
      Filter: DropdownFilter,
    },
  ];

  return (
    <div>
      <Loader
        loading={isProjectListLoading === true || isUsertListLoading === true}
      />

      {projectsList.length !== 0 && (
        <select
          defaultValue={"default"}
          onChange={(val) => setProjectId(Number(val.target.value))}
        >
          <option hidden disabled value="default">
            Выбери проект
          </option>
          {projectsList.map((project) => (
            <option key={project.projectId} value={project.projectId}>
              {project.nodeCode + "_" + project.projectName}
            </option>
          ))}
        </select>
      )}

      {userList.length !== 0 && (
        <div>
          <div>
            <h2>
              <span>{currentProject.nodeCode}</span>
              <span>{currentProject.projectName}</span>
            </h2>
            <p>
              <span>{currentProject.mandatory}</span>,
              <span>{currentProject.type}</span>.
              <span>{currentProject.points}</span> points, duration{" "}
              <span>{currentProject.duration}</span>
            </p>
            <p>{currentProject.projectDescription}</p>
          </div>

          <ReactTable columns={columns} data={userList} />
        </div>
      )}
    </div>
  );
};

export default Project;
