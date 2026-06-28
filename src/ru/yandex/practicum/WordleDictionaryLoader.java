package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class WordleDictionaryLoader {

    public WordleDictionary loadDictionary(String filePath, PrintWriter logger) throws IOException {
        Set<String> words = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase().replace('ё', 'е');

                if (line.length() == 5 && line.chars().allMatch(ch -> (ch >= 'а' && ch <= 'я') || ch == 'ё')) {
                    words.add(line);
                } else if (!line.isEmpty()) {
                    logger.println("Пропущено слово из словаря: " + line);
                }
            }
        } catch (IOException e) {
            logger.println("Ошибка чтения файла словаря: " + e.getMessage());
            throw e;
        }

        if (words.isEmpty()) {
            throw new IOException("Словарь пуст или не содержит слов из 5 букв.");
        }

        logger.println("Словарь загружен. Найдено слов: " + words.size());
        return new WordleDictionary(words);
    }
}