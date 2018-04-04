package com.github.cschen1205.tensorflow.web.components;

import com.github.cschen1205.objdetect.ObjectDetector;
import com.github.cschen1205.tensorflow.recommenders.models.ImageRecommender;
import com.github.cschen1205.tensorflow.commons.FileUtils;
import com.github.cschen1205.tensorflow.search.models.ImageSearchEngine;
import com.github.cschen1205.tensorflow.web.services.ImageClassifierService;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
public class Loader implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(Loader.class);


    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    @Override public void onApplicationEvent(ApplicationReadyEvent e) {
        logger.info("Loader triggered at {}", e.getTimestamp());

        final ApplicationContext context = e.getApplicationContext();
        final ImageClassifierService imageClassifierService = context.getBean(ImageClassifierService.class);
        final ImageSearchEngine imageSearchEngine = context.getBean("imageSearchEngine", ImageSearchEngine.class);
        final ImageRecommender imageRecommender = context.getBean("imageRecommender", ImageRecommender.class);
        final ObjectDetector objectDetector = context.getBean("objectDetector", ObjectDetector.class);

        executorService.submit(imageClassifierService::loadModel);
        executorService.submit(() -> {
            if(!imageSearchEngine.loadIndexDbIfExists()) {
                imageSearchEngine.indexAll(FileUtils.getImageFiles());
                imageSearchEngine.saveIndexDb();
            }
        });
        executorService.submit(() -> {
            if(!imageRecommender.loadIndexDbIfExists()) {
                imageRecommender.indexAll(FileUtils.getAudioFiles());
                imageRecommender.saveIndexDb();
            }
        });
        executorService.submit(() -> {
            try{
                objectDetector.loadModel();
            }catch(Exception ex) {
                logger.error("Failed to load model for the object detector", ex);
            }
        });

    }





}