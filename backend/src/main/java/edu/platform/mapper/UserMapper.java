package edu.platform.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.platform.modelView.StatUserView;
import edu.platform.models.User;
import edu.platform.models.UserProjectDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
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

    public static String getRealWave(String wave) {
        return WAVE_UNKNOWN;
    }

    public static String getLogin(String  login, boolean isGraduate, boolean isActive) {
        if (isGraduate) {
            login += " " + ALUMNI;
        } else if (!isActive) {
            login += " " + DEACTIVATED;
        }
        return login;
    }

    private int getXpDiff(int xp, String xph, int noOfMonths) {
        LocalDate minusMonth = LocalDate.now().minusMonths(noOfMonths);
        int diff = 0;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode xpHistory = mapper.readTree(xph);
            int minXpValue = xp;
            for (JsonNode row : xpHistory) {
                LocalDate date = LocalDate.parse(row.get(AWARD_DATE).asText());
                int xpValue = row.get(XP_VALUE).asInt();
                if (!date.isBefore(minusMonth) && xpValue < minXpValue) {
                    minXpValue = xpValue;
                }
            }
            diff = xp - minXpValue;
        } catch (JsonProcessingException e) {
            System.out.println("[userService] getMonthDiff ERROR " + e.getMessage());
        }
        return diff;
    }

    public StatUserView getUserStatView(UserProjectDTO user) {
        StatUserView statUserView = new StatUserView();

        statUserView.setLogin(getLogin(user.getLogin(), user.getIsGraduate(), user.getIsActive()));
        statUserView.setEmail(user.getEmail());
        statUserView.setCampus(CAMPUS_LOCALE.get(user.getCampus()));
        statUserView.setCoalition(user.getCoalitionName());
        statUserView.setWave(getRealWave(user.getWaveName()));
        statUserView.setPlatformClass(user.getWaveName());
        statUserView.setBootcamp(user.getBootcampName());
        statUserView.setLevel(user.getLevel());
        statUserView.setXp(user.getXp());
        statUserView.setPeerPoints(user.getPeerPoints());
        statUserView.setCodeReviewPoints(user.getCodeReviewPoints());
        statUserView.setCoins(user.getCoins());
        statUserView.setDiff(getXpDiff(user.getXp(), user.getXpHistory(), 1));
        statUserView.setDiff3(getXpDiff(user.getXp(), user.getXpHistory(), 3));
        List<String> userProjects = user.getProjects();
        if (userProjects != null) {
            statUserView.setCurrentProject(userProjects.stream().map(Object::toString)
                    .collect(Collectors.joining(", ")));
        }

        return statUserView;
    }
}
