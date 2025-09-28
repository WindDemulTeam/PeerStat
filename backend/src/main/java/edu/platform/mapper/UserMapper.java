package edu.platform.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.platform.modelView.StatUserView;
import edu.platform.models.UserProjectDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private static final String AWARD_DATE = "awardDate";
    private static final String XP_VALUE = "expValue";
    private static final String ALUMNI = "(alumni)";
    private static final String DEACTIVATED = "(deactivated)";
    public static Map<String, String> CAMPUS_LOCALE;

    public UserMapper() {
        CAMPUS_LOCALE = new HashMap<>();

        CAMPUS_LOCALE.put("21 Moscow", "Москва");
        CAMPUS_LOCALE.put("21 Kazan", "Казань");
        CAMPUS_LOCALE.put("21 Novosibirsk", "Новосибирск");
        CAMPUS_LOCALE.put("21 Surgut", "Сургут");
        CAMPUS_LOCALE.put("21 Test", "Тестирование");
        CAMPUS_LOCALE.put("21 Test QA", "Тестирование QA");
        CAMPUS_LOCALE.put("21 Yakutsk", "Якутск");
        CAMPUS_LOCALE.put("21 Veliky Novgorod", "Великий Новгород");
        CAMPUS_LOCALE.put("21 Samarkand", "Самарканд");
        CAMPUS_LOCALE.put("21 Yaroslavl", "Ярославль");
        CAMPUS_LOCALE.put("21 Sakhalin", "Сахалин");
        CAMPUS_LOCALE.put("21 Tashkent", "Ташкент");
        CAMPUS_LOCALE.put("21 Magadan", "Магадан");
        CAMPUS_LOCALE.put("21 Belgorod", "Белгород");
        CAMPUS_LOCALE.put("21 Anadyr", "Анадырь");
        CAMPUS_LOCALE.put("21 Chelyabinsk", "Челябинск");
        CAMPUS_LOCALE.put("Акселератор студента 0 этап", "Акселератор студента 0 этап");
        CAMPUS_LOCALE.put("21 Lipetsk", "Липецк");
        CAMPUS_LOCALE.put("21 Magas", "Магас");
        CAMPUS_LOCALE.put("21 Nizhny Novgorod", "Нижний Новгород");
    }

    public static String getLogin(String login, boolean isGraduate, boolean isActive) {
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
