package com.care4elders.blogforum.service;

import com.care4elders.blogforum.nlp.StanfordNLPPipeline;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.trees.Tree;
import org.ejml.simple.SimpleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EnhancedSentimentAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedSentimentAnalysisService.class);

    private final StanfordNLPPipeline nlpPipeline;

    // Emotion keywords for enhanced analysis
    private static final Map<String, Set<String>> EMOTION_KEYWORDS = Map.of(
        "JOY", Set.of("happy", "joy", "excited", "thrilled", "delighted", "cheerful", "elated", "euphoric"),
        "ANGER", Set.of("angry", "furious", "mad", "rage", "irritated", "annoyed", "outraged", "livid"),
        "SADNESS", Set.of("sad", "depressed", "melancholy", "grief", "sorrow", "despair", "heartbroken", "miserable"),
        "FEAR", Set.of("afraid", "scared", "terrified", "anxious", "worried", "nervous", "panic", "frightened"),
        "SURPRISE", Set.of("surprised", "amazed", "astonished", "shocked", "stunned", "bewildered", "startled"),
        "DISGUST", Set.of("disgusted", "revolted", "repulsed", "sickened", "appalled", "nauseated")
    );

    // Intensity modifiers
    private static final Set<String> INTENSIFIERS = Set.of(
        "very", "extremely", "incredibly", "absolutely", "completely", "totally", "utterly", "quite", "rather", "really"
    );

    private static final Set<String> DIMINISHERS = Set.of(
        "slightly", "somewhat", "barely", "hardly", "scarcely", "little", "bit", "kind of", "sort of"
    );

    @Autowired
    public EnhancedSentimentAnalysisService(StanfordNLPPipeline nlpPipeline) {
        this.nlpPipeline = nlpPipeline;
    }

    /**
     * Performs comprehensive sentiment analysis using Stanford CoreNLP
     */
    public SentimentResult analyzeWithHybridApproach(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new SentimentResult("NEUTRAL", 0.0, "Empty text provided", null, false);
        }

        logger.info("Starting enhanced sentiment analysis for text: {}", 
                   text.substring(0, Math.min(text.length(), 50)) + "...");

        try {
            // Create document and annotate with sentiment pipeline
            CoreDocument document = new CoreDocument(text);
            nlpPipeline.getSentimentPipeline().annotate(document);

            // Analyze sentences
            List<SentenceAnalysis> sentenceAnalyses = analyzeSentences(document);
            
            // Calculate overall sentiment
            OverallSentiment overallSentiment = calculateOverallSentiment(sentenceAnalyses);
            
            // Detect emotions
            EmotionAnalysis emotionAnalysis = detectEmotions(text, sentenceAnalyses);
            
            // Analyze intensity and modifiers
            IntensityAnalysis intensityAnalysis = analyzeIntensity(text);
            
            // Generate comprehensive analysis report
            String analysisReport = generateAnalysisReport(sentenceAnalyses, overallSentiment, 
                                                         emotionAnalysis, intensityAnalysis);

            logger.info("Enhanced sentiment analysis completed: {} (score: {})", 
                       overallSentiment.label, overallSentiment.score);

            return new SentimentResult(
                overallSentiment.label,
                overallSentiment.score,
                analysisReport,
                new EnhancedAnalysisData(sentenceAnalyses, emotionAnalysis, intensityAnalysis),
                true
            );

        } catch (Exception e) {
            logger.error("Error during enhanced sentiment analysis", e);
            return new SentimentResult("NEUTRAL", 0.0, "Analysis failed: " + e.getMessage(), null, false);
        }
    }

    /**
     * Analyzes individual sentences for sentiment
     */
    private List<SentenceAnalysis> analyzeSentences(CoreDocument document) {
        List<SentenceAnalysis> analyses = new ArrayList<>();

        for (CoreSentence sentence : document.sentences()) {
            try {
                Tree sentimentTree = sentence.sentimentTree();
                if (sentimentTree != null) {
                    int sentimentValue = RNNCoreAnnotations.getPredictedClass(sentimentTree);
                    double confidence = getConfidenceScore(sentimentTree, sentimentValue);
                    String sentimentLabel = mapSentimentValue(sentimentValue);
                    double normalizedScore = normalizeSentimentScore(sentimentValue);

                    // Extract key phrases from sentence
                    List<String> keyPhrases = extractKeyPhrases(sentence);
                    
                    // Detect negation
                    boolean hasNegation = detectNegation(sentence);

                    analyses.add(new SentenceAnalysis(
                        sentence.text(),
                        sentimentLabel,
                        normalizedScore,
                        confidence,
                        sentimentValue,
                        keyPhrases,
                        hasNegation
                    ));
                }
            } catch (Exception e) {
                logger.warn("Error analyzing sentence: {}", sentence.text(), e);
                // Add neutral analysis for failed sentences
                analyses.add(new SentenceAnalysis(
                    sentence.text(), "NEUTRAL", 0.0, 0.0, 2, new ArrayList<>(), false
                ));
            }
        }

        return analyses;
    }

    /**
     * Calculates overall sentiment from sentence analyses
     */
    private OverallSentiment calculateOverallSentiment(List<SentenceAnalysis> sentenceAnalyses) {
        if (sentenceAnalyses.isEmpty()) {
            return new OverallSentiment("NEUTRAL", 0.0, 0.0);
        }

        // Weighted average based on sentence length and confidence
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;

        for (SentenceAnalysis analysis : sentenceAnalyses) {
            double weight = Math.max(0.1, analysis.confidence) * Math.log(analysis.text.length() + 1);
            totalWeightedScore += analysis.normalizedScore * weight;
            totalWeight += weight;
        }

        double averageScore = totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
        
        // Calculate confidence based on consistency
        double confidence = calculateSentimentConsistency(sentenceAnalyses);

        String label = mapScoreToLabel(averageScore);
        
        return new OverallSentiment(label, averageScore, confidence);
    }

    /**
     * Detects emotions using keyword analysis and sentiment context
     */
    private EmotionAnalysis detectEmotions(String text, List<SentenceAnalysis> sentenceAnalyses) {
        String lowerText = text.toLowerCase();
        Map<String, Double> emotionScores = new HashMap<>();

        // Initialize emotion scores
        for (String emotion : EMOTION_KEYWORDS.keySet()) {
            emotionScores.put(emotion, 0.0);
        }

        // Keyword-based emotion detection
        for (Map.Entry<String, Set<String>> entry : EMOTION_KEYWORDS.entrySet()) {
            String emotion = entry.getKey();
            Set<String> keywords = entry.getValue();
            
            double score = 0.0;
            for (String keyword : keywords) {
                if (lowerText.contains(keyword)) {
                    score += 1.0;
                }
            }
            emotionScores.put(emotion, score / keywords.size());
        }

        // Sentiment-based emotion enhancement
        double avgSentiment = sentenceAnalyses.stream()
            .mapToDouble(s -> s.normalizedScore)
            .average()
            .orElse(0.0);

        if (avgSentiment > 0.3) {
            emotionScores.put("JOY", emotionScores.get("JOY") + 0.3);
        } else if (avgSentiment < -0.3) {
            emotionScores.put("ANGER", emotionScores.get("ANGER") + 0.2);
            emotionScores.put("SADNESS", emotionScores.get("SADNESS") + 0.2);
        }

        // Find dominant emotion
        String dominantEmotion = emotionScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("NEUTRAL");

        double dominantScore = emotionScores.get(dominantEmotion);

        return new EmotionAnalysis(dominantEmotion, dominantScore, emotionScores);
    }

    /**
     * Analyzes intensity modifiers in the text
     */
    private IntensityAnalysis analyzeIntensity(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        int intensifierCount = 0;
        int diminisherCount = 0;

        for (String word : words) {
            if (INTENSIFIERS.contains(word)) {
                intensifierCount++;
            } else if (DIMINISHERS.contains(word)) {
                diminisherCount++;
            }
        }

        double intensityModifier = (intensifierCount * 0.2) - (diminisherCount * 0.15);
        String intensityLevel = determineIntensityLevel(intensityModifier);

        return new IntensityAnalysis(intensifierCount, diminisherCount, intensityModifier, intensityLevel);
    }

    /**
     * Extracts key phrases from a sentence using POS tagging
     */
    private List<String> extractKeyPhrases(CoreSentence sentence) {
        List<String> keyPhrases = new ArrayList<>();
        
        try {
            List<CoreLabel> tokens = sentence.tokens();
            for (int i = 0; i < tokens.size(); i++) {
                CoreLabel token = tokens.get(i);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String word = token.word().toLowerCase();
                
                // Extract adjectives and adverbs as key sentiment indicators
                if (pos != null && (pos.startsWith("JJ") || pos.startsWith("RB")) && word.length() > 2) {
                    keyPhrases.add(word);
                }
            }
        } catch (Exception e) {
            logger.warn("Error extracting key phrases from sentence", e);
        }
        
        return keyPhrases;
    }

    /**
     * Detects negation in a sentence
     */
    private boolean detectNegation(CoreSentence sentence) {
        String text = sentence.text().toLowerCase();
        String[] negationWords = {"not", "no", "never", "nothing", "nobody", "nowhere", 
                                 "neither", "nor", "none", "hardly", "scarcely", "barely"};
        
        for (String negation : negationWords) {
            if (text.contains(negation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets confidence score from sentiment tree
     */
    private double getConfidenceScore(Tree sentimentTree, int predictedClass) {
        try {
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
        } catch (Exception e) {
            logger.debug("Could not extract confidence score: {}", e.getMessage());
        }
        return 0.5; // Default confidence
    }

    /**
     * Maps Stanford sentiment values to labels
     */
    private String mapSentimentValue(int value) {
        switch (value) {
            case 0: return "VERY_NEGATIVE";
            case 1: return "NEGATIVE";
            case 2: return "NEUTRAL";
            case 3: return "POSITIVE";
            case 4: return "VERY_POSITIVE";
            default: return "NEUTRAL";
        }
    }

    /**
     * Normalizes sentiment score to -1 to 1 range
     */
    private double normalizeSentimentScore(int value) {
        return (value - 2.0) / 2.0;
    }

    /**
     * Maps normalized score to sentiment label
     */
    private String mapScoreToLabel(double score) {
        if (score > 0.5) return "VERY_POSITIVE";
        if (score > 0.1) return "POSITIVE";
        if (score < -0.5) return "VERY_NEGATIVE";
        if (score < -0.1) return "NEGATIVE";
        return "NEUTRAL";
    }

    /**
     * Calculates sentiment consistency across sentences
     */
    private double calculateSentimentConsistency(List<SentenceAnalysis> analyses) {
        if (analyses.size() <= 1) return 1.0;

        double variance = 0.0;
        double mean = analyses.stream().mapToDouble(a -> a.normalizedScore).average().orElse(0.0);
        
        for (SentenceAnalysis analysis : analyses) {
            variance += Math.pow(analysis.normalizedScore - mean, 2);
        }
        
        variance /= analyses.size();
        return Math.max(0.0, 1.0 - Math.sqrt(variance));
    }

    /**
     * Determines intensity level from modifier score
     */
    private String determineIntensityLevel(double modifier) {
        if (modifier > 0.3) return "HIGH";
        if (modifier > 0.1) return "MEDIUM";
        if (modifier < -0.2) return "LOW";
        return "NORMAL";
    }

    /**
     * Generates comprehensive analysis report
     */
    private String generateAnalysisReport(List<SentenceAnalysis> sentenceAnalyses, 
                                        OverallSentiment overallSentiment,
                                        EmotionAnalysis emotionAnalysis, 
                                        IntensityAnalysis intensityAnalysis) {
        StringBuilder report = new StringBuilder();
        
        report.append("Stanford CoreNLP Enhanced Analysis Report:\n");
        report.append(String.format("Overall Sentiment: %s (%.3f, confidence: %.3f)\n", 
                     overallSentiment.label, overallSentiment.score, overallSentiment.confidence));
        report.append(String.format("Dominant Emotion: %s (%.3f)\n", 
                     emotionAnalysis.dominantEmotion, emotionAnalysis.dominantScore));
        report.append(String.format("Intensity Level: %s (modifier: %.3f)\n", 
                     intensityAnalysis.level, intensityAnalysis.modifier));
        report.append(String.format("Sentences Analyzed: %d\n", sentenceAnalyses.size()));
        
        long negatedSentences = sentenceAnalyses.stream().mapToLong(s -> s.hasNegation ? 1 : 0).sum();
        if (negatedSentences > 0) {
            report.append(String.format("Negated Sentences: %d\n", negatedSentences));
        }
        
        return report.toString();
    }

    /**
     * Simplified sentiment analysis for backward compatibility
     */
    public String getSimplifiedSentiment(String text) {
        SentimentResult result = analyzeWithHybridApproach(text);
        return mapToSimpleSentiment(result.sentiment);
    }

    private String mapToSimpleSentiment(String sentiment) {
        if (sentiment.contains("POSITIVE")) return "POSITIVE";
        if (sentiment.contains("NEGATIVE")) return "NEGATIVE";
        return "NEUTRAL";
    }

    // Inner classes for structured results
    public static class SentimentResult {
        private final String sentiment;
        private final double score;
        private final String analysis;
        private final EnhancedAnalysisData enhancedData;
        private final boolean isEnhanced;

        public SentimentResult(String sentiment, double score, String analysis, 
                             EnhancedAnalysisData enhancedData, boolean isEnhanced) {
            this.sentiment = sentiment;
            this.score = score;
            this.analysis = analysis;
            this.enhancedData = enhancedData;
            this.isEnhanced = isEnhanced;
        }

        // Getters
        public String getSentiment() { return sentiment; }
        public double getScore() { return score; }
        public String getAnalysis() { return analysis; }
        public EnhancedAnalysisData getEnhancedData() { return enhancedData; }
        public boolean isEnhanced() { return isEnhanced; }
    }

    public static class SentenceAnalysis {
        private final String text;
        private final String sentiment;
        private final double normalizedScore;
        private final double confidence;
        private final int rawValue;
        private final List<String> keyPhrases;
        private final boolean hasNegation;

        public SentenceAnalysis(String text, String sentiment, double normalizedScore, 
                              double confidence, int rawValue, List<String> keyPhrases, boolean hasNegation) {
            this.text = text;
            this.sentiment = sentiment;
            this.normalizedScore = normalizedScore;
            this.confidence = confidence;
            this.rawValue = rawValue;
            this.keyPhrases = keyPhrases;
            this.hasNegation = hasNegation;
        }

        // Getters
        public String getText() { return text; }
        public String getSentiment() { return sentiment; }
        public double getNormalizedScore() { return normalizedScore; }
        public double getConfidence() { return confidence; }
        public int getRawValue() { return rawValue; }
        public List<String> getKeyPhrases() { return keyPhrases; }
        public boolean isHasNegation() { return hasNegation; }
    }

    public static class OverallSentiment {
        private final String label;
        private final double score;
        private final double confidence;

        public OverallSentiment(String label, double score, double confidence) {
            this.label = label;
            this.score = score;
            this.confidence = confidence;
        }

        // Getters
        public String getLabel() { return label; }
        public double getScore() { return score; }
        public double getConfidence() { return confidence; }
    }

    public static class EmotionAnalysis {
        private final String dominantEmotion;
        private final double dominantScore;
        private final Map<String, Double> emotionScores;

        public EmotionAnalysis(String dominantEmotion, double dominantScore, Map<String, Double> emotionScores) {
            this.dominantEmotion = dominantEmotion;
            this.dominantScore = dominantScore;
            this.emotionScores = emotionScores;
        }

        // Getters
        public String getDominantEmotion() { return dominantEmotion; }
        public double getDominantScore() { return dominantScore; }
        public Map<String, Double> getEmotionScores() { return emotionScores; }
    }

    public static class IntensityAnalysis {
        private final int intensifierCount;
        private final int diminisherCount;
        private final double modifier;
        private final String level;

        public IntensityAnalysis(int intensifierCount, int diminisherCount, double modifier, String level) {
            this.intensifierCount = intensifierCount;
            this.diminisherCount = diminisherCount;
            this.modifier = modifier;
            this.level = level;
        }

        // Getters
        public int getIntensifierCount() { return intensifierCount; }
        public int getDiminisherCount() { return diminisherCount; }
        public double getModifier() { return modifier; }
        public String getLevel() { return level; }
    }

    public static class EnhancedAnalysisData {
        private final List<SentenceAnalysis> sentenceAnalyses;
        private final EmotionAnalysis emotionAnalysis;
        private final IntensityAnalysis intensityAnalysis;

        public EnhancedAnalysisData(List<SentenceAnalysis> sentenceAnalyses, 
                                  EmotionAnalysis emotionAnalysis, IntensityAnalysis intensityAnalysis) {
            this.sentenceAnalyses = sentenceAnalyses;
            this.emotionAnalysis = emotionAnalysis;
            this.intensityAnalysis = intensityAnalysis;
        }

        // Getters
        public List<SentenceAnalysis> getSentenceAnalyses() { return sentenceAnalyses; }
        public EmotionAnalysis getEmotionAnalysis() { return emotionAnalysis; }
        public IntensityAnalysis getIntensityAnalysis() { return intensityAnalysis; }
    }
}