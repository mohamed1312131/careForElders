package com.care4elders.blogforum.nlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class StanfordNLPPipeline {

    private static final Logger logger = LoggerFactory.getLogger(StanfordNLPPipeline.class);

    private StanfordCoreNLP pipeline;
    private StanfordCoreNLP sentimentPipeline;
    private StanfordCoreNLP nerPipeline;
    private boolean initialized = false;

    @PostConstruct
    public void init() {
        logger.info("Initializing Stanford CoreNLP pipelines...");
        
        try {
            // Set system properties for JAXB compatibility
            System.setProperty("javax.xml.bind.JAXBContextFactory", "org.glassfish.jaxb.runtime.v2.JAXBContextFactory");
            
            // Main pipeline with basic annotators (no NER to avoid JAXB issues)
            Properties mainProps = new Properties();
            mainProps.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse");
            mainProps.setProperty("tokenize.language", "en");
            mainProps.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
            this.pipeline = new StanfordCoreNLP(mainProps);
            logger.info("Main Stanford CoreNLP pipeline initialized successfully");

            // Sentiment analysis pipeline
            Properties sentimentProps = new Properties();
            sentimentProps.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,sentiment");
            sentimentProps.setProperty("tokenize.language", "en");
            sentimentProps.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
            this.sentimentPipeline = new StanfordCoreNLP(sentimentProps);
            logger.info("Sentiment analysis pipeline initialized successfully");

            // Simplified NER pipeline (without time expressions to avoid JAXB issues)
            Properties nerProps = new Properties();
            nerProps.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            nerProps.setProperty("tokenize.language", "en");
            nerProps.setProperty("ner.useSUTime", "false"); // Disable SUTime to avoid JAXB issues
            nerProps.setProperty("ner.applyNumericClassifiers", "false");
            this.nerPipeline = new StanfordCoreNLP(nerProps);
            logger.info("Named Entity Recognition pipeline initialized successfully");

            this.initialized = true;
            logger.info("All Stanford CoreNLP pipelines initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize Stanford CoreNLP pipelines", e);
            this.initialized = false;
            
            // Create fallback minimal pipelines
            try {
                logger.info("Attempting to create minimal fallback pipelines...");
                createFallbackPipelines();
            } catch (Exception fallbackError) {
                logger.error("Failed to create fallback pipelines", fallbackError);
                throw new RuntimeException("Stanford CoreNLP initialization completely failed", fallbackError);
            }
        }
    }

    private void createFallbackPipelines() {
        try {
            // Minimal sentiment pipeline
            Properties minimalSentimentProps = new Properties();
            minimalSentimentProps.setProperty("annotators", "tokenize,ssplit,sentiment");
            minimalSentimentProps.setProperty("tokenize.language", "en");
            this.sentimentPipeline = new StanfordCoreNLP(minimalSentimentProps);
            
            // Minimal main pipeline
            Properties minimalMainProps = new Properties();
            minimalMainProps.setProperty("annotators", "tokenize,ssplit,pos");
            minimalMainProps.setProperty("tokenize.language", "en");
            this.pipeline = new StanfordCoreNLP(minimalMainProps);
            
            // Set NER pipeline to null (will be handled gracefully)
            this.nerPipeline = null;
            
            this.initialized = true;
            logger.info("Fallback pipelines created successfully");
            
        } catch (Exception e) {
            logger.error("Failed to create fallback pipelines", e);
            throw e;
        }
    }

    @PreDestroy
    public void cleanup() {
        logger.info("Cleaning up Stanford CoreNLP pipelines...");
        this.initialized = false;
        logger.info("Stanford CoreNLP pipelines cleanup completed");
    }

    public StanfordCoreNLP getMainPipeline() {
        if (!initialized || pipeline == null) {
            throw new IllegalStateException("Main pipeline not initialized");
        }
        return pipeline;
    }

    public StanfordCoreNLP getSentimentPipeline() {
        if (!initialized || sentimentPipeline == null) {
            throw new IllegalStateException("Sentiment pipeline not initialized");
        }
        return sentimentPipeline;
    }

    public StanfordCoreNLP getNerPipeline() {
        if (!initialized) {
            throw new IllegalStateException("NER pipeline not initialized");
        }
        if (nerPipeline == null) {
            logger.warn("NER pipeline is not available, returning sentiment pipeline as fallback");
            return sentimentPipeline;
        }
        return nerPipeline;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isNerAvailable() {
        return initialized && nerPipeline != null;
    }

    /**
     * Creates a custom pipeline with specified annotators
     */
    public StanfordCoreNLP createCustomPipeline(String annotators) {
        if (!initialized) {
            throw new IllegalStateException("Pipeline not initialized");
        }
        
        try {
            Properties props = new Properties();
            props.setProperty("annotators", annotators);
            props.setProperty("tokenize.language", "en");
            
            // Disable problematic features
            if (annotators.contains("ner")) {
                props.setProperty("ner.useSUTime", "false");
                props.setProperty("ner.applyNumericClassifiers", "false");
            }
            
            return new StanfordCoreNLP(props);
        } catch (Exception e) {
            logger.error("Failed to create custom pipeline with annotators: {}", annotators, e);
            throw new RuntimeException("Custom pipeline creation failed", e);
        }
    }

    /**
     * Gets pipeline information for debugging
     */
    public String getPipelineInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Stanford CoreNLP Pipeline Status:\n");
        info.append("Initialized: ").append(initialized).append("\n");
        info.append("Main Pipeline: ").append(pipeline != null ? "Available" : "Not Available").append("\n");
        info.append("Sentiment Pipeline: ").append(sentimentPipeline != null ? "Available" : "Not Available").append("\n");
        info.append("NER Pipeline: ").append(nerPipeline != null ? "Available" : "Not Available (using fallback)").append("\n");
        return info.toString();
    }
}