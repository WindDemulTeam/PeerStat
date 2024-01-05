package edu.platform.parser;

import edu.platform.models.User;

import java.time.LocalDate;

public class RequestBody {

    public static String getSearchResults(int limit, int offset) {
        return String.format("""
                {
                  "operationName": "getGlobalSearchResults",
                  "variables": {
                    "searchString": "student",
                    "items": [
                      "PROFILES"
                    ],
                    "page": {
                      "limit": %d,
                      "offset": %d
                    }
                  },
                  "query": "query getGlobalSearchResults($searchString: String!, $items: [SearchItem]!, $page: PagingInput!) {\\n  globalSearch {\\n    searchByText(searchString: $searchString, items: $items, page: $page) {\\n      profiles {\\n        ...GlobalSearchProfilesSearchResult\\n        __typename\\n      }\\n      projects {\\n        ...GlobalSearchProjectsSearchResult\\n        __typename\\n      }\\n      studentCourses {\\n        ...GlobalSearchCoursesSearchResult\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment GlobalSearchProfilesSearchResult on ProfilesSearchResult {\\n  count\\n  profiles {\\n    login\\n    firstName\\n    lastName\\n    level\\n    avatarUrl\\n    school {\\n      shortName\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment GlobalSearchProjectsSearchResult on ProjectsSearchResult {\\n  count\\n  projects {\\n    studentTaskId\\n    status\\n    finalPercentage\\n    finalPoint\\n    project {\\n      goalId\\n      goalName\\n      __typename\\n    }\\n    executionType\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment GlobalSearchCoursesSearchResult on CoursesSearchResult {\\n  count\\n  courses {\\n    goalId\\n    name\\n    displayedCourseStatus\\n    executionType\\n    finalPercentage\\n    experience\\n    courseType\\n    localCourseId\\n    goalStatus\\n    __typename\\n  }\\n  __typename\\n}\\n"
                }""", limit, offset);
    }

    public static String getGraphBasisGoals(User user) {
        return String.format("""
                {
                  "operationName": "getGraphBasisGoals",
                  "variables": {
                    "studentId": "%s"
                  },
                  "query": "query getGraphBasisGoals($studentId: UUID!) {\\n  student {\\n    getBasisGraph(studentId: $studentId) {\\n      isIntensiveGraphAvailable\\n      graphNodes {\\n        ...BasisGraphNode\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment BasisGraphNode on GraphNode {\\n  graphNodeId\\n  nodeCode\\n  studyDirections {\\n    id\\n    name\\n    color\\n    textColor\\n    __typename\\n  }\\n  entityType\\n  entityId\\n  goal {\\n    goalExecutionType\\n    projectState\\n    projectName\\n    projectDescription\\n    projectPoints\\n    projectDate\\n    duration\\n    isMandatory\\n    __typename\\n  }\\n  course {\\n    courseType\\n    projectState\\n    projectName\\n    projectDescription\\n    projectPoints\\n    projectDate\\n    duration\\n    localCourseId\\n    __typename\\n  }\\n  parentNodeCodes\\n  __typename\\n}\\n"
                }
                """, user.getStudentId());
    }

    public static String getCredentialInfo(User user) {
        return String.format("""
                {
                  "operationName": "getCredentialsByLogin",
                  "variables": {
                    "login": "%s@student.21-school.ru"
                  },
                  "query": "query getCredentialsByLogin($login: String!) {\\n  school21 {\\n    getStudentByLogin(login: $login) {\\n      studentId\\n      userId\\n      schoolId\\n      isActive\\n      isGraduate\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, user.getLogin());
    }

    public static String getPersonalInfo(User user) {
        return String.format("""
                {
                  "operationName": "publicProfileGetPersonalInfo",
                  "variables": {
                    "userId": "%s",
                    "studentId": "%s",
                    "schoolId": "%s",
                    "login": "%s@student.21-school.ru"
                  },
                  "query": "query publicProfileGetPersonalInfo($userId: UUID!, $studentId: UUID!, $login: String!, $schoolId: UUID!) {\\n  school21 {\\n    getAvatarByUserId(userId: $userId)\\n    getStageGroupS21PublicProfile(studentId: $studentId) {\\n      waveId\\n      waveName\\n      eduForm\\n      __typename\\n    }\\n    getExperiencePublicProfile(userId: $userId) {\\n      value\\n      level {\\n        levelCode\\n        range {\\n          leftBorder\\n          rightBorder\\n          __typename\\n        }\\n        __typename\\n      }\\n      cookiesCount\\n      coinsCount\\n      codeReviewPoints\\n      __typename\\n    }\\n    getEmailbyUserId(userId: $userId)\\n    getClassRoomByLogin(login: $login) {\\n      id\\n      number\\n      floor\\n      __typename\\n    }\\n    __typename\\n  }\\n  student {\\n    getWorkstationByLogin(login: $login) {\\n      workstationId\\n      hostName\\n      row\\n      number\\n      __typename\\n    }\\n    getFeedbackStatisticsAverageScore(studentId: $studentId) {\\n      countFeedback\\n      feedbackAverageScore {\\n        categoryCode\\n        categoryName\\n        value\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  user {\\n    getSchool(schoolId: $schoolId) {\\n      id\\n      fullName\\n      shortName\\n      address\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, user.getUserId(), user.getStudentId(), user.getSchoolId(), user.getLogin());
    }

    public static String getCoalitionInfo(User user) {
        return String.format("""
                {
                  "operationName": "publicProfileGetCoalition",
                  "variables": {
                    "userId": "%s"
                  },
                  "query": "query publicProfileGetCoalition($userId: UUID!) {\\n  student {\\n    getUserTournamentWidget(userId: $userId) {\\n      coalitionMember {\\n        coalition {\\n          avatarUrl\\n          color\\n          name\\n          memberCount\\n          __typename\\n        }\\n        currentTournamentPowerRank {\\n          rank\\n          power {\\n            id\\n            points\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      lastTournamentResult {\\n        userRank\\n        power\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, user.getUserId());
    }

    public static String getAchievements(User user) {
        return String.format("""
                {
                  "operationName": "publicProfileLoadAverageLogtime",
                  "variables": {
                    "login": "%s@student.21-school.ru",
                    "schoolID": "%s",
                    "date": "%s"
                  },
                  "query": "query publicProfileLoadAverageLogtime($login: String!, $schoolID: UUID!, $date: Date!) {\\n  school21 {\\n    loadAverageLogtime(login: $login, schoolID: $schoolID, date: $date) {\\n      week\\n      month\\n      weekPerMonth\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, user.getLogin(), user.getSchoolId(), LocalDate.now());
    }

    public static String getLogTime(User user) {
        return String.format("""
                {
                  "operationName": "publicProfileLoadAverageLogtime",
                  "variables": {
                    "login": "%s@student.21-school.ru",
                    "schoolID": "%s",
                    "date": "%s"
                  },
                  "query": "query publicProfileLoadAverageLogtime($login: String!, $schoolID: UUID!, $date: Date!) {\\n  school21 {\\n    loadAverageLogtime(login: $login, schoolID: $schoolID, date: $date) {\\n      week\\n      month\\n      weekPerMonth\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, user.getLogin(), user.getSchoolId(), LocalDate.now());
    }

    public static String getStageInfo(User user) {
        return String.format("""
                {
                  "operationName": "publicProfileLoadStageGroups",
                  "variables": {
                    "studentId": "%s"
                  },
                  "query": "query publicProfileLoadStageGroups($studentId: UUID!) {\\n  school21 {\\n    loadStudentStageGroups(studentId: $studentId) {\\n      stageGroupStudentId\\n      studentId\\n      stageGroupS21 {\\n        waveId\\n        waveName\\n        eduForm\\n        active\\n        __typename\\n      }\\n      safeSchool {\\n        fullName\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, user.getStudentId());
    }

    public static String getXpHistory(User user) {
        return String.format("""
                {
                  "operationName": "publicProfileGetXpGraph",
                  "variables": {
                    "userId": "%s"
                  },
                  "query": "query publicProfileGetXpGraph($userId: UUID!) {\\n  student {\\n    getExperienceHistoryDate(userId: $userId) {\\n      history {\\n        awardDate\\n        expValue\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, user.getUserId());
    }

    public static String getUserProjects(User user) {
        return String.format("""
                {
                  "operationName": "publicProfileGetProjects",
                  "variables": {
                    "studentId": "%s",
                    "stageGroupId": "%s"
                  },
                  "query": "query publicProfileGetProjects($studentId: UUID!, $stageGroupId: ID!) {\\n  school21 {\\n    getStudentProjectsForPublicProfileByStageGroup(\\n      studentId: $studentId\\n      stageGroupId: $stageGroupId\\n    ) {\\n      groupName\\n      name\\n      experience\\n      finalPercentage\\n      goalId\\n      goalStatus\\n      amountAnswers\\n      amountReviewedAnswers\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, user.getStudentId(), user.getWaveId());
    }

    public static String getGraphInfo(User user) {
        return String.format("""
                {
                  "operationName": "getGraphBasisGoals",
                  "variables": {
                    "studentId": "%s"
                  },
                  "query": "query getGraphBasisGoals($studentId: UUID!) {\\n  student {\\n    getBasisGraph(studentId: $studentId) {\\n      isIntensiveGraphAvailable\\n      graphNodes {\\n        ...BasisGraphNode\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment BasisGraphNode on GraphNode {\\n  graphNodeId\\n  nodeCode\\n  studyDirections {\\n    id\\n    name\\n    color\\n    textColor\\n    __typename\\n  }\\n  entityType\\n  entityId\\n  goal {\\n    goalExecutionType\\n    projectState\\n    projectName\\n    projectDescription\\n    projectPoints\\n    projectDate\\n    duration\\n    isMandatory\\n    __typename\\n  }\\n  course {\\n    courseType\\n    projectState\\n    projectName\\n    projectDescription\\n    projectPoints\\n    projectDate\\n    duration\\n    localCourseId\\n    __typename\\n  }\\n  parentNodeCodes\\n  __typename\\n}\\n"
                }
                """, user.getStudentId());
    }

    public static String getProjectInfo(Long goalId) {
        return String.format("""
                {
                  "operationName": "getProjectInfo",
                  "variables": {
                    "goalId": "%d"
                  },
                  "query": "query getProjectInfo($goalId: ID!) {\\n  student {\\n    getModuleById(goalId: $goalId) {\\n      ...ProjectInfo\\n      __typename\\n    }\\n    getModuleCoverInformation(goalId: $goalId) {\\n      ...ModuleCoverInfo\\n      __typename\\n    }\\n    getP2PChecksInfo(goalId: $goalId) {\\n      ...P2PInfo\\n      __typename\\n    }\\n    getStudentCodeReviewByGoalId(goalId: $goalId) {\\n      ...StudentsCodeReview\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment ProjectInfo on StudentModule {\\n  id\\n  moduleTitle\\n  finalPercentage\\n  finalPoint\\n  goalExecutionType\\n  displayedGoalStatus\\n  accessBeforeStartProgress\\n  resultModuleCompletion\\n  finishedExecutionDateByScheduler\\n  durationFromStageSubjectGroupPlan\\n  currentAttemptNumber\\n  isDeadlineFree\\n  isRetryAvailable\\n  localCourseId\\n  teamSettings {\\n    ...teamSettingsInfo\\n    __typename\\n  }\\n  studyModule {\\n    id\\n    idea\\n    duration\\n    goalPoint\\n    retrySettings {\\n      ...RetrySettings\\n      __typename\\n    }\\n    levels {\\n      id\\n      goalElements {\\n        id\\n        tasks {\\n          id\\n          taskId\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  currentTask {\\n    ...CurrentInternshipTaskInfo\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment teamSettingsInfo on TeamSettings {\\n  teamCreateOption\\n  minAmountMember\\n  maxAmountMember\\n  enableSurrenderTeam\\n  __typename\\n}\\n\\nfragment RetrySettings on ModuleAttemptsSettings {\\n  maxModuleAttempts\\n  isUnlimitedAttempts\\n  __typename\\n}\\n\\nfragment CurrentInternshipTaskInfo on StudentTask {\\n  id\\n  taskId\\n  task {\\n    id\\n    assignmentType\\n    studentTaskAdditionalAttributes {\\n      cookiesCount\\n      maxCodeReviewCount\\n      codeReviewCost\\n      ciCdMode\\n      __typename\\n    }\\n    checkTypes\\n    __typename\\n  }\\n  lastAnswer {\\n    id\\n    __typename\\n  }\\n  teamSettings {\\n    ...teamSettingsInfo\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment ModuleCoverInfo on ModuleCoverInformation {\\n  isOwnStudentTimeline\\n  softSkills {\\n    softSkillId\\n    softSkillName\\n    totalPower\\n    maxPower\\n    currentUserPower\\n    achievedUserPower\\n    teamRole\\n    __typename\\n  }\\n  timeline {\\n    ...TimelineItem\\n    __typename\\n  }\\n  projectStatistics {\\n    ...ProjectStatistics\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment TimelineItem on ProjectTimelineItem {\\n  type\\n  status\\n  start\\n  end\\n  children {\\n    ...TimelineItemChildren\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment TimelineItemChildren on ProjectTimelineItem {\\n  type\\n  elementType\\n  status\\n  start\\n  end\\n  order\\n  __typename\\n}\\n\\nfragment ProjectStatistics on ProjectStatistics {\\n  registeredStudents\\n  inProgressStudents\\n  evaluationStudents\\n  finishedStudents\\n  acceptedStudents\\n  failedStudents\\n  retriedStudentsPercentage\\n  groupProjectStatistics {\\n    ...GroupProjectStatistics\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment GroupProjectStatistics on GroupProjectStatistics {\\n  inProgressTeams\\n  evaluationTeams\\n  finishedTeams\\n  acceptedTeams\\n  failedTeams\\n  __typename\\n}\\n\\nfragment P2PInfo on P2PChecksInfo {\\n  cookiesCount\\n  periodOfVerification\\n  projectReviewsInfo {\\n    ...ProjectReviewsInfo\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment ProjectReviewsInfo on ProjectReviewsInfo {\\n  reviewByStudentCount\\n  relevantReviewByStudentsCount\\n  reviewByInspectionStaffCount\\n  relevantReviewByInspectionStaffCount\\n  __typename\\n}\\n\\nfragment StudentsCodeReview on StudentCodeReviewsWithCountRound {\\n  countRound1\\n  countRound2\\n  codeReviewsInfo {\\n    maxCodeReviewCount\\n    codeReviewDuration\\n    codeReviewCost\\n    __typename\\n  }\\n  __typename\\n}\\n"
                }""", goalId);
    }

    public static String getBuildingInfo() {
        return """
                {
                  "operationName": "getCampusBuildings",
                  "variables": {
                   
                  },
                  "query": "query getCampusBuildings {\\n  student {\\n    getBuildings {\\n      id\\n      name\\n      classrooms {\\n        id\\n        number\\n        capacity\\n        availableCapacity\\n        floor\\n        classroomPlan {\\n          classroomPlanId\\n          planMeta\\n          __typename\\n        }\\n        specializations\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }""";
    }

    public static String getClusterPlanInfo(int clusterId) {
        return String.format("""
                {
                  "operationName": "getCampusPlanOccupied",
                  "variables": {
                    "clusterId": "%d"
                  },
                  "query": "query getCampusPlanOccupied($clusterId: ID!) {\\n  student {\\n    getClusterPlanStudentsByClusterId(clusterId: $clusterId) {\\n      occupiedPlaces {\\n        row\\n        number\\n        stageGroupName\\n        stageName\\n        user {\\n          id\\n          login\\n          avatarUrl\\n          __typename\\n        }\\n        experience {\\n          id\\n          value\\n          level {\\n            id\\n            range {\\n              id\\n              levelCode\\n              leftBorder\\n              rightBorder\\n              __typename\\n            }\\n            __typename\\n          }\\n          __typename\\n        }\\n        studentType\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n"
                }
                """, clusterId);
    }

}
