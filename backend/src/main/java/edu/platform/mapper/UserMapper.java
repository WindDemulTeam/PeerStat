package edu.platform.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.platform.constants.ProjectState;
import edu.platform.modelView.StatUserView;
import edu.platform.models.Project;
import edu.platform.models.User;
import edu.platform.models.UserProject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public static final Map<String, String> CAMPUS_LOCALE = Map.of(
            "21 Moscow", "Москва",
            "21 Kazan", "Казань",
            "21 Novosibirsk", "Новосибирск",
            "21 Surgut", "Сургут",
            "21 Test", "Тестирование",
            "21 Test QA", "Тестирование QA"
    );
    private static final String AWARD_DATE = "awardDate";
    private static final String XP_VALUE = "expValue";
    private static final String WAVE_INTRA = "Intra";
    private static final String WAVE_UNKNOWN = "???";
    private static final String ALUMNI = "(alumni)";
    private static final String DEACTIVATED = "(deactivated)";

    public static String getRealWave(User user) {
        return WAVE_UNKNOWN;
    }

    public static String getLogin(User user) {
        String login = user.getLogin();
        if (user.isGraduate()) {
            login += " " + ALUMNI;
        } else if (!user.isActive()) {
            login += " " + DEACTIVATED;
        }
        return login;
    }

    private String getCurrentProject(User user) {
        return user.getUserProjectList().stream()
                .filter(up -> up.getProjectState().equals(ProjectState.IN_PROGRESS)
                        || up.getProjectState().equals(ProjectState.P2P_EVALUATIONS))
                .map(UserProject::getProject)
                .map(Project::getProjectName)
                .collect(Collectors.joining(" "));
    }

    private int getXpDiff(User user, int noOfMonths) {
        LocalDate minusMonth = LocalDate.now().minusMonths(noOfMonths);
        int diff = 0;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode xpHistory = mapper.readTree(user.getXpHistory());
            int minXpValue = user.getXp();
            for (JsonNode row : xpHistory) {
                LocalDate date = LocalDate.parse(row.get(AWARD_DATE).asText());
                int xpValue = row.get(XP_VALUE).asInt();
                if (!date.isBefore(minusMonth) && xpValue < minXpValue) {
                    minXpValue = xpValue;
                }
            }
            diff = user.getXp() - minXpValue;
        } catch (JsonProcessingException e) {
            System.out.println("[userService] getMonthDiff ERROR " + e.getMessage());
        }
        return diff;
    }

    public StatUserView getUserStatView(User user) {
        StatUserView statUserView = new StatUserView();

        statUserView.setLogin(getLogin(user));
        statUserView.setEmail(user.getEmail());
        statUserView.setCampus(CAMPUS_LOCALE.get(user.getCampus()));
        statUserView.setCoalition(user.getCoalitionName());
        statUserView.setWave(getRealWave(user));
        statUserView.setPlatformClass(user.getWaveName());
        statUserView.setBootcamp(user.getBootcampName());
        statUserView.setLevel(user.getLevel());
        statUserView.setXp(user.getXp());
        statUserView.setPeerPoints(user.getPeerPoints());
        statUserView.setCodeReviewPoints(user.getCodeReviewPoints());
        statUserView.setCoins(user.getCoins());
        statUserView.setDiff(getXpDiff(user, 1));
        statUserView.setDiff3(getXpDiff(user, 3));
        statUserView.setCurrentProject(getCurrentProject(user));

        return statUserView;
    }
}
