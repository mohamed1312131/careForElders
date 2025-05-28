export interface Comment {
  id: string;
  content: string;
  authorId: string;
  authorName: string;
  postId: string;
  parentCommentId?: string;
  createdAt: Date;
  updatedAt?: Date;
  likes: CommentLike[];
  replies?: Comment[];
  isDeleted?: boolean;
  
  // Sentiment analysis fields
  sentiment?: string;
  sentimentScore?: number;
  sentimentConfidence?: number;
  emotions?: EmotionAnalysis[];
  keywords?: string[];
  entities?: EntityAnalysis[];
  toxicity?: ToxicityAnalysis;
  
  // Content analysis fields
  wordCount?: number;
  characterCount?: number;
  readingTimeSeconds?: number;
  readabilityScore?: number;
  languageDetection?: LanguageDetection;
  
  // Engagement fields
  engagementScore?: number;
  qualityScore?: number;
  
  // Moderation fields
  isModerated?: boolean;
  moderationFlags?: string[];
  
  // Additional metadata
  editCount?: number;
  lastEditedAt?: Date;
  ipAddress?: string;
  userAgent?: string;
}

export interface CommentLike {
  id: string;
  userId: string;
  createdAt: Date;
}

export interface CommentRequest {
  content: string;
  postId: string;
  userId: string;
  //username:string;
  parentCommentId?: string;
}

export interface CommentResponse {
  content: Comment[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Keep all the existing sentiment analysis interfaces...
export interface SentimentAnalysisResult {
  sentiment: string;
  score: number;
  confidence: number;
  magnitude: number;
  emotions?: EmotionAnalysis[];
  keywords?: string[];
  entities?: EntityAnalysis[];
  toxicity?: ToxicityAnalysis;
  analysisTimestamp: Date;
  processingTimeMs: number;
}

export interface EmotionAnalysis {
  emotion: string;
  score: number;
  confidence: number;
}

export interface EntityAnalysis {
  name: string;
  type: string;
  salience: number;
  sentiment?: {
    score: number;
    magnitude: number;
  };
  mentions: EntityMention[];
}

export interface EntityMention {
  text: string;
  type: string;
  beginOffset: number;
  endOffset: number;
}

export interface ToxicityAnalysis {
  overallToxicity: number;
  categories: ToxicityCategory[];
  isToxic: boolean;
  confidence: number;
}

export interface ToxicityCategory {
  name: string;
  score: number;
  threshold: number;
}

export interface LanguageDetection {
  language: string;
  confidence: number;
  isReliable: boolean;
}

export interface ComprehensiveAnalysis {
  id: string;
  textAnalyzed: string;
  analysisTimestamp: Date;
  processingTimeMs: number;
  sentimentAnalysis: SentimentAnalysisResult;
  emotionAnalysis: {
    primaryEmotion: string;
    emotionDistribution: EmotionAnalysis[];
    emotionalIntensity: number;
    emotionalStability: number;
  };
  contentAnalysis: {
    wordCount: number;
    characterCount: number;
    sentenceCount: number;
    averageWordsPerSentence: number;
    readabilityScore: number;
    readabilityLevel: string;
    complexity: number;
  };
  linguisticAnalysis: {
    languageDetection: LanguageDetection;
    partOfSpeechTags: PartOfSpeechTag[];
    syntaxAnalysis: SyntaxAnalysis;
    dependencyParsing: DependencyRelation[];
  };
  entityAnalysis: {
    entities: EntityAnalysis[];
    entityCount: number;
    entityDensity: number;
  };
  keywordAnalysis: {
    keywords: KeywordAnalysis[];
    keywordDensity: number;
    topicCategories: string[];
  };
  moderationAnalysis: {
    toxicity: ToxicityAnalysis;
    spam: SpamAnalysis;
    profanity: ProfanityAnalysis;
    personalInfo: PersonalInfoAnalysis;
  };
  engagementPrediction: {
    likeabilityScore: number;
    controversyScore: number;
    engagementPotential: number;
    viralityScore: number;
  };
  summary: {
    overallScore: number;
    qualityScore: number;
    appropriatenessScore: number;
    recommendations: string[];
    flags: AnalysisFlag[];
  };
}

// Supporting interfaces
export interface PartOfSpeechTag {
  word: string;
  tag: string;
  lemma: string;
  beginOffset: number;
  endOffset: number;
}

export interface SyntaxAnalysis {
  sentences: SentenceAnalysis[];
  complexity: number;
  structure: string;
}

export interface SentenceAnalysis {
  text: string;
  sentiment: {
    score: number;
    magnitude: number;
  };
  beginOffset: number;
  endOffset: number;
}

export interface DependencyRelation {
  headTokenIndex: number;
  dependentTokenIndex: number;
  label: string;
}

export interface KeywordAnalysis {
  keyword: string;
  relevance: number;
  frequency: number;
  sentiment: {
    score: number;
    magnitude: number;
  };
  category?: string;
}

export interface SpamAnalysis {
  isSpam: boolean;
  spamScore: number;
  confidence: number;
  reasons: string[];
}

export interface ProfanityAnalysis {
  containsProfanity: boolean;
  profanityScore: number;
  profaneWords: string[];
  severity: string;
}

export interface PersonalInfoAnalysis {
  containsPersonalInfo: boolean;
  infoTypes: PersonalInfoType[];
  riskLevel: string;
}

export interface PersonalInfoType {
  type: string;
  confidence: number;
  maskedValue?: string;
}

export interface AnalysisFlag {
  type: string;
  category: string;
  message: string;
  severity: string;
  recommendation?: string;
}