package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final PrintWriter logger;
    private final List<String> guesses;
    private final List<String> hints;
    private final Set<String> usedHints;
    private boolean gameOver;
    private boolean win;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.dictionary = dictionary;
        this.logger = logger;
        this.answer = dictionary.getRandomWord();
        this.steps = 6;
        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.usedHints = new HashSet<>();
        this.gameOver = false;
        this.win = false;
        logger.println("Начало игры. Загадано: " + answer);
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter logger, String answer) {
        this.dictionary = dictionary;
        this.logger = logger;
        this.answer = answer;
        this.steps = 6;
        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.usedHints = new HashSet<>();
        this.gameOver = false;
        this.win = false;
        logger.println("Начало игры. Загадано: " + answer);
    }

    public int getSteps() {
        return steps;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWin() {
        return win;
    }

    public String getAnswer() {
        return answer;
    }

    public String makeMove(String guess) throws WordleException {
        if (gameOver) {
            throw new WordleException("Игра завершена.");
        }

        if (!dictionary.contains(guess)) {
            throw new WordNotFoundExceptionInDictionary("Слово не найдено: " + guess);
        }

        String hint = WordleDictionary.checkWord(answer, guess);
        steps--;
        guesses.add(guess);
        hints.add(hint);

        logger.println("Ход " + (6 - steps) + ": " + guess + " -> " + hint);

        if (guess.equals(answer)) {
            win = true;
            gameOver = true;
            logger.println("Победа.");
        } else if (steps == 0) {
            gameOver = true;
            logger.println("Поражение.");
        }

        return hint;
    }

    public String getHint() throws WordleException {
        if (gameOver) {
            throw new WordleException("Игра завершена.");
        }

        List<String> candidates = dictionary.findHintWords(guesses, hints);
        candidates.removeIf(w -> usedHints.contains(w) || w.equals(answer));

        if (candidates.isEmpty()) {
            throw new WordleException("Нет подходящих слов.");
        }

        String hint = candidates.get(new Random().nextInt(candidates.size()));
        usedHints.add(hint);
        logger.println("Подсказка: " + hint);
        return hint;
    }

    public static class WordleException extends Exception {
        public WordleException(String message) {
            super(message);
        }
    }

    public static class WordNotFoundExceptionInDictionary extends WordleException {
        public WordNotFoundExceptionInDictionary(String message) {
            super(message);
        }
    }
}