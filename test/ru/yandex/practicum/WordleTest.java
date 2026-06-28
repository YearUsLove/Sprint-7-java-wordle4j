package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleDictionary dictionary;
    private PrintWriter logger;

    @BeforeEach
    void setUp() {
        logger = new PrintWriter(System.out);
        Set<String> wordSet = new HashSet<>(Arrays.asList("герой", "гонец", "слово", "буква", "право", "ответ", "точка", "наука", "арбуз"));
        dictionary = new WordleDictionary(wordSet);
    }

    @Test
    void testCheckWordAllCorrect() {
        assertEquals("+++++", WordleDictionary.checkWord("герой", "герой"));
    }

    @Test
    void testCheckWordAllWrong() {
        assertEquals("-----", WordleDictionary.checkWord("герой", "наука"));
    }

    @Test
    void testCheckWordMixed() {
        assertEquals("+^-^-", WordleDictionary.checkWord("герой", "гонец"));
    }

    @Test
    void testCheckWordDuplicateLetters() {
        assertEquals("+^--^", WordleDictionary.checkWord("арбуз", "абажу"));
    }

    @Test
    void testDictionaryContains() {
        assertTrue(dictionary.contains("герой"));
        assertFalse(dictionary.contains("абвгд"));
    }

    @Test
    void testDictionaryGetRandomWord() {
        String word = dictionary.getRandomWord();
        assertNotNull(word);
        assertEquals(5, word.length());
    }

    @Test
    void testGameWin() throws WordleGame.WordleException {
        WordleGame game = new WordleGame(dictionary, logger, "герой");
        String hint = game.makeMove("герой");
        assertEquals("+++++", hint);
        assertTrue(game.isWin());
        assertTrue(game.isGameOver());
    }

    @Test
    void testGameLose() throws WordleGame.WordleException {
        Set<String> smallSet = new HashSet<>(Arrays.asList("герой", "гонец", "слово", "буква", "право", "ответ"));
        WordleDictionary smallDict = new WordleDictionary(smallSet);
        WordleGame game = new WordleGame(smallDict, logger, "герой");
        game.makeMove("гонец");
        game.makeMove("слово");
        game.makeMove("буква");
        game.makeMove("право");
        game.makeMove("ответ");
        assertFalse(game.isWin());
        assertFalse(game.isGameOver());
        assertEquals(1, game.getSteps());
    }

    @Test
    void testWordNotFound() {
        WordleGame game = new WordleGame(dictionary, logger);
        assertThrows(WordleGame.WordNotFoundExceptionInDictionary.class, () -> game.makeMove("абвгд"));
    }

    @Test
    void testFindHintWords() {
        List<String> guesses = Arrays.asList("гонец");
        List<String> hints = Arrays.asList("+^-^-");
        List<String> result = dictionary.findHintWords(guesses, hints);
        assertTrue(result.contains("герой"));
        assertFalse(result.contains("гонец"));
        assertFalse(result.contains("слово"));
    }

    @Test
    void testHintExcludesUsed() throws WordleGame.WordleException {
        Set<String> testSet = new HashSet<>(Arrays.asList("герой", "гонец", "горец", "гость", "горал", "гобой", "голем", "гольф", "гогот", "годок", "горец", "гость"));
        WordleDictionary testDict = new WordleDictionary(testSet);
        WordleGame game = new WordleGame(testDict, logger, "герой");
        String hint1 = game.getHint();
        assertNotNull(hint1);
        String hint2 = game.getHint();
        assertNotNull(hint2);
        assertNotEquals(hint1, hint2);
    }
}