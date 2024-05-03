import React, { useState, useMemo } from "react";
import Api from "../../Api/Api";
import ReactTable from "./ReactTable";
import { DropdownFilter, TextSearchFilter } from "../../utils/filters";
import Loader from "../loader/Loader";
import useFetching from "../hooks/useFetching";

const Project = () => {
  const [projectsList, setProjectsList] = useState([]);
  const [courseList, setCourseList] = useState([]);
  const [userList, setUserList] = useState([]);

  const [projectId, setProjectId] = useState(0);
  const [courseId, setCourseId] = useState(0);

  const { loading: isProjectListLoading } = useFetching(async () => {
    const response = await Api.getProjectList();
    setProjectsList(response.data);
  }, []);

  const { loading: isCourseLoading } = useFetching(async () => {
    if (courseId > 0) {
      const response = await Api.getCourseList(courseId);
      setCourseList(response.data);
    }
  }, [courseId]);

  const { loading: isUsertListLoading } = useFetching(async () => {
    if (courseId === 0) {
      const response = await Api.getProjectUsers(projectId);
      setUserList(response.data);
    } else {
      setUserList([]);
    }
  }, [projectId, courseId]);

  const currentProject = useMemo(() => {
    if (courseList.length > 0 && projectId > 0) {
      return courseList.find((item) => item.projectId === projectId);
    }
    if (projectsList.length > 0 && projectId > 0) {
      return projectsList.find((item) => item.projectId === projectId);
    }
    return undefined;
  }, [projectId, projectsList, courseList]);

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
        loading={
          isProjectListLoading === true ||
          isUsertListLoading === true ||
          isCourseLoading === true
        }
      />

      {projectsList.length !== 0 && (
        <select
          defaultValue={"default"}
          onChange={(val) => {
            const project = projectsList[val.target.value];
            const courseId = Number(project.courseId);
            const projectId = Number(project.projectId);
            setCourseList([]);
            setProjectId(projectId);
            setCourseId(courseId);
          }}
        >
          <option hidden disabled value="default">
            Выбери проект
          </option>
          {projectsList.map((project, index) => (
            <option key={project.projectId} value={index}>
              {project.projectName}
            </option>
          ))}
        </select>
      )}

      {courseList.length !== 0 && (
        <select
          defaultValue={"default"}
          onChange={(val) => {
            setProjectId(Number(val.target.value));
            setCourseId(0);
          }}
        >
          <option hidden disabled value="default">
            Выбери проект
          </option>
          {courseList.map((course) => (
            <option key={course.projectId} value={course.projectId}>
              {course.projectName}
            </option>
          ))}
        </select>
      )}

      {currentProject !== undefined && (
        <div>
          <h2>
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
      )}

      {userList.length !== 0 && (
        <div>
          <ReactTable columns={columns} data={userList} />
        </div>
      )}
    </div>
  );
};

export default Project;
