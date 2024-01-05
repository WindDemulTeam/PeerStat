package edu.platform.parser;

import edu.platform.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class Runner {
    @Value("${run.runtime}")
    private String runTimeSetting;
    @Value("${run.plus-days}")
    private String plusDays;
    private Parser parser;
    private LoginService loginService;

    @Autowired
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAtStart() {
        System.out.println("[Runner] run");

        try {
            loginService.Init();
            parser.parseGraphInfo();
            parser.initUsers();

            scheduleDataUpdate();
            scheduleCookieUpdate();
            scheduleLocationsUpdate();

        } catch (IOException e) {
            System.out.println("[Runner] ERROR " + e.getMessage());
        }
    }

    private void scheduleDataUpdate() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalTime runTime = LocalTime.parse(runTimeSetting);
        long delay = LocalDateTime.now().until(LocalDate.now()
                .plusDays(Integer.parseInt(plusDays))
                .atTime(runTime), ChronoUnit.MINUTES);

        System.out.println("[Runner] scheduleDataUpdate time " + runTime + " delay " + delay);

        final Runnable scheduleRunner = this::dataUpdate;
        scheduler.scheduleAtFixedRate(scheduleRunner, delay,
                TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
    }

    private void scheduleCookieUpdate() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        long period = TimeUnit.HOURS.toMinutes(6);
        System.out.println("[Runner] scheduleCookieUpdate period " + period);

        final Runnable scheduleRunner = this::cookieUpdate;
        scheduler.scheduleAtFixedRate(scheduleRunner, period, period, TimeUnit.MINUTES);
    }

    private void scheduleLocationsUpdate() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        long period = TimeUnit.MINUTES.toMinutes(10);
        System.out.println("[Runner] scheduleLocationsUpdate period " + period);

        final Runnable scheduleRunner = this::locationsUpdate;
        scheduler.scheduleAtFixedRate(scheduleRunner, TimeUnit.MINUTES.toMinutes(1), period, TimeUnit.MINUTES);
    }

    private void dataUpdate() {
        System.out.println("[Runner] dataUpdate " + LocalDateTime.now());
        parser.updateUsers();
    }

    private void cookieUpdate() {
        System.out.println("[Runner] cookieUpdate " + LocalDateTime.now());
        loginService.setCookies();
    }

    private void locationsUpdate() {
        System.out.println("[Runner] locationsUpdate " + LocalDateTime.now());
        parser.updateUserLocations();
    }
}
