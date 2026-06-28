package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter logger = new PrintWriter("wordle_game.log", "UTF-8")) {
            logger.println("=== Запуск игры Wordle ===");

            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary;

            try {
                dictionary = loader.loadDictionary("russian_nouns.txt", logger);
            } catch (IOException e) {
                logger.println("Критическая ошибка при загрузке словаря: " + e.getMessage());
                System.out.println("Критическая ошибка при загрузке словаря. Подробности в лог-файле.");
                return;
            }

            WordleGame game = new WordleGame(dictionary, logger);
            Scanner scanner = new Scanner(System.in);

            System.out.println("Добро пожаловать в Wordle! У вас 6 попыток угадать слово из 5 букв.");
            System.out.println("Нажмите Enter на пустой строке, чтобы получить подсказку.\n");

            while (!game.isGameOver()) {
                System.out.println("Осталось попыток: " + game.getSteps());
                System.out.print("> ");

                String input = scanner.nextLine().trim().toLowerCase().replace('ё', 'е');

                try {
                    if (input.isEmpty()) {
                        try {
                            String hint = game.getHint();
                            System.out.println("Подсказка компьютера: " + hint);
                        } catch (WordleGame.WordleException e) {
                            System.out.println(e.getMessage());
                            logger.println("Ошибка подсказки: " + e.getMessage());
                        }
                        continue;
                    }

                    if (input.length() != 5 || !input.chars().allMatch(ch -> (ch >= 'а' && ch <= 'я') || ch == 'ё')) {
                        System.out.println("Слово должно состоять ровно из 5 русских букв. Попробуйте снова.");
                        continue;
                    }

                    String hint = game.makeMove(input);
                    System.out.println(hint);

                } catch (WordleGame.WordNotFoundExceptionInDictionary e) {
                    System.out.println("Такого слова нет в словаре. Попробуйте другое.");
                } catch (WordleGame.WordleException e) {
                    System.out.println("Ошибка игры: " + e.getMessage());
                    logger.println("Ошибка игровой логики: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Произошла непредвиденная ошибка. Подробности в лог-файле.");
                    logger.println("Непредвиденная ошибка в игровом цикле: " + e.getMessage());
                    e.printStackTrace(logger);
                }
            }

            if (game.isWin()) {
                System.out.println("Поздравляем! Вы угадали слово '" + game.getAnswer() + "'!");
            } else {
                System.out.println("К сожалению, вы проиграли. Загаданное слово было: " + game.getAnswer());
            }
            logger.println("=== Игра завершена ===");

        } catch (IOException e) {
            System.out.println("Не удалось создать лог-файл: " + e.getMessage());
        }
    }
}