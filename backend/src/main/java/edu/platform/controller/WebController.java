package edu.platform.controller;

import edu.platform.modelView.ProjectUserView;
import edu.platform.modelView.ProjectView;
import edu.platform.modelView.StatUserView;
import edu.platform.service.ProjectService;
import edu.platform.service.UserProjectService;
import edu.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class WebController {
    private UserService userService;
    private UserProjectService userProjectService;
    private ProjectService projectService;

    @GetMapping("/campus")
    public Map<String, String> getCampuses() {
        return userService.getCampuses();
    }

    @GetMapping("/stat")
    public List<StatUserView> getStatPage1(@RequestParam(defaultValue = "") String campus) {
        if (campus != null && !campus.isEmpty()) {
            return userService.findUsersByCampusName(campus);
        }
        return null;
    }

    @GetMapping("/project")
    public List<ProjectUserView> getProjectsInfo1(@RequestParam(defaultValue = "0") long id) {
        if (id != 0) {
            return userProjectService.getProjectUsersList(id);
        }
        return null;
    }

    @GetMapping("/projectList")
    public List<ProjectView> getProjectList() {
        return projectService.getProjectListForWeb();
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserProjectService(UserProjectService userProjectService) {
        this.userProjectService = userProjectService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }
}