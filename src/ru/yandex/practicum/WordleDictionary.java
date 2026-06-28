package ru.yandex.practicum;

import java.util.*;

public class WordleDictionary {

    private final List<String> words;
    private final Random random = new Random();

    public WordleDictionary(Set<String> wordSet) {
        this.words = new ArrayList<>(wordSet);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }

    public List<String> findHintWords(List<String> previousGuesses, List<String> previousHints) {
        Set<Character> wrongLetters = new HashSet<>();
        Map<Integer, Character> exactMatches = new HashMap<>();
        Map<Character, Integer> minLetterCounts = new HashMap<>();
        Map<Character, Set<Integer>> wrongPositions = new HashMap<>();

        for (int i = 0; i < previousGuesses.size(); i++) {
            String guess = previousGuesses.get(i);
            String hint = previousHints.get(i);

            Map<Character, Integer> exactCountInGuess = new HashMap<>();
            Map<Character, Integer> presentCountInGuess = new HashMap<>();

            for (int j = 0; j < hint.length(); j++) {
                char guessChar = guess.charAt(j);
                char hintChar = hint.charAt(j);

                if (hintChar == '+') {
                    exactMatches.put(j, guessChar);
                    exactCountInGuess.merge(guessChar, 1, Integer::sum);
                    wrongLetters.remove(guessChar);
                } else if (hintChar == '^') {
                    wrongPositions.computeIfAbsent(guessChar, k -> new HashSet<>()).add(j);
                    presentCountInGuess.merge(guessChar, 1, Integer::sum);
                    wrongLetters.remove(guessChar);
                }
            }

            for (int j = 0; j < hint.length(); j++) {
                char guessChar = guess.charAt(j);
                char hintChar = hint.charAt(j);

                if (hintChar == '-') {
                    int exactCount = exactCountInGuess.getOrDefault(guessChar, 0);
                    int presentCount = presentCountInGuess.getOrDefault(guessChar, 0);
                    int totalInWord = exactCount + presentCount;

                    int totalInGuess = 0;
                    for (int k = 0; k < guess.length(); k++) {
                        if (guess.charAt(k) == guessChar) totalInGuess++;
                    }

                    if (totalInGuess > totalInWord) {
                        minLetterCounts.put(guessChar, totalInWord);
                    }

                    if (totalInWord == 0) {
                        wrongLetters.add(guessChar);
                    }
                }
            }

            for (Map.Entry<Character, Integer> entry : exactCountInGuess.entrySet()) {
                minLetterCounts.merge(entry.getKey(), entry.getValue(), Math::max);
            }
            for (Map.Entry<Character, Integer> entry : presentCountInGuess.entrySet()) {
                char c = entry.getKey();
                int presentCount = entry.getValue();
                int exactCount = exactCountInGuess.getOrDefault(c, 0);
                minLetterCounts.merge(c, exactCount + presentCount, Math::max);
            }
        }

        List<String> result = new ArrayList<>();
        for (String word : words) {
            boolean valid = true;

            for (char c : wrongLetters) {
                if (word.indexOf(c) != -1) {
                    valid = false;
                    break;
                }
            }
            if (!valid) continue;

            for (Map.Entry<Integer, Character> entry : exactMatches.entrySet()) {
                if (word.charAt(entry.getKey()) != entry.getValue()) {
                    valid = false;
                    break;
                }
            }
            if (!valid) continue;

            for (Map.Entry<Character, Integer> entry : minLetterCounts.entrySet()) {
                long count = word.chars().filter(ch -> ch == entry.getKey()).count();
                if (count < entry.getValue()) {
                    valid = false;
                    break;
                }
            }
            if (!valid) continue;

            for (Map.Entry<Character, Set<Integer>> entry : wrongPositions.entrySet()) {
                for (int pos : entry.getValue()) {
                    if (word.charAt(pos) == entry.getKey()) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) break;
            }
            if (!valid) continue;

            result.add(word);
        }
        return result;
    }

    public static String checkWord(String answer, String guess) {
        StringBuilder result = new StringBuilder("-----");
        char[] answerChars = answer.toCharArray();
        char[] guessChars = guess.toCharArray();
        boolean[] matched = new boolean[5];

        for (int i = 0; i < 5; i++) {
            if (guessChars[i] == answerChars[i]) {
                result.setCharAt(i, '+');
                matched[i] = true;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result.charAt(i) == '+') continue;
            for (int j = 0; j < 5; j++) {
                if (!matched[j] && guessChars[i] == answerChars[j]) {
                    result.setCharAt(i, '^');
                    matched[j] = true;
                    break;
                }
            }
        }
        return result.toString();
    }
}