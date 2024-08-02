package scrabble;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.countMatches;
import static scrabble.TemplateGenerator.TemplateType.SAME_SIZE;

public final class TemplateGenerator {

    public enum TemplateType {
        SAME_SIZE, BROADENING
    }

    public Set<String> sameSizeTemplates(String word) {
        boolean skip = (countMatches(word, 'A') + countMatches(word, 'I')) < 2;
        byte [] wordBytes = word.getBytes(StandardCharsets.US_ASCII);
        Set<String> templates = new HashSet<>();
        for (int i = 0; i < wordBytes.length; i++) {
            if (!skip || (wordBytes[i] != 65 && wordBytes[i] != 73)) {
                byte[] templateBytes = ArrayUtils.clone(wordBytes);
                templateBytes[i] = 42;
                templates.add(new String(templateBytes));
            }
        }
        return templates;
    }

    public Set<String> broadeningTemplates(String word) {
        byte [] wordBytes = word.getBytes(StandardCharsets.US_ASCII);
        int wordLength = wordBytes.length;
        Set<String> templates = new HashSet<>();
        byte [] a = new byte[wordLength + 1];
        a[0] = 42;
        System.arraycopy(wordBytes, 0, a, 1, wordLength);
        templates.add(new String(a));
        for (int i = 1; i < wordLength; i++) {
            byte [] templateBytes = new byte[wordLength + 1];
            System.arraycopy(wordBytes, 0, templateBytes, 0, i);
            templateBytes[i] = 42;
            System.arraycopy(wordBytes, i, templateBytes, i + 1, wordLength - i);
            templates.add(new String(templateBytes));
        }
        byte [] b = new byte[wordLength + 1];
        b[wordLength] = 42;
        System.arraycopy(wordBytes, 0, b, 0, wordLength);
        templates.add(new String(b));
        return templates;
    }

    public Map<String,Set<String>> generateTemplates(Set<String> words, TemplateType type) {
        Map<String,Set<String>> result = new HashMap<>();
        for (String word : words)
            for (String template : type == SAME_SIZE ? this.sameSizeTemplates(word) : this.broadeningTemplates(word))
                result.computeIfAbsent(template, k -> new HashSet<>()).add(word);
        return result;
    }

    public Map<String,Set<String>> generateIntersectingTemplates(Set<String> words, TemplateType type, Collection<String> collection) {
        Map<String,Set<String>> result = new HashMap<>();
        for (String word : words)
            for (String template : type == SAME_SIZE ? this.sameSizeTemplates(word) : this.broadeningTemplates(word))
                if (collection.contains(template))
                    result.computeIfAbsent(template, k -> new HashSet<>()).add(word);
        return result;
    }
}
