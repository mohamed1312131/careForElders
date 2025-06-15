package com.care4elders.blogforum.service;

import com.care4elders.blogforum.nlp.StanfordNLPPipeline;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StanfordNLPSentimentAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(StanfordNLPSentimentAnalysisService.class);
    
    private final StanfordNLPPipeline nlpPipeline;
    
    @Autowired
    public StanfordNLPSentimentAnalysisService(StanfordNLPPipeline nlpPipeline) {
        this.nlpPipeline = nlpPipeline;
    }
    
    /**
     * Analyzes sentiment using Stanford CoreNLP
     */
    public SentimentResult analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new SentimentResult("NEUTRAL", 0.0, "Empty text", new ArrayList<>());
        }
        
        logger.info("Analyzing sentiment with Stanford CoreNLP for text: {}", 
                   text.substring(0, Math.min(text.length(), 50)) + "...");
        
        try {
            // Create document and annotate
            CoreDocument document = new CoreDocument(text);
            nlpPipeline.getSentimentPipeline().annotate(document);
            
            List<SentenceAnalysis> sentenceAnalyses = new ArrayList<>();
            double totalScore = 0.0;
            int sentenceCount = 0;
            
            // Analyze each sentence
            for (CoreSentence sentence : document.sentences()) {
                Tree sentimentTree = sentence.sentimentTree();
                if (sentimentTree != null) {
                    int sentimentValue = RNNCoreAnnotations.getPredictedClass(sentimentTree);
                    double confidence = getConfidence(sentimentTree, sentimentValue);
                    String sentimentLabel = getSentimentLabel(sentimentValue);
                    double normalizedScore = normalizeScore(sentimentValue);
                    
                    sentenceAnalyses.add(new SentenceAnalysis(
                        sentence.text(),
                        sentimentLabel,
                        normalizedScore,
                        confidence,
                        sentimentValue
                    ));
                    
                    totalScore += normalizedScore;
                    sentenceCount++;
                }
            }
            
            // Calculate overall sentiment
            double averageScore = sentenceCount > 0 ? totalScore / sentenceCount : 0.0;
            String overallSentiment = getOverallSentiment(averageScore);
            
            String analysis = String.format(
                "Stanford CoreNLP Analysis - Sentences: %d, Average Score: %.3f, Overall: %s",
                sentenceCount, averageScore, overallSentiment
            );
            
            logger.info("Stanford CoreNLP sentiment analysis complete: {}", overallSentiment);
            
            return new SentimentResult(overallSentiment, averageScore, analysis, sentenceAnalyses);
            
        } catch (Exception e) {
            logger.error("Error during Stanford CoreNLP sentiment analysis", e);
            return new SentimentResult("NEUTRAL", 0.0, "Analysis failed: " + e.getMessage(), new ArrayList<>());
        }
    }
    
    /**
     * Performs comprehensive text analysis including sentiment, entities, and keywords
     */
    public ComprehensiveAnalysis performComprehensiveAnalysis(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ComprehensiveAnalysis();
        }
        
        logger.info("Performing comprehensive analysis with Stanford CoreNLP");
        
        try {
            // Sentiment Analysis
            SentimentResult sentimentResult = analyzeSentiment(text);
            
            // Named Entity Recognition
            List<NamedEntity> entities = extractNamedEntities(text);
            
            // Keyword Extraction (using POS tagging)
            List<String> keywords = extractKeywords(text);
            
            // Emotion Detection (rule-based enhancement)
            String emotionalTone = detectEmotionalTone(text, sentimentResult);
            
            return new ComprehensiveAnalysis(
                sentimentResult,
                entities,
                keywords,
                emotionalTone,
                text.split("\\s+").length, // word count
                estimateReadingTime(text)
            );
            
        } catch (Exception e) {
            logger.error("Error during comprehensive analysis", e);
            return new ComprehensiveAnalysis();
        }
    }
    
    /**
     * Extracts named entities from text
     */
    public List<NamedEntity> extractNamedEntities(String text) {
        List<NamedEntity> entities = new ArrayList<>();
        
        try {
            CoreDocument document = new CoreDocument(text);
            nlpPipeline.getNerPipeline().annotate(document);
            
            for (CoreMap sentence : document.annotation().get(CoreAnnotations.SentencesAnnotation.class)) {
                for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    
                    if (ner != null && !ner.equals("O")) {
                        entities.add(new NamedEntity(word, ner));
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting named entities", e);
        }
        
        return entities;
    }
    
    /**
     * Extracts keywords using POS tagging
     */
    public List<String> extractKeywords(String text) {
        List<String> keywords = new ArrayList<>();
        
        try {
            CoreDocument document = new CoreDocument(text);
            nlpPipeline.getMainPipeline().annotate(document);
            
            for (CoreMap sentence : document.annotation().get(CoreAnnotations.SentencesAnnotation.class)) {
                for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                    
                    // Extract nouns, adjectives, and verbs as keywords
                    if (pos != null && (pos.startsWith("NN") || pos.startsWith("JJ") || pos.startsWith("VB")) 
                        && word.length() > 3) {
                        keywords.add(lemma != null ? lemma.toLowerCase() : word.toLowerCase());
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error extracting keywords", e);
        }
        
        return keywords.stream().distinct().limit(10).toList();
    }
    
    // Helper methods - FIXED VERSION
    private double getConfidence(Tree sentimentTree, int predictedClass) {
        if (sentimentTree == null) return 0.0;
        
        try {
            // Fixed: Handle both SimpleMatrix and double[] return types
            Object predictions = RNNCoreAnnotations.getPredictions(sentimentTree);
            
            if (predictions instanceof SimpleMatrix) {
                SimpleMatrix matrix = (SimpleMatrix) predictions;
                if (predictedClass < matrix.numRows()) {
                    return matrix.get(predictedClass, 0);
                }
            } else if (predictions instanceof double[]) {
                double[] predArray = (double[]) predictions;
                if (predictedClass < predArray.length) {
                    return predArray[predictedClass];
                }
            }
            
            return 0.0;
            
        } catch (Exception e) {
            logger.warn("Error getting confidence score: {}", e.getMessage());
            return 0.0;
        }
    }
    
    private String getSentimentLabel(int sentimentValue) {
        switch (sentimentValue) {
            case 0: return "VERY_NEGATIVE";
            case 1: return "NEGATIVE";
            case 2: return "NEUTRAL";
            case 3: return "POSITIVE";
            case 4: return "VERY_POSITIVE";
            default: return "NEUTRAL";
        }
    }
    
    private double normalizeScore(int sentimentValue) {
        // Convert 0-4 scale to -1 to 1 scale
        return (sentimentValue - 2.0) / 2.0;
    }
    
    private String getOverallSentiment(double score) {
        if (score > 0.3) return "POSITIVE";
        if (score < -0.3) return "NEGATIVE";
        return "NEUTRAL";
    }
    
    private String detectEmotionalTone(String text, SentimentResult sentimentResult) {
        String lowerText = text.toLowerCase();
        
        // Enhanced emotion detection using sentiment context
        if (sentimentResult.getSentiment().contains("NEGATIVE")) {
            if (lowerText.contains("angry") || lowerText.contains("furious") || lowerText.contains("mad")) {
                return "ANGRY";
            } else if (lowerText.contains("sad") || lowerText.contains("depressed") || lowerText.contains("disappointed")) {
                return "SAD";
            } else if (lowerText.contains("worried") || lowerText.contains("anxious") || lowerText.contains("concerned")) {
                return "WORRIED";
            }
            return "NEGATIVE";
        } else if (sentimentResult.getSentiment().contains("POSITIVE")) {
            if (lowerText.contains("excited") || lowerText.contains("thrilled") || lowerText.contains("amazing")) {
                return "EXCITED";
            } else if (lowerText.contains("grateful") || lowerText.contains("thankful") || lowerText.contains("appreciate")) {
                return "GRATEFUL";
            }
            return "POSITIVE";
        }
        
        return "NEUTRAL";
    }
    
    private int estimateReadingTime(String text) {
        int wordCount = text.split("\\s+").length;
        return Math.max(1, wordCount / 200); // Assuming 200 words per minute
    }
    
    // Inner classes for structured results
    public static class SentimentResult {
        private final String sentiment;
        private final double score;
        private final String analysis;
        private final List<SentenceAnalysis> sentenceAnalyses;
        
        public SentimentResult(String sentiment, double score, String analysis, List<SentenceAnalysis> sentenceAnalyses) {
            this.sentiment = sentiment;
            this.score = score;
            this.analysis = analysis;
            this.sentenceAnalyses = sentenceAnalyses;
        }
        
        // Getters
        public String getSentiment() { return sentiment; }
        public double getScore() { return score; }
        public String getAnalysis() { return analysis; }
        public List<SentenceAnalysis> getSentenceAnalyses() { return sentenceAnalyses; }
    }
    
    public static class SentenceAnalysis {
        private final String text;
        private final String sentiment;
        private final double score;
        private final double confidence;
        private final int rawValue;
        
        public SentenceAnalysis(String text, String sentiment, double score, double confidence, int rawValue) {
            this.text = text;
            this.sentiment = sentiment;
            this.score = score;
            this.confidence = confidence;
            this.rawValue = rawValue;
        }
        
        // Getters
        public String getText() { return text; }
        public String getSentiment() { return sentiment; }
        public double getScore() { return score; }
        public double getConfidence() { return confidence; }
        public int getRawValue() { return rawValue; }
    }
    
    public static class NamedEntity {
        private final String text;
        private final String type;
        
        public NamedEntity(String text, String type) {
            this.text = text;
            this.type = type;
        }
        
        // Getters
        public String getText() { return text; }
        public String getType() { return type; }
    }
    
    public static class ComprehensiveAnalysis {
        private final SentimentResult sentimentResult;
        private final List<NamedEntity> namedEntities;
        private final List<String> keywords;
        private final String emotionalTone;
        private final int wordCount;
        private final int readingTimeMinutes;
        
        public ComprehensiveAnalysis() {
            this(null, new ArrayList<>(), new ArrayList<>(), "NEUTRAL", 0, 0);
        }
        
        public ComprehensiveAnalysis(SentimentResult sentimentResult, List<NamedEntity> namedEntities, 
                                   List<String> keywords, String emotionalTone, int wordCount, int readingTimeMinutes) {
            this.sentimentResult = sentimentResult;
            this.namedEntities = namedEntities;
            this.keywords = keywords;
            this.emotionalTone = emotionalTone;
            this.wordCount = wordCount;
            this.readingTimeMinutes = readingTimeMinutes;
        }
        
        // Getters
        public SentimentResult getSentimentResult() { return sentimentResult; }
        public List<NamedEntity> getNamedEntities() { return namedEntities; }
        public List<String> getKeywords() { return keywords; }
        public String getEmotionalTone() { return emotionalTone; }
        public int getWordCount() { return wordCount; }
        public int getReadingTimeMinutes() { return readingTimeMinutes; }
    }
}