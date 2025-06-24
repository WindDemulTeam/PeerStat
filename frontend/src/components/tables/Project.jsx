import React, { useState, useMemo, useRef, useEffect } from "react";
import Api from "../../Api/Api";
import ReactTable from "./ReactTable";
import { DropdownFilter, TextSearchFilter } from "../../utils/filters";
import Loader from "../loader/Loader";
import classes from "./Project.module.css";

const Project = () => {
  const [projectsList, setProjectsList] = useState([]);
  const [courseList, setCourseList] = useState([]);
  const [userList, setUserList] = useState([]);
  const [searchInput, setSearchInput] = useState("");
  const [selectedProject, setSelectedProject] = useState(null);
  const [selectedCourse, setSelectedCourse] = useState(null);
  const [isProjectDropdownOpen, setIsProjectDropdownOpen] = useState(false);
  const [isCourseDropdownOpen, setIsCourseDropdownOpen] = useState(false);
  const [isLoading, setIsLoading] = useState({
    projects: true,
    courses: false,
    users: false,
  });

  const projectDropdownRef = useRef(null);
  const courseDropdownRef = useRef(null);

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        setIsLoading((prev) => ({ ...prev, projects: true }));
        const response = await Api.getProjectList();
        setProjectsList(response.data);
      } finally {
        setIsLoading((prev) => ({ ...prev, projects: false }));
      }
    };

    fetchProjects();
  }, []);

  useEffect(() => {
    const fetchCourses = async () => {
      if (selectedProject?.courseId) {
        try {
          setIsLoading((prev) => ({ ...prev, courses: true }));
          const response = await Api.getCourseList(selectedProject.courseId);
          setCourseList(response.data);
        } finally {
          setIsLoading((prev) => ({ ...prev, courses: false }));
        }
      }
    };

    fetchCourses();
  }, [selectedProject]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setIsLoading((prev) => ({ ...prev, users: true }));

        if (selectedCourse) {
          const response = await Api.getProjectUsers(selectedCourse.projectId);
          setUserList(response.data);
        } else if (selectedProject && !selectedProject.courseId) {
          const response = await Api.getProjectUsers(selectedProject.projectId);
          setUserList(response.data);
        } else {
          setUserList([]);
        }
      } finally {
        setIsLoading((prev) => ({ ...prev, users: false }));
      }
    };

    fetchUsers();
  }, [selectedProject, selectedCourse]);

  const filteredProjects = useMemo(() => {
    if (!searchInput) return projectsList;
    return projectsList.filter((project) =>
      project.projectName.toLowerCase().includes(searchInput.toLowerCase())
    );
  }, [projectsList, searchInput]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        projectDropdownRef.current &&
        !projectDropdownRef.current.contains(event.target)
      ) {
        setIsProjectDropdownOpen(false);
      }
      if (
        courseDropdownRef.current &&
        !courseDropdownRef.current.contains(event.target)
      ) {
        setIsCourseDropdownOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleSelectProject = (project) => {
    setSelectedProject(project);
    setSelectedCourse(null);
    setSearchInput("");
    setIsProjectDropdownOpen(false);
  };

  const handleSelectCourse = (course) => {
    setSelectedCourse(course);
    setIsCourseDropdownOpen(false);
  };

  const columns = [
    { Header: "Логин", accessor: "login", Filter: TextSearchFilter },
    { Header: "Кампус", accessor: "campus", Filter: DropdownFilter },
    { Header: "Коалиция", accessor: "coalition", Filter: DropdownFilter },
    { Header: "Класс", accessor: "platformClass", Filter: DropdownFilter },
    { Header: "Уровень", accessor: "level", Filter: DropdownFilter },
    { Header: "XP пира", accessor: "xp", disableFilters: true },
    { Header: "Результат проекта", accessor: "score", disableFilters: true },
    { Header: "Статус", accessor: "state", Filter: DropdownFilter },
    { Header: "Место", accessor: "location", Filter: DropdownFilter },
  ];

  const displayProject = selectedCourse || selectedProject;

  return (
    <>
      <Loader
        loading={isLoading.projects || isLoading.courses || isLoading.users}
      />

      <div ref={projectDropdownRef} className={classes.projectSelector}>
        <input
          type="text"
          placeholder="Поиск проекта..."
          value={searchInput}
          onChange={(e) => {
            setSearchInput(e.target.value);
            setIsProjectDropdownOpen(true);
          }}
          onFocus={() => setIsProjectDropdownOpen(true)}
          className={classes.projectSearchInput}
        />

        {isProjectDropdownOpen && filteredProjects.length > 0 && (
          <div className={classes.projectDropdown}>
            {filteredProjects.map((project) => (
              <div
                key={project.projectId}
                onClick={() => handleSelectProject(project)}
                className={classes.projectDropdownItem}
              >
                {project.projectName}
              </div>
            ))}
          </div>
        )}
      </div>

      {selectedProject?.courseId && courseList.length > 0 ? (
        <div ref={courseDropdownRef} className={classes.courseSelector}>
          <div
            className={classes.courseSelected}
            onClick={() => setIsCourseDropdownOpen(!isCourseDropdownOpen)}
          >
            {selectedCourse ? selectedCourse.projectName : "Выберите курс"}
          </div>

          {isCourseDropdownOpen && (
            <div className={classes.courseDropdown}>
              {courseList.map((course) => (
                <div
                  key={course.projectId}
                  onClick={() => handleSelectCourse(course)}
                  className={classes.courseDropdownItem}
                >
                  {course.projectName}
                </div>
              ))}
            </div>
          )}
        </div>
      ) : null}

      {((selectedProject && !selectedProject?.courseId) || selectedCourse) && displayProject && (
        <div className={classes.projectInfo}>
          <h2>{displayProject.projectName}</h2>
          <p>
            {displayProject.type}.
            {displayProject.points} points, duration {displayProject.duration}
          </p>
          <p>{displayProject.projectDescription}</p>
        </div>
      )}

      {userList.length > 0 && (
        <div className={classes.projectUsers}>
          <ReactTable columns={columns} data={userList} />
        </div>
      )}
    </>
  );
};

export default Project;
