package com.aif.language.sentence;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.aif.cli.common.FileHelper;
import com.aif.language.sentence.separators.clasificators.ISentenceSeparatorGroupsClassificatory;
import com.aif.language.sentence.separators.extractors.ISentenceSeparatorExtractor;
import com.aif.language.sentence.separators.groupers.ISentenceSeparatorsGrouper;
import com.aif.language.token.TokenSplitter;

class SentencesSeparatorExtractorCommand extends BasicTextCommand {

    private static final String PRINT_CHARACTERS_IN_GROUP    = "Group: \'%s\', characters: %s\n";

    private static final String NO_SEPARATOR_WERE_FOUND    = "No separators were found";

    @Override
    public Void apply(final String... args) {
        final String text;
        try {
            text = FileHelper.readAllTextFromFile(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        final TokenSplitter tokenSplitter = new TokenSplitter();
        final ISentenceSeparatorExtractor separatorExtractor = ISentenceSeparatorExtractor.Type.PROBABILITY.getInstance();
        ISentenceSeparatorsGrouper separatorsGrouper = ISentenceSeparatorsGrouper.Type.PROBABILITY.getInstance();
        ISentenceSeparatorGroupsClassificatory sentenceSeparatorGroupsClassificatory = ISentenceSeparatorGroupsClassificatory.Type.PROBABILITY.getInstance();

        final List<String> tokens = tokenSplitter.split(text);
        final Optional<List<Character>> optSeparators = separatorExtractor.extract(tokens);
        if (!optSeparators.isPresent()) {
            System.out.println(NO_SEPARATOR_WERE_FOUND);
            return null;
        }
        final List<Character> separators = optSeparators.get();

        final List<Set<Character>> separatorsGroupsUnclasify = separatorsGrouper.group(tokens, separators);
        final Map<ISentenceSeparatorGroupsClassificatory.Group, Set<Character>> separatorsGroups = sentenceSeparatorGroupsClassificatory.classify(tokens, separatorsGroupsUnclasify);
        separatorsGroups.keySet().forEach(key -> System.out.printf(PRINT_CHARACTERS_IN_GROUP, key, toString(separatorsGroups.get(key))));
        return null;
    }

    private static String toString(final Set<Character> characters) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        characters.forEach(ch -> stringBuilder.append(ch + " "));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

}
