import axios from "axios";

export default class Api {
  static async getStat(campusId) {
    const response = await axios.get("/api/stat", {
      params: {
        campus: campusId,
      },
    });
    return response;
  }

  static async getCampusList() {
    const response = await axios.get("/api/campus");
    return response;
  }

  static async getProjectList() {
    const response = await axios.get("/api/projectList");
    return response;
  }

  static async getProjectUsers(projectId) {
    const response = await axios.get("/api/project", {
      params: {
        id: projectId,
      },
    });
    return response;
  }
}
