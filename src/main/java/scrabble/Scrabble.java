package scrabble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.containsAny;
import static scrabble.TemplateGenerator.TemplateType.BROADENING;
import static scrabble.TemplateGenerator.TemplateType.SAME_SIZE;

public class Scrabble {

    private static final String fileURL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";
    private static long startTime;

    public static void main(String[] args) {
        List<String> result = new Scrabble().findNineLetterWords();
        long endTime = Instant.now().toEpochMilli();
        System.out.println(result.stream().sorted().collect(Collectors.joining("\n", "", "\n---------")));
        System.out.println(result.size() + " words found in " + (endTime - startTime) + " ms");
    }

    public WordCollector readWords() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(fileURL).openStream(), StandardCharsets.US_ASCII))) {
            List<String> allLines = reader.lines().toList();
            startTime = Instant.now().toEpochMilli();
            return allLines.stream().skip(2).filter(this::matchesRequirements).collect(new WordCollector());
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + fileURL);
        } catch (IOException e) {
            System.out.println("Failed to open URL: " + fileURL);
        }
        return new WordCollector();
    }

    private boolean matchesRequirements(String word) {
        return word.length() < 10 && containsAny(word, 'A', 'I');
    }

    public List<String> findNineLetterWords() {
        WordCollector words = this.readWords();
        TemplateGenerator generator = new TemplateGenerator();
        Map<String, Set<String>> broadeningTemplates = generator.generateTemplates(words.getWordsOfLength(1), BROADENING);
        List<String> iLetterWords = null;
        for (int i = 2; i < 10; i++) {
            Map<String,Set<String>> sameSizeTemplates = generator.generateIntersectingTemplates(words.getWordsOfLength(i), SAME_SIZE, broadeningTemplates.keySet());
            words.clearWordsOfLength(i);
            iLetterWords = sameSizeTemplates.values().stream().flatMap(Set::stream).toList();
            broadeningTemplates.clear();
            if (i < 9) {
                broadeningTemplates = generator.generateTemplates(iLetterWords, BROADENING);
                iLetterWords = null;
            }
        }
        return iLetterWords;
    }
}