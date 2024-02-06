import axios from "axios";

export default class Api {
  static async getStat(campusId) {
    const response = await axios.get(process.env.REACT_APP_API_ADDR + "/stat", {
      params: {
        campus: campusId,
      },
    });
    return response;
  }

  static async getCampusList() {
    const response = await axios.get(
      process.env.REACT_APP_API_ADDR + "/campus"
    );
    return response;
  }

  static async getProjectList() {
    const response = await axios.get(
      process.env.REACT_APP_API_ADDR + "/projectList"
    );
    return response;
  }

  static async getCourseList(courseId) {
    const response = await axios.get(
      process.env.REACT_APP_API_ADDR + "/courseList",
      {
        params: {
          id: courseId,
        },
      }
    );
    return response;
  }

  static async getProjectUsers(projectId) {
    const response = await axios.get(
      process.env.REACT_APP_API_ADDR + "/project",
      {
        params: {
          id: projectId,
        },
      }
    );
    return response;
  }
}
