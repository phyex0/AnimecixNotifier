package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.config.SystemConfig;
import com.phyex.animecixnotifier.document.UserDocument;
import com.phyex.animecixnotifier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class AnimecixScheduleServiceImpl implements AnimecixScheduleService {

    private final SystemConfig systemConfig;
    private final UserRepository userRepository;
    private final ExecutorService executorService;
    private final AnimecixUserService animecixUserService;


    @Override
    @Scheduled(fixedDelayString = "#{@systemConfig.scheduleFixedRate}", timeUnit = TimeUnit.HOURS)
    public void newEpisodeScheduler() {
        animecixUserService.fetchLastEpisodes();

        int page = 0;
        Slice<UserDocument> userSlice;
        Sort sort = Sort.by(Sort.Direction.ASC, "_id");

        do {

            Pageable pageable = PageRequest.of(page, systemConfig.getBatchSize(), sort);
            userSlice = userRepository.findAll(pageable);

            List<Future<?>> futures = new ArrayList<>();
            for (UserDocument user : userSlice.getContent()) {
                Future<?> future = executorService.submit(() -> animecixUserService.updateUser(user));
                log.debug("Created executor future: {}", future);
                futures.add(future);
            }
            waitForBatch(futures);
            page++;

        } while (userSlice.hasNext());

    }

    private void waitForBatch(List<? extends Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("User update interrupted", e);
            } catch (ExecutionException e) {
                log.error("User update failed", e.getCause());
            }
        }
    }
}
